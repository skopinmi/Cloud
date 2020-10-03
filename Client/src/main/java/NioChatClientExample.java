import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class NioChatClientExample {

    public static void main(String[] args) {
        try {
            SocketChannel client = null;
            client = SocketChannel.open();

            SocketAddress socketAddress = new InetSocketAddress("localhost", 8189);
            client.connect(socketAddress);
            client.configureBlocking(false);

            Selector selector = Selector.open();
            client.register(selector, SelectionKey.OP_ACCEPT);
            // печать в консоли и вывод на сервер...

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String word = "";
            ByteBuffer msg;
            do {
                word = reader.readLine();
                msg = ByteBuffer.wrap(word.getBytes());
                client.write(msg);
            } while (!word.equals("end"));

            System.out.println("end");
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

