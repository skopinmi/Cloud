import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
//  бинарная версия
    public static void main(String[] args) {
        binaryVersion();
    }

    public static void binaryVersion () {
        try (ServerSocket serverSocket = new ServerSocket(8089)) {
            System.out.println("Server is listening...");
            try (Socket socket = serverSocket.accept();
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
                System.out.println("Client is recieved");


                int n;
                while ((n = inputStream.read()) != -1) {
                    if ((char) n == '%') {
                        System.out.println(readCommand(inputStream));
                    }
                    if ((char) n == '$') {
                        readAndWriteFile(inputStream);
                    }
            }
                System.out.println("end");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

//    чтение команды

    public static String readCommand (BufferedInputStream inputStream) throws IOException {
        int n;
        String command = "";
        while ((n = inputStream.read()) != - 1) {
            if ((char) n == ' ') {
                break;
            }
            command += (char) n;
        }
        return command;
    }

//  чтение и запись файла на сервер

    public static void readAndWriteFile (BufferedInputStream inputStream) throws IOException {
        int n;
        String fileName = "";
        FileOutputStream fileOutputStream;
        while ((n = inputStream.read()) != -1) {
//  чтение имени файла - конец $
            if (n == '$') {
                break;
            }
            fileName += (char) n;
        }
//  чтение и запись файла
        fileOutputStream = new FileOutputStream (fileName, true);
        while ((n = inputStream.read()) != -1) {
            fileOutputStream.write(n);
        }
    }

}
