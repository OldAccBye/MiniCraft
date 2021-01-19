package de.minicraft.players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.minicraft.database;
import org.bukkit.entity.Player;

public class playerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    private static final HashMap<UUID, playerData> playerList = new HashMap<>();

    public static void createUser(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);
        if (p == null) return;

        PreparedStatement ps = null;

        String username = p.getName();

        try {
            ps = database.getConnection().prepareStatement("INSERT INTO users SET username = ?, UUID = ?, perm_group = 'default'");
            ps.setString(1, username);
            ps.setString(2, pUUID.toString());
            ps.execute();

            playerData data = new playerData(pUUID, "en_us");
            data.username = username;
            data.group = "default";

            playerList.put(pUUID, data);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
            p.kickPlayer("[ERROR-01] Please contact the support!");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[playerApi->createUser][ERROR] " + e.getMessage());
            }
        }
    }

    public static void addUser(UUID pUUID) {
        // Diese Funktion prüft ob dieser Spieler bereits eingetragen ist und wenn ja entfernt diese Funktion diesen Eintrag
        remove(pUUID);

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = database.getConnection().prepareStatement("SELECT * FROM users WHERE UUID = ?");
            ps.setString(1, pUUID.toString());
            rs = ps.executeQuery();

            if (!rs.next()) {
                // Benutzer nicht gefunden also brich hier ab und erstell einen neuen
                createUser(pUUID);
                ps.close();
                rs.close();
                return;
            }

            playerData data = new playerData(pUUID, rs.getString("language"));
            data.username = rs.getString("username");
            data.group = rs.getString("perm_group");

            playerList.put(pUUID, data);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
            Player p = Bukkit.getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-02] Please contact the support!");
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[playerApi->add][ERROR] " + e.getMessage());
            }
        }
    }

    public static playerData getPlayer(UUID pUUID) {
        if (!exists(pUUID)) {
            Player p = Bukkit.getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-03] Please contact the support!");
        }

        return playerList.get(pUUID);
    }

    public static void remove(UUID pUUID) { if (exists(pUUID)) playerList.remove(pUUID); }
    public static boolean exists(UUID pUUID) { return playerList.containsKey(pUUID); }
}