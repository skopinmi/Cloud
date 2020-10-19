import Services.DecoderService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
/*
    получение байтов с командами
 */

public class CommandHandler extends ChannelInboundHandlerAdapter {

    private final String login;
    public FirstByteTypeData firstByteTypeData = FirstByteTypeData.EMPTY;
    private String result = null;

    private PartOfFileMsg partOfFile = PartOfFileMsg.FILE_NAME_SIZE;
    int fileNameSize = 0;
    String fileName = null;
    byte[] fileNameBytes = null;
    long fileSize = -1;
    long readBytes = 0;
    OutputStream out;

    String filePath = null;
    byte filePathSize = -1;
    InputStream in;
    File fileOut;



    public CommandHandler(String login) {
        super();
        this.login = login;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf buf = (ByteBuf) msg;

        /*
            если не идет загрузка или отправка файла, то ...
         */

        if (buf.readableBytes() < 1) {
            return;
        }

        if (firstByteTypeData == FirstByteTypeData.EMPTY || firstByteTypeData == FirstByteTypeData.ERROR) {
            byte firstByte = buf.readByte();
            firstByteTypeData = FirstByteTypeData.getFirstByte(firstByte);
        }

         /*
            в первый байт байт сообщения
            '0' - команда
            '1' - файл in
            '2' - файл out
         */

        if (firstByteTypeData == FirstByteTypeData.COMMAND) {
            /*
                обрабатываем команду
             */
            if (buf.readableBytes() < 1) {
                return;
            }
            byte secondByte = buf.readByte();
            String com = DecoderService.byteToString(buf, secondByte);
            String [] tokens = com.split(" ");
            commandChanger(tokens);
            firstByteTypeData = FirstByteTypeData.EMPTY;

        }

        else if (firstByteTypeData == FirstByteTypeData.FILE_IN) {
            /*
                если запушен процесс получения файла - чтение файла
            */

            if (partOfFile == PartOfFileMsg.FILE_NAME_SIZE) {
                if (buf.readableBytes() >= 4) {
                    fileNameSize = buf.readInt();
                    partOfFile = PartOfFileMsg.FILE_NAME_BYTES;
                    System.out.println(fileNameSize);
                }
            }

            if (partOfFile == PartOfFileMsg.FILE_NAME_BYTES) {
                if (buf.readableBytes() >= fileNameSize) {
                    fileNameBytes = new byte[fileNameSize];
                    buf.readBytes(fileNameBytes);
                    fileName = new String(fileNameBytes);
                    partOfFile = PartOfFileMsg.FILE_SIZE;
                    try {
                        out = new FileOutputStream("Server/" + login + "/" + fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(fileName);
                }
            }

            if (partOfFile == PartOfFileMsg.FILE_SIZE) {
                if (buf.readableBytes() >= 8) {
                    fileSize = buf.readLong();
                    partOfFile = PartOfFileMsg.FILE_BODY;
                    System.out.println(fileSize);
                }
            }

            if (partOfFile == PartOfFileMsg.FILE_BODY) {
                while (buf.isReadable()) {
                    try {
                        out.write(buf.readByte());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    readBytes++;
                }
            }

            if (readBytes == fileSize) {
                partOfFile = PartOfFileMsg.FILE_NAME_SIZE;
                fileNameSize = -1;
                fileName = "";
                fileNameBytes = null;
                fileSize = -1;
                readBytes = 0;
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buf.release();
                firstByteTypeData = FirstByteTypeData.getFirstByte((byte) -1);
                ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/file was received\n/"));
                System.out.println("файл принят");
            }

        }

        else if (firstByteTypeData == FirstByteTypeData.FILE_OUT) {

            /*
                  отправка файла
            */

            if (partOfFile == PartOfFileMsg.FILE_NAME_SIZE) {
                if (buf.readableBytes() >= 1) {
                    filePathSize = buf.readByte();
                    partOfFile = PartOfFileMsg.FILE_NAME_BYTES;
                    System.out.println(filePathSize);
                }
            }

            if (partOfFile == PartOfFileMsg.FILE_NAME_BYTES) {
                if (buf.readableBytes() >= filePathSize) {
                    byte[] filePathBytes = new byte[filePathSize];
                    buf.readBytes(filePathBytes);
                    filePath = new String(filePathBytes);
                    partOfFile = PartOfFileMsg.FILE_SIZE;
                    try {
                        in = new FileInputStream(filePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    fileOut = new File(filePath);
                    fileNameSize = fileOut.getName().length();
                    fileNameBytes = fileOut.getName().getBytes();
                    fileSize = fileOut.length();

                    System.out.println("Отправляю файл " + fileOut.getName());
                    ByteBuf bufRez = ctx.alloc().buffer(1024);

                    bufRez.writeByte((byte) '?');
                    bufRez.writeInt(fileNameSize);
                    bufRez.writeBytes(fileNameBytes);
                    bufRez.writeLong(fileSize);
                    int x = 0;
                    byte [] buffer = new byte[1024];
                    try {
                        while ((x = in.read(buffer)) != -1) {
                            System.out.println("+");
                            bufRez.writeBytes(buffer, 0, x);
                            ctx.writeAndFlush(bufRez);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    partOfFile = PartOfFileMsg.FILE_NAME_SIZE;
                    firstByteTypeData = FirstByteTypeData.EMPTY;
                    System.out.println("отправил файл");
                    filePath = null;
                    filePathSize = -1;
                }
            }
        }

        else {
            ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/Ошибка команды\n/"));
            firstByteTypeData = FirstByteTypeData.EMPTY;
        }

        if (result != null) {
            ctx.writeAndFlush(Services.DecoderService.stringToByteBuf('/' + result + '/'));
            result = null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void commandChanger (String [] tokens) {
        switch (tokens [0]) {
            case "show" : {
                System.out.println("Show");
                show();
                break;
            }
            case "delete" : {
                try {
                    Files.deleteIfExists(Paths.get(tokens[1]));
                    show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "copy" : {
                try {
                    Files.copy(Paths.get(tokens[1]), Paths.get(tokens[2]), StandardCopyOption.REPLACE_EXISTING);
                    show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                System.out.println("неизвестная команда");
                System.out.println(tokens[0]);
                result = "/неизвестная команда\n";
                break;
            }
        }
    }

    private void show () {
        StringBuilder stringBuilder = new StringBuilder();
        String clientPath = "Server/" + login;
        try {   Files.walkFileTree(Paths.get(clientPath), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                stringBuilder.append(file);
                stringBuilder.append("\n");
                if (file.getFileName().toString().equals(clientPath)) {
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = String.valueOf(stringBuilder);
    }

    private enum PartOfFileMsg {
        FILE_NAME_SIZE, FILE_NAME_BYTES, FILE_SIZE, FILE_BODY
    }

    private enum FirstByteTypeData {
        EMPTY((byte)-1), COMMAND((byte) 0), FILE_IN((byte) 1), FILE_OUT((byte) 2), ERROR ((byte)'?');

        byte firstByte;

        FirstByteTypeData(byte firstByte) {
            this.firstByte = firstByte;
        }

        public static FirstByteTypeData getFirstByte(byte firstByte) {
            if (firstByte == -1) {
                return EMPTY;
            }
            if (firstByte == 0) {
                return COMMAND;
            }
            if (firstByte == 1) {
                return FILE_IN;
            }
            if (firstByte == 2) {
                return FILE_OUT;
            }
            System.out.println("error - ошибка в первом байте");
            return ERROR;
        }
    }
}
