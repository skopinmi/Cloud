import java.io.*;
import java.net.Socket;
import java.util.Scanner;
/*
    версия для теста сервера
 */

public class NettyClient implements Runnable {

    String login;
    String answer = "";

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 8085);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())){

            Scanner clientCommandReader = new Scanner(System.in);
            String clientCommand = "";

            /*
                авторизация на сервере, возможна из консоли
             */
            do {
                System.out.print("Введите логин : ");
                login = clientCommandReader.nextLine();
//                System.out.print("\n");
                System.out.print("Ведите пароль : ");
                String password = clientCommandReader.nextLine();
//                System.out.print("\n");
    //               sendCommand(("/auth " + login + " " +  password), out);

    // код ниже для быстрого входа

                sendCommand("/auth login1 password1", out);
                login = "login1";
                Thread.sleep(100);

    // код выше для быстрого входа

            } while (answer.equals("соединение установленно\n"));

            /*
               отдельный поток для чтения сообщений от сервера
             */

            new Thread(() -> {
                byte x;
                while (true) {
                    try {
                        if ((x = in.readByte()) != -1) {
                            if (x == '/') {
                                StringBuilder stringBuilder = new StringBuilder();
                                while ((x = in.readByte()) != '/') {
                                    stringBuilder.append((char) x);
                                }
                                answer = stringBuilder.toString();
                                System.out.print(answer);
                            }
                            if (x == '?') {
                                int fileNameSize = in.readInt();
                                System.out.println(fileNameSize);
                                byte[] fileNameBytes = new byte[fileNameSize];
                                in.readFully(fileNameBytes);
                                String fileName = new String(fileNameBytes);
                                FileOutputStream out1 = new FileOutputStream("Client/" + login + "/" + fileName);
                                System.out.println(fileName);
                                long fileSize = in.readLong();
                                System.out.println(fileSize);
                                long readBytes = 0;
                                while (readBytes != fileSize) {
                                    out1.write(in.readByte());
                                    readBytes++;
                                }
                                out1.close();
                                System.out.println("файл принят");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /*
                часть кода считывает команды из консоли и выполняет
             */
            clientCommandReader = new Scanner(System.in);
            do {
                try {
                    clientCommand = clientCommandReader.nextLine();
                    changer(clientCommand, out);

                } catch (Exception e) {
                    System.out.println("не удалось ");
                }
            } while (!clientCommand.equals("end"));


//      тестовая часть

            /*
                работают команды:
                show - выводит содержимое репозиротия на сервере
                send [путь к файлу] - отправляет файл на сервер
                delete [путь к файлу] - удаляет файл
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // выбор команда \ команда на отправку файла

    public static void changer (String command, DataOutputStream out) throws IOException {
        String [] tokens = command.split(" ");
        switch (tokens[0]) {
            case "send" : {
                File file = new File(tokens[1]);
                sendFile(file, out);
                break;
            }
            case "download": {
                downloadFile(tokens[1] , out);
                break;
            }
            case "help": {
                System.out.println("show - выводит содержимое репозиротия на сервере\n" +
                        "send [путь к файлу] - отправляет файл на сервер\n" +
                        "download [путь к файлу] - загрузка файлф с сервера\n" +
                        "delete [путь к файлу] - удаляет файл");
                break;
            }
            default : {
                sendCommand(command, out);
            }
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
            byte [] buf = new byte[1024];
            while ((x = in.read(buf)) != -1) {
                out.write(buf, 0, x);
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
        System.out.println("отправил файл\n");
    }

    public static void sendCommand (String com, DataOutputStream out) throws IOException {
        byte[] command = com.getBytes();
        out.writeByte(0);
        // первый байт размер команды
        out.writeByte(command.length);
        out.write(command);
    }

    public static void downloadFile (String path, DataOutputStream out) throws IOException {
        byte[] command = path.getBytes();
        out.writeByte(2);
        // первый  размер команды
        out.writeByte(command.length);
        out.write(command);
        System.out.println("отправил запрос на скачивание файла " + path);
    }
}
