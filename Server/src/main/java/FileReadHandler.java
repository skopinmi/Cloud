import Services.CommandService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;

public class FileReadHandler extends ChannelInboundHandlerAdapter {
    File file;

    public FileReadHandler (File file) {
        super();
        this.file = file;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("1");
        ByteBuf buf = (ByteBuf) msg;
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream("Server/" + file.getName()))){
            while (buf.isReadable()) {
                System.out.println("2");
                out.write(buf.readByte());
            }
        }
        System.out.println("3");

//        ByteBuf buf = (ByteBuf) msg;
////        char znak = buf.readChar();
////        System.out.println(znak);
//        short s = buf.readByte();
//        String fileNameSizeShort = DecoderService.byteToString(s);
//        System.out.println(fileNameSizeShort);
////        byte [] fileNameBytes = new byte [fileNameSizeShort];
////        buf.readBytes(fileNameBytes);
////        String fileName = DecoderService.byteToString(fileNameBytes);
////        System.out.println(fileName);
////        for(byte s : fileNameBytes) {
////            System.out.print((char) s);
//        }
//        long fileSizeLong = buf.getLong(0);
//        System.out.println(fileSizeLong);
//        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
//            while (buf.isReadable()) {
//                out.write(buf.readByte());
//            }
//        }
        ctx.writeAndFlush("Ошибка чтения\n");
        System.out.println("Ошибка чтения");

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}