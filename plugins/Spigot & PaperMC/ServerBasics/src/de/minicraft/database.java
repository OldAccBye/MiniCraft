package de.minicraft;

import org.bukkit.Bukkit;

import java.sql.*;

public class database {
    public static Connection con;

    public static boolean connect() throws SQLException, ClassNotFoundException {
        if (con != null && !con.isClosed()) return true;

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
        if (con != null && !con.isClosed())
            con.close();

        return true;
    }

    public static boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[database-isConnected][ERROR] " + e.getMessage());
            return false;
        }
    }
}