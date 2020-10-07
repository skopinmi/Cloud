package Services;

import io.netty.buffer.ByteBuf;

/*
    обработка команд
    пока только печать на экран
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class CommandService {
    public static void commandChanger (String [] tokens, ByteBuf buf) throws IOException {
        switch (tokens[0]) {
            case "copy": {
                System.out.println("copy");
                System.out.println(tokens[1]);
                File file = new File(tokens[1]);
                System.out.println(file.getName());
                readAndWriteFile(buf, file);
                break;
            }
            default: {
                System.out.println("404 - :)");
            }
        }
    }

    public static void commandChanger (String [] tokens, Object msg) throws IOException {
        switch (tokens [0]) {
            case "copy": {
                System.out.println(tokens[1]);
                File file = new File(tokens[1]);
                System.out.println(file.getName());
                readAndWriteFile(msg, file);
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
        public static void readAndWriteFile (ByteBuf buf, File file) throws IOException {
            System.out.println("1");
            try (OutputStream out = new FileOutputStream("Server/" + file.getName())){
                while (buf.readableBytes() > 0) {
                    System.out.println("2");
                    out.write(buf.readByte());
                }
            }
            System.out.println("3");
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
