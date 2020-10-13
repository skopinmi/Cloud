import Services.DecoderService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
/*
    получение байтов с командами
 */

public class CommandHandler extends ChannelInboundHandlerAdapter {
    public static String login;
    public static FirstByteTypeData firstByteTypeData = FirstByteTypeData.EMPTY;
    private static String result = null;

    public CommandHandler(String login) {
        super();
        CommandHandler.login = login;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        /*
            если запушен процесс получения файла
            msg в следующий handler
         */
        if (firstByteTypeData == FirstByteTypeData.FILE_IN) {
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf buf = (ByteBuf) msg;
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
            '1' - файл

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

        } else if (firstByteTypeData == FirstByteTypeData.FILE_IN) {
            System.out.println("прием файла");
            ctx.fireChannelRead(msg);

        } else if (firstByteTypeData == FirstByteTypeData.FILE_OUT) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush("Ошибка команды\n");
            firstByteTypeData = FirstByteTypeData.EMPTY;
        }
        if (result != null) {
            ctx.writeAndFlush(result + "\n");
            result = null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public enum FirstByteTypeData {
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

    public static void commandChanger (String [] tokens) {
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
            }
            case "copy" : {
                try {
                    Files.copy(Paths.get(tokens[1]), Paths.get(tokens[2]), StandardCopyOption.REPLACE_EXISTING);
                    show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            default: {
                System.out.println("неизвестная команда");
                System.out.println(tokens[0]);
                result = "неизвестная команда\n";
                break;
            }
        }
    }
/*
    show отображает содержимое репозитория на сервере
    пока отображает на сервере :)
 */
    private static void show () {
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
}
