package handler;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import future.ResultFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import medium.Medium;
import model.Response;
import model.ServerRequest;

public class ServerHandler extends ChannelInboundHandlerAdapter  {
	private static final Executor exec = Executors.newFixedThreadPool(10);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Server Handler:"+msg.toString());
//		ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
//		System.out.println(serverRequest.getCommand());
		exec.execute(new Runnable() {
			
			@Override
			public void run() {
				ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
				System.out.println(serverRequest.getCommand());
				Medium medium = Medium.newInstance();//medium
				
				Response response = medium.process(serverRequest);
				
				//send Response to the client 
				ctx.channel().writeAndFlush(JSONObject.toJSONString(response)+"\r\n");
			}
		});
//		Medium medium = Medium.newInstance();
//		
//		Response response = medium.process(serverRequest);
//		
//		//send Response to the client 
//		ctx.channel().writeAndFlush(JSONObject.toJSONString(response)+"\r\n");
		
	}

//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		
//		if(evt instanceof IdleStateEvent){
//			IdleStateEvent event = (IdleStateEvent)evt;
//			
//			if(event.state().equals(IdleState.READER_IDLE)){
//				System.out.println("read idle");
//			}
//			if(event.state().equals(IdleState.WRITER_IDLE)){
//				System.out.println("write idle");
//			}
//			if(event.state().equals(IdleState.ALL_IDLE)){
//				System.out.println("read&write idle");
//				ctx.channel().writeAndFlush("ping\r\n");
//			}
//		}
//	}
	
	
	
}
