package com.zhuzz.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @description: 处理服务器端通道
 * @author: zhuzz
 * @date: 2018-10-09 10:41
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            //==============打印接收的数据
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        } finally {
            // 是引用计数的对象，必须通过release()方法显式释放
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常时关闭连接。
        cause.printStackTrace();
        ctx.close();
    }
}
