import Services.CommandService;
import Services.DecoderService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        commandChanger(tokens, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    public static void commandChanger (String [] tokens, Object msg) throws IOException {
        switch (tokens [0]) {
            case "copy": {
                System.out.println(tokens[1]);
                File file = new File(tokens[1]);
                System.out.println(file.getName());
                new FileReadHandler(file);
                break;
            }
//            case "/delete" : {
//                System.out.println("delete");
//                break;
//            }
//            case "/move" : {
//                System.out.println("Move");
//                break;
//            }
            case "show" : {
                System.out.println("Show");
                show(tokens [1]);
                break;
            }
//            default: {
//                System.out.println("неизвестная команда");
//                break;
//            }
        }
    }

    // часть для чтения и записи файла пока не работает, ...

    public static void readAndWriteFile (Object msg, File file) throws IOException {
        System.out.println("начало чтения файла");
        ByteBuf buf = (ByteBuf) msg;
//        for (int i = 0; i < 46; i++) {
//            buf.readByte();
//        }
        try (OutputStream out = new FileOutputStream("Server/" + file.getName())){
            while (buf.isReadable()) {
                System.out.println("чтение байта файла");
                out.write(buf.readByte());
            }
        }
        System.out.println("файл получен");
    }

    private static void show (String path) {

        try {   Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                if (file.getFileName().toString().equals(path)) {
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
