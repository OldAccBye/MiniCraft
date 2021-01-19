package de.minicraft.players;

import java.sql.PreparedStatement;
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
    public String language;
    public String group;
    public PermissionAttachment permissions;

    playerData(UUID pUUID, String language) {
        this.pUUID = pUUID;
        this.language = language;
        this.permissions = Objects.requireNonNull(Bukkit.getPlayer(pUUID)).addAttachment(serverbasics.plugin);
    }

    public void saveAll() {
        PreparedStatement ps = null;

        try {
            ps = database.getConnection().prepareStatement("UPDATE users SET perm_group = ?, language = ? WHERE UUID = ? LIMIT 1");
            ps.setString(1, this.group);
            ps.setString(2, this.language);
            ps.setString(3, this.pUUID.toString());

            if (ps.executeUpdate() <= 0) {
                ps.close();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[DB][ERROR] " + e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[playerData->saveAll][ERROR] " + e.getMessage());
            }
        }
    }
}