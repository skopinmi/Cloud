import java.io.*;
import java.net.Socket;
import java.util.Scanner;
/*
    версия для теста сервера
 */

public class NettyClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8185);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(socket.getInputStream());
            File file = new File("Client/src/file.txt");
            /*
               отдельный поток для чтения сообщений от сервера
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String a = sc.nextLine();
                    System.out.println(a);
                }
            }).start();
            sendCommand("/auth login1 password1", out);
            Thread.sleep(2000);
//            sendCommand("show 1", out);
//            sendCommand("copy Client/src/file.txt", out);
            sendFile( file, out);
            /*
                что бы не отключался и видно было что работает :)
             */
            while (true) {
                System.out.print(".");
                Thread.sleep(2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    отправляем файл
    public static void sendFile (File file, DataOutputStream out) throws IOException, InterruptedException {

        sendCommand("copy file.txt", out);
        Thread.sleep(100);
        try (DataInputStream in = new DataInputStream(new FileInputStream("Client/src/file.txt"))) {
            int x;
            while ((x = in.read()) != -1) {
                out.writeByte(x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("отправил файл");
    }

    public static void sendCommand (String com, DataOutputStream out) throws IOException {
        byte[] command = com.getBytes();
        // первый байт размер команды
        out.write((byte) command.length);
        out.write(command);
        System.out.println("отправил команду");

    }
}
