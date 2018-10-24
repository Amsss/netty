package com.zhuzz.time;

import com.zhuzz.discard.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018-10-09 10:56
 */
public class TimeServer {
    private int port;

    public TimeServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // NioEventLoopGroup是处理I/O操作的多线程事件循环
        // Netty为不同类型的传输提供了各种EventLoopGroup实现
        // 第一个通常称为“boss”，接受传入连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 第二个通常称为“worker”，当“boss”接受连接并且向“worker”注册接受连接，则“worker”处理所接受连接的流量
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // 该类用于实例化新的通道以接受传入连接。
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                        //用于帮助用户配置新的通道
                        new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // ChannelPipeline
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8081;
        }
        new TimeServer(port).run();
    }
}
