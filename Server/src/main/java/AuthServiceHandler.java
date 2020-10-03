import Services.AuthService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import Services.DecoderService;
/*
    handler берет кусок байтов с логином и паролем для проверки соответсвтвия логина и пароля,
    при первичном подключении
    подключаться может несколько клиентов
    к одной учетной записи можно подключаться несколько раз  - ИСПРАВИТЬ
 */

public class AuthServiceHandler extends ChannelInboundHandlerAdapter {

    private boolean authOk = false;
    static {
        AuthService.connect();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (authOk) {
            ctx.fireChannelRead(msg);
            return;
        }

        String input = DecoderService.byteToString(msg);
        System.out.println(input);

        if (input.split(" ")[0].equals("/auth")) {
            String login = input.split(" ")[1];
            String pass = input.split(" ")[2];
            authOk = AuthService.getNickByLoginAndPass(login, pass);
            if (authOk) {
                System.out.println("Подключен клиент " + login);
                ctx.writeAndFlush("соединение установленно\n");
            } else {
                ctx.writeAndFlush("Ошибка ввода login или password\n");
                System.out.println("Ошибка ввода login или password");
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}