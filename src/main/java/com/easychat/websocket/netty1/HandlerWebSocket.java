package com.easychat.websocket.netty1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    /*
    *
    * 通道就绪后调用，一般做初始化
    * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的连接加入");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的连接已断开");
    }

    /*
    *
    * 读消息
    * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel=ctx.channel();
        log.info("收到消息{}",textWebSocketFrame.text());
    }

}
