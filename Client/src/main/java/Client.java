import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
// бинарная версия
    public static void main(String[] args) {
        binaryClient();
    }

    public static void binaryClient () {
        try (Socket socket = new Socket("localhost", 8085)) {
            Scanner sc = new Scanner(socket.getInputStream());
            File file = new File("client\\src\\file.txt");
            sendCommand("auth login1 password1 ", socket); // конец команды

//            String s = ".";
////            sendCommand("auth login1 password1", socket); // конец команды
//
            sendCommand("load", socket);
            sendFile(file, socket);
//            sendFile(file, socket);
//            while (s.equals(".")) {
//                System.out.print(s);
//                s = sc.nextLine();
//                Thread.sleep(1000);
//            }
    } catch (IOException e) {
            e.printStackTrace();
        }

    }
//    отправляем файл, метка/разделитель перед файлом $
    public static void sendFile (File file, Socket socket) {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())){
//            getOutputStreamout.write('$');
//            String fileName = file.getName();
//            out.writeShort(fileName.length());
//            out.write(fileName.getBytes());
//            long fileSize = file.length();
//            out.writeLong(fileSize);
            byte [] buf = new byte[1024];
            try (InputStream inputStream = new FileInputStream(file);) {
                int n;
                while ((n = inputStream.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
            }
            System.out.println("отправил файл");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    отправляем команду (разделитель /)
    public static void sendCommand (String com, Socket socket) throws IOException {
        com = "/" + com;
        byte [] command = com.getBytes();

        for (int i = 0; i < command.length; i++){
            try {
                socket.getOutputStream().write(command[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("отправил команду");
    }
}
