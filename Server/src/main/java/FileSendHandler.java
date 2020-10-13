import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileSendHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        
        int fileNameSize = file.getName().length();
        byte [] fileNameBytes = file.getName().getBytes();
        long fileSize = file.length();
        int x;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file.getPath()))) {
            out.writeInt(fileNameSize);
            out.write(fileNameBytes);
            out.writeLong(fileSize);
//            byte [] buf = new byte[1024];
            while ((x = in.read()) != -1) {
                out.writeInt(x);
//                out.write(buf, 0, x);
                System.out.print("+");
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
        System.out.println("отправил файл");

        String str = (String)msg;
        byte[] arr = str.getBytes();
        ByteBuf buf = ctx.alloc().buffer(arr.length);
        buf.writeBytes(arr);
        ctx.writeAndFlush(buf);
        buf.release();
    }


}
