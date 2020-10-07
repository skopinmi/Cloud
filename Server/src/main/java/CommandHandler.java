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
    private boolean isFile = false;

    public CommandHandler(String login) {
        super();
        this.login = login;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        char one = (char) buf.readByte();
        /*
            в первый байт байт сообщения
            '0' - команда
            '1' - файл

         */

        if (one == '0') {
            /*

             */
            byte two = buf.readByte();
            String com = DecoderService.byteToString(buf, two);
            String [] tokens = com.split(" ");
            commandChanger(tokens);

        } else if (one == '1') {
            System.out.println("переход в файлридфайл для чтения файла");
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush("Ошибка команды\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    public static void commandChanger (String [] tokens) {
        switch (tokens [0]) {
            case "show" : {
                System.out.println("Show");
                show();
                break;
            }
            default: {
                System.out.println("неизвестная команда");
                System.out.println(tokens[0]);
                break;
            }
        }
    }
/*
    show отображает содержимое репозитория на сервере
    пока отображает на сервере :)
 */
    private static void show () {
        String clientPath = "Server/login1";
        try {   Files.walkFileTree(Paths.get(clientPath), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                if (file.getFileName().toString().equals(clientPath)) {
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
