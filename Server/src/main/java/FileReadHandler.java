import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.*;



public class FileReadHandler extends ChannelInboundHandlerAdapter {
    private final String login;
    private PartOfFileMsg partOfFile = PartOfFileMsg.FILE_NAME_SIZE;
    int fileNameSize = 0;
    String fileName = null;
    byte[] fileNameBytes = null;
    long fileSize = -1;
    long readBytes = 0;
    int i = 0;
    OutputStream out;

    public FileReadHandler(String login) {
        super();
        this.login = login;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
//
////        проверял содержимое buf
////        for (int i = 0; i < 200; i++) {
////            System.out.println(i + " байт: " + buf.getByte(i));
////        }
//
//
//        if (partOfFile == PartOfFileMsg.FILE_NAME_SIZE) {
//            if (buf.readableBytes() >= 4) {
//                fileNameSize = buf.readInt();
//                partOfFile = PartOfFileMsg.FILE_NAME_BYTES;
//                System.out.println(fileNameSize);
//            }
//        }
//
//        if (partOfFile == PartOfFileMsg.FILE_NAME_BYTES) {
//            if (buf.readableBytes() >= fileNameSize) {
//                fileNameBytes = new byte[fileNameSize];
//                buf.readBytes(fileNameBytes);
//                fileName = new String(fileNameBytes);
//                partOfFile = PartOfFileMsg.FILE_SIZE;
//                out = new FileOutputStream("Server/" + login + "/" + fileName);
//                System.out.println(fileName);
//            }
//        }
//
//        if (partOfFile == PartOfFileMsg.FILE_SIZE) {
//            if (buf.readableBytes() >= 8) {
//                fileSize = buf.readLong();
//                partOfFile = PartOfFileMsg.FILE_BODY;
//                System.out.println(fileSize);
//            }
//        }
//
//        if (partOfFile == PartOfFileMsg.FILE_BODY) {
//            while (buf.isReadable()) {
////            if (buf.isReadable()) {
//                    out.write(buf.readByte());
//                    readBytes++;
//                    System.out.println(readBytes);
//            }
//
//        }
//
//        if (readBytes == fileSize) {
//            partOfFile = PartOfFileMsg.FILE_NAME_SIZE;
//            fileNameSize = -1;
//            fileName = "";
//            fileNameBytes = null;
//            fileSize = -1;
//            readBytes = 0;
//            out.close();
//            buf.release();
//            firstByteTypeData = CommandHandler.FirstByteTypeData.getFirstByte((byte) -1);
//            ctx.writeAndFlush("файл сервером принят\n");
//            System.out.println("файл принят");
//        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private enum PartOfFileMsg {
        FILE_NAME_SIZE, FILE_NAME_BYTES, FILE_SIZE, FILE_BODY
    }

}