package com.zhuzz.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.1 EchoServerHandler
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */

/**
 * 标识一个ChannelHandler可以被多个Channel安全地共享
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每一个传入的消息都要调用
     * 这里你仍然需要将传入的回送给发送者，而write()操作时异步的，直到channelRead（）方法返回后仍可能没有完成，
     * 其实现ChannelInboundHandlerAdapter，在这个时间点上不会释放消息
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8));
        //将接受到的消息写给发送者，而不冲刷出战消息
        ctx.write(in);
    }

    /**
     * 通知ChannelInboundHandler最后一次对channelRead的调用是当前批量读取中的最后一条消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        //将未决消息冲刷到远程节点，并且关闭该Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 在读取过程中有异常抛出时会调用
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        //打印异常栈跟踪
        cause.printStackTrace();
        //关闭该Channel
        ctx.close();
    }
}
