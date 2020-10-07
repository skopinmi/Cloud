import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;

public class FileReadHandler extends ChannelInboundHandlerAdapter {
    public static String login;

    public FileReadHandler (String login) {
        super();
        this.login = login;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("start reading");
        int fileNameSize = buf.readInt();
        System.out.println(fileNameSize);
        byte [] fileNameBytes = new byte[fileNameSize];
        buf.readBytes(fileNameBytes);
        String fileName = new String(fileNameBytes);
        System.out.println(fileName);
        long fileSize = buf.readLong();
        long readBytes = 0;
        try (OutputStream out = new FileOutputStream(login + "/" + fileName)){
            while (fileSize > readBytes) {
                System.out.println(".");
                out.write(buf.readByte());
                readBytes++;
            }
        }
        ctx.writeAndFlush("файл сервером принят\n");

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}