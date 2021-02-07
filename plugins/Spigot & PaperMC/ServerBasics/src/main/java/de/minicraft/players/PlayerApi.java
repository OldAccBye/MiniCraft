package de.minicraft.players;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.Configs;
import de.minicraft.ServerBasics;
import org.bson.Document;

import org.bukkit.entity.Player;

public class PlayerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();

    private static void register(UUID pUUID, Player p) {
        try {
            ServerBasics.mongo.collections.get("players").insertOne(new Document("username", p.getName())
                    .append("UUID", pUUID.toString())
                    .append("perm_group", "default")
                    .append("language", "en")
                    .append("banned", false)
                    .append("banSinceTimestamp", 0)
                    .append("banExpiresTimestamp", 0)
                    .append("banReason", "")
                    .append("bannedFrom", ""));
        } catch (MongoWriteException e) {
            e.printStackTrace();
            p.kickPlayer("[PlayerApi->register] Daten konnte nicht gespeichert werden.");
            return;
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[PlayerApi->register] Irgendetwas ist schief gelaufen.");
            return;
        }

        PlayerData data = new PlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.group = "default";
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        playerList.put(pUUID, data);
    }

    public static boolean login(UUID pUUID) {
        Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);
        if (p == null) return false;

        // Diese Funktion prüft ob dieser Spieler bereits eingetragen ist und wenn ja entfernt diese Funktion diesen Eintrag
        logout(pUUID);

        Document playerDoc;

        try {
            playerDoc = ServerBasics.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null) {
                register(pUUID, p);
                return true;
            }
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[PlayerApi->login] Irgendetwas ist schief gelaufen.");
            return false;
        }

        if (playerDoc.getBoolean("banned")) {
            Date date = new Date();
            Long currentDateTime = date.getTime();
            if (playerDoc.getLong("banExpiresTimestamp") > currentDateTime) {
                p.kickPlayer("§cDu wurdest von diesem Netzwerk ausgeschlossen." +
                        "\n\n§cDatum und Uhrzeit §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDateTime) +
                        "\n§cAusgeschlossen bis §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(playerDoc.getLong("banExpiresTimestamp")) +
                        "\n§cBegründung §7>>§f " + playerDoc.getString("banReason") +
                        "\n§cAusgeschlossen von §7>>§f " + playerDoc.getString("bannedFrom"));
                return false;
            }
        }

        PlayerData data = new PlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.group = playerDoc.getString("perm_group");
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        playerList.put(pUUID, data);
        return true;
    }

    public static PlayerData get(UUID pUUID) {
        if (!playerList.containsKey(pUUID)) {
            Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-03] Bitte kontaktiere den Support!");
            return null;
        }

        return playerList.get(pUUID);
    }

    public static void logout(UUID pUUID) { playerList.remove(pUUID); }

    public static void saveAll(UUID pUUID) {
        try {
            Document found = ServerBasics.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (found == null) {
                ServerBasics.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            ServerBasics.mongo.collections.get("players").findOneAndUpdate(found, playerList.get(pUUID).getDoc());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void addAllPerm(UUID pUUID) {
        Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);

        if (p == null) {
            ServerBasics.plugin.getLogger().severe("[PlayerApi->addAllPerm] p = null");
            return;
        }

        PlayerData pData = get(pUUID);
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        // Setzt dem Spieler die neuen vorgegebenen Permissions aus "permissionsList" und wählt die anhand von der Gruppe des Spielers.
        for (String perm : Configs.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, true);
    }

    public static void removeAllPerm(UUID pUUID) {
        Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);

        if (p == null) {
            ServerBasics.plugin.getLogger().severe("[PlayerApi->removeAllPerm] p = null");
            return;
        }

        PlayerData pData = get(pUUID);
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        // Entfernt alle Permissions von diesem Spieler indem alle Permissions aus der jetzigen Gruppe auf "false" gesetzt werden.
        for (String perm : Configs.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, false);
    }
}