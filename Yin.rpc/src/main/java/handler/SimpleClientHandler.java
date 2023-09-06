package handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import future.ResultFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import model.Response;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter implements ChannelHandler {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg.toString().equals("ping")){
			System.out.println("receive READ_IDLE or WRITE_IDLE ping,向服务端发送pong");
			ctx.channel().writeAndFlush("pong\r\n");
		}
		
		//configure response
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		ResultFuture.receive(response);
//	Locate the corresponding request in the map using the ID of the response and assign the response to the respective request, enabling the client to retrieve the result by invoking 'get()'
	}
	
}
