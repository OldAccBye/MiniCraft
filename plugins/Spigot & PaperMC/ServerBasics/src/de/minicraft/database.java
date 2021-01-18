package de.minicraft;

import java.sql.*;

public class database {
    private static Connection con = null;

    public static Connection getConnection() { return con; }

    public static boolean connect() throws SQLException, ClassNotFoundException {
        if (isConnected()) return true;

        String host = config.config.getString("mysql.host"),
                database = config.config.getString("mysql.database"),
                port = config.config.getString("mysql.port"),
                username = config.config.getString("mysql.username"),
                password = config.config.getString("mysql.password"),
                useSSL = config.config.getString("mysql.ssl");

        con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL, username, password);

        return con != null;
    }

    public static boolean disconnect() throws SQLException {
        if (!isConnected()) return true;

        con.close();

        return con == null;
    }

    public static boolean isConnected() { return (con != null); }
}