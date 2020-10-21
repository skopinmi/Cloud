import Services.AuthService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import Services.DecoderService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
    handler берет кусок байтов с логином и паролем для проверки соответсвтвия логина и пароля,
    при первичном подключении
    подключаться может несколько клиентов
    к одной учетной записи можно подключаться несколько раз  - ИСПРАВИТЬ
 */

public class AuthServiceHandler extends ChannelInboundHandlerAdapter {

    private boolean authOk = false;
    private String login = null;
    static {
        AuthService.connect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (authOk) {
            ctx.fireChannelRead(msg);
            return;
        }

        String input = DecoderService.byteToString(msg);
        String [] partOfInput = input.split(" ");

        if (partOfInput [0].equals("/auth")) {
            String login = partOfInput [1];
            int pass = partOfInput [2].hashCode();
            authOk = AuthService.getNickByLoginAndPass(login, pass);
            if (authOk) {
                ctx.pipeline().addLast(new CommandHandler(login));
                System.out.println("Подключен клиент " + login);
                ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/connected\n/"));
            } else {
                ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/Error login or password\n/"));
                System.out.println("Ошибка ввода login или password");
            }
        }
        /*
            процесс регистрации нового пользователя
         */
        if (partOfInput [0].equals("/registration")) {
            if (!AuthService.hasLogin(partOfInput[1])) {
                login = partOfInput [1];
                ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/loginIsGood\n/"));
            }
        }
        if (partOfInput [0].equals("/password")) {
            AuthService.registrationLogin (login, Integer.parseInt(partOfInput[1]));
            if (AuthService.hasLogin(login)) {
                try {
                    // создание директории для нового пользователя
                    Files.createDirectory(Paths.get("Server/" + login));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(Services.DecoderService.stringToByteBuf("/Successful registration\n/"));
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
