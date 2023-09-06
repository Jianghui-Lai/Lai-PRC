package Yin.rpc.cousumer.handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Yin.rpc.cousumer.core.ResultFuture;
import Yin.rpc.cousumer.param.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {
	private static final Executor exec = Executors.newFixedThreadPool(10);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		final Object m = msg;
		if(msg.toString().equals("ping")){
			System.out.println("receive READER_Idle and WRITER_IDLEping,send pong to the server");

			ctx.channel().writeAndFlush("pong\r\n");
		}
		
		//设置response
//		final Response response = JSONObject.parseObject(msg.toString(), Response.class);
//		System.out.println("SimpleClientHandler中的Response:"+JSONObject.toJSONString(response));
		exec.execute(new Runnable() {
			
			public void run() {
				Response response = JSONObject.parseObject(m.toString(), Response.class);
				System.out.println("Response in SimpleClientHandler:"+JSONObject.toJSONString(response));
				ResultFuture.receive(response);				
			}
		});
//		ResultFuture.receive(response);//find the corresponding request in the map using the ID of response and set 
//		the response for the respective request,allowing the 'get()' method on the client side to obtain the result
	 
	}
	
}
