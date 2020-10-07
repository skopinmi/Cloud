import java.io.*;
import java.net.Socket;
import java.util.Scanner;
/*
    версия для теста сервера
 */

public class NettyClient implements Runnable {
    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 8085);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(socket.getInputStream()); ) {

            File file = new File("Client/login1/file.txt");
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

            /*
                авторизация на сервере
             */
            sendCommand("/auth login1 password1", out);
            Thread.sleep(1000);

            /*
                метод отправляющий команды первый байт '0'
                тестовая команда для CommandHandler сервера
                показывает содержимое репозитория на сервере
             */
            sendCommand("show", out);
            Thread.sleep(100);

            /*
                метод посылающий файл
             */
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
    public static void sendFile (File file, DataOutputStream out) throws IOException {
        out.writeByte(1);
        System.out.println(file.getName().length());
        System.out.println(file.getPath());
        int fileNameSize = file.getName().length();
        byte [] fileNameBytes = file.getName().getBytes();
        long fileSize = file.length();
        int x;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file.getPath()))) {
            out.writeByte('1');
            out.writeInt(fileNameSize);
            out.write(fileNameBytes);
            out.writeLong(fileSize);
            while ((x = in.read()) != -1) {
                out.writeByte(x);
                System.out.println(".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("отправил файл");
    }

    public static void sendCommand (String com, DataOutputStream out) throws IOException {
        byte[] command = com.getBytes();
        out.write('0');
        // первый байт размер команды
        out.write(command.length);
        out.write(command);
        System.out.println("отправил команду");

    }
}
