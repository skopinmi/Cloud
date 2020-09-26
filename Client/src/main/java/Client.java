import java.io.*;
import java.net.Socket;

public class Client {
// бинарная версия
    public static void main(String[] args) {
        binaryClient();
    }

    public static void binaryClient () {
        try (Socket socket = new Socket("localhost", 8089)) {
            File file = new File("client\\src\\file.txt");
            sendCommand("MyFirstCommand.", socket); // конец команды '.'
            sendFile(file, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//    отправляем файл, метка/разделитель перед файлом $
    public static void sendFile (File file, Socket socket) {
        try ( FileInputStream fileInputStream = new FileInputStream(file);
              BufferedInputStream out = new BufferedInputStream(fileInputStream)){
//    определение имени файла и разделитель + отправка
            socket.getOutputStream().write((int)'$');
            String fileName = file.getName();
            for (int i = 0; i < fileName.length(); i++) {
                socket.getOutputStream().write((int)fileName.charAt(i));
            }
/*
    определение размера файла и отправка
    из строки с единицей измерения
 */
            long fileSize = file.length();
            String fileSizeSt = "$ " + fileSize + " byte ";
            for (int i = 0; i < fileSizeSt.length(); i++) {
                socket.getOutputStream().write((int)fileSizeSt.charAt(i));
            }
//    отправляем содержимое
            int n;
            socket.getOutputStream().write((int)'$');
            while ((n = out.read()) != -1) {
                socket.getOutputStream().write(n);
            }

            System.out.println("отправил файл");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    отправляем команду (разделитель %)
    public static void sendCommand (String com, Socket socket){
        com = "%" + com;
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
