/*
    Проект: Облачное хранилище на Java
    выполнен с использованием Java фреймворка Netty
    в рамках курса: Разработка сетевого хранилища на Java
    Geek University Java-разработки 2020 г.

    Консольный клиент
 */

public class Client {
    public static void main(String[] args) {
        new Thread(new NettyClient()).start();
    }
}
