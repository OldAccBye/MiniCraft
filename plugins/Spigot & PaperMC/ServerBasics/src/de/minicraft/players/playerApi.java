package de.minicraft.players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.minicraft.database;
import org.bukkit.entity.Player;

public class playerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    private static final HashMap<UUID, playerData> playerList = new HashMap<>();

    public static void createUser(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);

        if (p == null) {
            Bukkit.getLogger().severe("[createUser][ERROR] player = null");
            return;
        }

        PreparedStatement ps = null;

        String username = p.getName();

        try {
            ps = database.getConnection().prepareStatement("INSERT INTO users SET username = ?, UUID = ?, perm_group = 'default'");
            ps.setString(1, username);
            ps.setString(2, pUUID.toString());
            if (!ps.execute()) {
                Objects.requireNonNull(Bukkit.getPlayer(pUUID)).kickPlayer("Account konnte nicht erstellt werden!"); // TODO add to language.yml
                return;
            }

            playerData data = new playerData(pUUID);
            data.username = username;
            data.group = "default";

            playerList.put(pUUID, data);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[playerApi->createUser][ERROR] " + e.getMessage());
            }
        }
    }

    public static void add(UUID pUUID) {
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
                return;
            }

            playerData data = new playerData(pUUID);
            data.username = rs.getString("username");
            data.group = rs.getString("perm_group");

            playerList.put(pUUID, data);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
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
        return (exists(pUUID) ? playerList.get(pUUID) : null);
    }

    public static void remove(UUID pUUID) { if (exists(pUUID)) playerList.remove(pUUID); }
    public static boolean exists(UUID pUUID) { return playerList.containsKey(pUUID); }
}