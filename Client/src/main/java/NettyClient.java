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
             Scanner sc = new Scanner(socket.getInputStream())){

            /*
               отдельный поток для чтения сообщений от сервера
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String a = sc.nextLine();
                        System.out.println(a);
                    }
                }
            }).start();





            /*
                авторизация на сервере, возможна из консоли
             */

            sendCommand("/auth login1 password1", out);
            Thread.sleep(2000);

            /*
                часть кода считывает команды из консоли и выполняет,
                но часто возникает ошибка при этом команда выполняется
             */

//            Scanner clientCommandReader = new Scanner(System.in);
//            String clientCommand = "";
//            do {
//                try {
//                    clientCommand = clientCommandReader.nextLine();
//                    changer(clientCommand, out);
//
//                } catch (Exception e) {
//                    System.out.println("не удалось ");
//                }
//            } while (!clientCommand.equals("end"));


//      тестовая часть
            /*
                метод отправляющий команды первый байт '0'
                тестовая команда для CommandHandler сервера
                показывает содержимое репозитория на сервере

                работают команды:
                show - выводит содержимое репозиротия на сервере
                send [путь к файлу] - отправляет файл на сервер
                delete [путь к файлу] - удаляет файл


             */
            sendCommand("show", out);
            Thread.sleep(1000);

            /*
                метод посылающий файл
             */
//                File file = new File("Client/login1/netty-servers.zip");
//            при попытке послать большой файл выбрасывает исключение

                File file = new File("Client/login1/file.txt");
                sendFile(file, out);
                Thread.sleep(1000);

                sendCommand("show", out);
                Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // выбор команда \ команда на отправку файла

    public static void changer (String command, DataOutputStream out) throws IOException {
        String [] tokens = command.split(" ");
        if (tokens[0].equals("send")) {
            File file = new File(tokens[1]);
            sendFile(file, out);
        } else {
            sendCommand(command, out);
        }
    }

    //    отправляем файл

    public static void sendFile (File file, DataOutputStream out) {

        int fileNameSize = file.getName().length();
        byte [] fileNameBytes = file.getName().getBytes();
        long fileSize = file.length();
        int x;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file.getPath()))) {
            out.writeByte(1);
            out.writeInt(fileNameSize);
            out.write(fileNameBytes);
            out.writeLong(fileSize);
            long partOfFile = fileSize / 20 - 1;
            long howManyBytes = 0;
            while ((x = in.read()) != -1) {
                out.writeByte(x);
                howManyBytes++;
                if (howManyBytes == partOfFile) {
                    System.out.print(".");
                    howManyBytes = 0;
                }
                /*
                    неизвесная магия - без задержки не работает прием на сервере...
                 */
                Thread.sleep(1);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("отправил файл");
    }

    public static void sendCommand (String com, DataOutputStream out) throws IOException {
        byte[] command = com.getBytes();
        out.write(0);
        // первый байт размер команды
        out.write(command.length);
        out.write(command);
        System.out.println("отправил команду");

    }
}
