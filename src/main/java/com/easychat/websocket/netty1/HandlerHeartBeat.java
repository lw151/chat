package com.easychat.websocket.netty1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/*
*
* 超时处理器
* */
@Slf4j
public class HandlerHeartBeat extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if(e.state()== IdleState.READER_IDLE){
                log.info("心跳超时");
                ctx.close();
            }
            else if(e.state()==IdleState.READER_IDLE){
                ctx.writeAndFlush("heart");
            }
        }

    }
}
