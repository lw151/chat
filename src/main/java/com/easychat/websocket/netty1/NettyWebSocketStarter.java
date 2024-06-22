package com.easychat.websocket.netty1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyWebSocketStarter {

    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private static EventLoopGroup workGroup = new NioEventLoopGroup();

    public static void main(String[] args) {

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            //设置几个重要的处理器
                            //使用http的编码器，解码器
                            pipeline.addLast(new HttpServerCodec());
                            //聚合解码 httpRequest/httpContent/lastHttpContent
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //心跳
                            //readeIdleTime 读超时时间
                            //writerIdleTime 为写超时时间
                            //allIdleTime 所有类型超时时间
                            pipeline.addLast(new IdleStateHandler(6, 0
                                    , 0, TimeUnit.SECONDS));

                            pipeline.addLast(new HandlerHeartBeat());
                            //将http协议升级为ws协议，对websocket支持
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new HandlerWebSocket());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(5044).sync();
            log.info("netty启动成功");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("启动netty失败{}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}
