import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
/*

    Для отправки ответа на клиент

 */

public class ReportHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String str = (String)msg;
        byte[] arr = str.getBytes();
        ByteBuf buf = ctx.alloc().buffer(arr.length);
        buf.writeBytes(arr);
        ctx.writeAndFlush(buf);
        buf.release();
    }
}
