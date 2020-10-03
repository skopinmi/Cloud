import Services.CommandService;
import Services.DecoderService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
/*
    получение байтов с командами
 */

public class CommandHandler extends ChannelInboundHandlerAdapter {
    public static File file;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
//        byte one = buf.readByte();
//        String com = DecoderService.byteToString(buf, one);
        String com = DecoderService.byteToString(msg);
        String [] tokens = com.split(" ");
        System.out.println(tokens[0]);
//        CommandService.commandChanger(tokens, buf);
        CommandService.commandChanger(tokens, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
