package de.minicraft.players;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import de.minicraft.database;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import de.minicraft.serverbasics;

public class playerData {
    public UUID pUUID;
    public String username;
    public String group;
    public PermissionAttachment permissions;

    playerData(UUID pUUID) {
        this.pUUID = pUUID;
        permissions = Objects.requireNonNull(Bukkit.getPlayer(pUUID)).addAttachment(serverbasics.plugin);
    }

    public boolean saveAll() {
        PreparedStatement ps = null;

        try {
            ps = database.getConnection().prepareStatement("UPDATE users SET perm_group = ? WHERE UUID = ? LIMIT 1");
            ps.setString(1, this.group);
            ps.setString(2, this.pUUID.toString());

            if (ps.executeUpdate() <= 0) {
                ps.close();
                return false;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
        } finally {
            try {
                ps.close();
                return true;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[playerData->saveAll][ERROR] " + e.getMessage());
                return false;
            }
        }
    }
}