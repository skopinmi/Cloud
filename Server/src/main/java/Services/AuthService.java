package Services;

import java.sql.*;
/*
    получение true при совпадении логина и пароля
 */
public class AuthService{
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getNickByLoginAndPass (String login, int pass) {
        String sql = String.format("select password from baza where login = '%s'", login);
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                if (resultSet.getInt(1) == pass) {
                    return true;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    // проверка наличия логина в базе
    // есть в базе true

    public static boolean hasLogin (String login) {

        String sql = String.format("select login from baza where login = '%s'", login);
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                    return true;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }

    // регистрация нового пользователя

    public static void registrationLogin (String login, int pass) {
        String sql = String.format("insert into baza values ('%s', '%s')", login, pass);
        try {
            statement.addBatch(sql);
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
