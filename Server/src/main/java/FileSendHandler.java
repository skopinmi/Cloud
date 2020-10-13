import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.*;

public class FileSendHandler extends ChannelOutboundHandlerAdapter {
    File file;

    FileSendHandler(File file) {
        this.file = file;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {

        int fileNameSize = file.getName().length();
        byte [] fileNameBytes = file.getName().getBytes();
        long fileSize = file.length();
        int x;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file.getPath()))) {
//            не ясно можно так ? или через ByteBuf надо
            ctx.writeAndFlush(fileNameSize); // как пойдет int ?

            ctx.writeAndFlush(fileNameBytes);
            ctx.writeAndFlush(fileSize);
            byte [] buf = new byte[1024];
            try {
                while ((x = in.read(buf)) != -1) {
                    ctx.writeAndFlush (buf);
                    System.out.print("+");
                }
            } catch (Exception  e) {
                e.printStackTrace();
            }
        } catch (Exception  e) {
            e.printStackTrace();
            }

        System.out.println("отправил файл");
//        buf.release();
    }


}
