import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;

public class NettyServer implements Runnable{

    public void run() {
        EventLoopGroup connectGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(connectGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast( new ReportHandler(), new AuthServiceHandler(), new CommandHandler());
                        }
                    });
//            .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = server.bind(8185).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            connectGroup.shutdownGracefully();
        }
    }

//    public static void main(String[] args) throws InterruptedException, IOException {
//        new Thread(new NettyServer()).start();
//    }
}


