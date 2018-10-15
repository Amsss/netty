package com.zhuzz.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    //指定所使用的NIO传输channel
                .channel(NioServerSocketChannel.class)
                    //使用指定的端口设置套接字地址
                .localAddress(new InetSocketAddress(port))
                    //添加一个EchoServerHandler到子channel的ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    /**
                     * 当一个新的连接被接受时，一个新的子Channel将会被创建
                     * @param ch
                     * @throws Exception
                     */
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // EchoServerHandler被标注为@Sharable，所以我们可以总是使用同样的实例
                        ch.pipeline().addLast(serverHandler);
                    }
                });
            // 异地地绑定服务器调用sync()方法阻塞等到绑定完成
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            //获取Channel的closeFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            //关闭EventLoopGroup释放所有资源
            group.shutdownGracefully().sync();
        }
    }
}
