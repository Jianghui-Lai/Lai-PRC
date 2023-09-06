package Yin.rpc.cousumer.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Yin.rpc.cousumer.param.ClientRequest;
import Yin.rpc.cousumer.param.Response;



public class ResultFuture {
	public final static ConcurrentHashMap<Long,ResultFuture> map = new ConcurrentHashMap<Long,ResultFuture>();
	final  Lock lock = new ReentrantLock();//Modify
	private Condition condition = lock.newCondition();
	private Response response;
	private Long timeOut = 2*60*1000l;
	private Long start = System.currentTimeMillis();
	
	
	public ResultFuture(ClientRequest request){
		map.put(request.getId(), this);
	}
	
	public Response get(){
//		System.out.println("lock information of get:"+lock);
		lock.lock();
		
		try {

			while(!done()){
				condition.await();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
			System.out.println(Thread.currentThread().getName() + "   release the lock at get!");
		}
		
		return this.response;
	}
	
	public Response get(Long time){
		lock.lock();
//		System.out.println("get的锁信息:"+lock);
		try {
			while(!done()){
				condition.await(time,TimeUnit.MILLISECONDS);
				if((System.currentTimeMillis()-start)>time){
//					System.out.println("Future request time out        "
//							+ "                                                                                                                                                                                                               ");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
//			System.out.println(Thread.currentThread().getName() + "   release the lock at get！");
		}
		
		return this.response;
		
	}
	
	public static void receive(Response response){
		if(response != null){
			ResultFuture future = map.get(response.getId());
			if(future != null){
				Lock lock = future.lock;
//				System.out.println("Receive the info of lock:"+lock);
				lock.lock();
				try {
					future.setResponse(response);
					future.condition.signal();
					map.remove(future);//Remember remove
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					lock.unlock();
//					System.out.println(Thread.currentThread().getName() + "   receive处释放锁！");
				}
			}

		}
		
	} 

	private boolean done() {
		if(this.response != null){
			return true;
		}
		return false;
	}

	public Long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}

	public Long getStart() {
		return start;
	}


	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	//clear the threads
	static class ClearFutureThread extends Thread{
		@Override
		public void run() {
			Set<Long> ids = map.keySet();
			for(Long id : ids){
				ResultFuture f = map.get(id);
				if(f==null){
					map.remove(f);
				}else if(f.getTimeOut()<(System.currentTimeMillis()-f.getStart()))
				{//链路超时
					Response res = new Response();
					res.setId(id);
					res.setCode("33333");
					res.setMsg("Link time out");
					receive(res);
				}
			}
		}
	}
	
	static{
		ClearFutureThread clearThread = new ClearFutureThread();
		clearThread.setDaemon(true);
		clearThread.start();
	}
	
	
	
}
