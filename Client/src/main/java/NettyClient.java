import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class NettyClient implements Runnable {

    String login;
    String answer = "";
    String clientCommand = "";

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 8085);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())){

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

            System.out.println("Проект: Облачное хранилище на Java\n" +
                    "Geek University Java-разработка \n" +
                    "СПб 2020 г.\n" +
                    "\n" +
                    "Процесс регистрации/авторизации :");

            Scanner clientCommandReader = new Scanner(System.in);

            do {
                System.out.println("Вы зарегестрированны на сервере?   y/n");
                clientCommand = clientCommandReader.nextLine();
            } while (!clientCommand.equals("y") && !clientCommand.equals("n"));

            /*
                регистрация
                логин и пароль
            */


            if (clientCommand.equals("n")) {
                do {
                    System.out.println("Придумайте и введите login : ");
                    clientCommand = "/registration " + clientCommandReader.nextLine();
                    sendCommand(clientCommand, out);
                    Thread.sleep(100); // пауза для печати ответа из другого потока
                    if (!answer.equals("loginIsGood\n")) {
                        System.out.println("login уже используется, проробуйте еще раз.");
                    }
                } while (!answer.equals("loginIsGood\n"));
                System.out.println("Придумайте и введите пароль : ");
                clientCommand = "/password " + clientCommandReader.nextLine().hashCode();
                sendCommand(clientCommand, out);
                Thread.sleep(100); // пауза для печати ответа из другого потока
            }

            /*
                авторизация на сервере, возможна из консоли
             */

            do {
                System.out.print("Введите логин : ");
                login = clientCommandReader.nextLine();
                System.out.print("Ведите пароль : ");
                String password = clientCommandReader.nextLine();
                sendCommand(("/auth " + login + " " +  password), out);

    // код ниже для быстрого входа

//                sendCommand("/auth login1 password1", out);
//                login = "login1";

    // код выше для быстрого входа

            } while (!answer.equals("connected\n"));

            System.out.println("help - справка");

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // выбор : команда / команда на отправку / получение файла / помощь

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
                        "send [путь к файлу] - загружает файл на сервер\n" +
                        "download [путь к файлу] - загрузка файла с сервера\n" +
                        "delete [путь к файлу] - удаляет файл\n" +
                        "copy [путь к файлу] [путь к копии файла] - копирует файл\n" +
                        "move [путь от куда] [путь куда] - пермещает файл\n" +
                        "create_dir [путь к новой папке] - создать новую папку\n" +
                        "end - выход из программы\n");
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
