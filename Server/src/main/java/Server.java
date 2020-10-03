import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
//  бинарная версия
    public static void main(String[] args) {
//        binaryVersion();
        new Thread(new NettyServer()).start();
    }
//   бинарная версия
//    public static void binaryVersion () {
//        try (ServerSocket serverSocket = new ServerSocket(8085)) {
//            System.out.println("Server is listening...");
//            try (Socket socket = serverSocket.accept();
//                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream())) {
//                System.out.println("Client is received");
///*
//    чтение символов из входящего потока
//    метка % - начало передачи команды
//    метка $ - начало передачи файла
// */
//                int n;
//                while ((n = inputStream.read()) != -1) {
//                    if ((char) n == '%') {
//                        commandChanger(readCommand(inputStream));
//                    }
//                    if ((char) n == '$') {
//                        readAndWriteFile(inputStream);
//                    }
//            }
//                System.out.println("end");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////  чтение команды
//
//    public static String readCommand (BufferedInputStream inputStream) throws IOException {
//        int n;
//        StringBuilder command = new StringBuilder();
//        while ((n = inputStream.read()) != - 1) {
////  конец команды '.'
//            if ((char) n == '.') {
//                break;
//            }
//            command.append((char) n);
//        }
//        return command.toString();
//    }
//
////  чтение и запись файла на сервер
//
//    public static void readAndWriteFile (BufferedInputStream inputStream) throws IOException {
//        int n;
//        StringBuilder fileName = new StringBuilder();
//        StringBuilder fileSize = new StringBuilder();
//        FileOutputStream fileOutputStream;
////  чтение имени файла - конец $
//        while ((n = inputStream.read()) != -1) {
//            if (n == '$') {
//                break;
//            }
//            fileName.append((char) n);
//        }
//        System.out.println("Имя файла " + fileName.toString());
////  чтение размера файла - конец $
//        while ((n = inputStream.read()) != -1) {
//            if (n == '$') {
//                break;
//            }
//            fileSize.append((char) n);
//        }
//        System.out.println("Размер файла " + fileSize.toString());
////  чтение и запись файла
//        fileOutputStream = new FileOutputStream (fileName.toString(), true);
///*
//    использовал FileOutputStream - с возможностью дозаписывания < при повторной отпаравке и отправке
//    файла с тем же именем будет дозапись в существующий!
//    ТРЕБУЕТ ДОРАБОТКИ
// */
//        while ((n = inputStream.read()) != -1) {
//            fileOutputStream.write(n);
//        }
//    }
//    public static void commandChanger (String command) {
//        switch (command) {
//            case "copy" : {
//                System.out.println("Copy");
//            }
//            case "delete" : {
//                System.out.println("Delete");
//            }
//            case "move" : {
//                System.out.println("Move");
//            }
//            case "show" : {
//                System.out.println("Show");
//            }
//            default: {
//                System.out.println("неизвестная команда");
//            }
//        }
//
//    }
}
