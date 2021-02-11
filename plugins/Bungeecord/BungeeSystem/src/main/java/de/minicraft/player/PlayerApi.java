package de.minicraft.player;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PlayerApi {
    private static Document register(UUID pUUID, ProxiedPlayer p) {
        try {
            BungeeSystem.mongo.collections.get("players").insertOne(new Document("username", p.getName())
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
            return new Document("status", "error").append("reason", "[PlayerApi->register] Daten konnte nicht gespeichert werden.");
        } catch (MongoException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->register] Irgendetwas ist schief gelaufen.");
        }

        PlayerData data = new PlayerData();
        data.username = p.getName();
        data.group = "default";
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        BungeeSystem.playerList.put(pUUID, data);

        return new Document("status", "success");
    }

    public static Document login(UUID pUUID) {
        ProxiedPlayer p = BungeeSystem.plugin.getProxy().getPlayer(pUUID);
        if (p == null) return new Document("status", "error").append("reason", "[PlayerApi->login] p = null");

        // Diese Funktion prüft ob dieser Spieler bereits eingetragen ist und wenn ja entfernt diese Funktion diesen Eintrag
        logout(pUUID);

        // Daten aus der Datenbank werden hier zwischen gespeichert
        Document playerDoc;

        try {
            playerDoc = BungeeSystem.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null)
                return register(pUUID, p);
        } catch (MongoException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->login] Irgendetwas ist schief gelaufen.");
        }

        if (playerDoc.getBoolean("banned")) {
            Date date = new Date();
            Long currentDateTime = date.getTime();
            if (playerDoc.getLong("banExpiresTimestamp") > currentDateTime) {
                return new Document("status", "error").append("reason", "§cDu wurdest von diesem Netzwerk ausgeschlossen." +
                        "\n\n§cDatum und Uhrzeit §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDateTime) +
                        "\n§cAusgeschlossen bis §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(playerDoc.getLong("banExpiresTimestamp")) +
                        "\n§cBegründung §7>>§f " + playerDoc.getString("banReason") +
                        "\n§cAusgeschlossen von §7>>§f " + playerDoc.getString("bannedFrom"));
            }
        }

        PlayerData data = new PlayerData();
        data.username = p.getName();
        data.group = playerDoc.getString("perm_group");
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        BungeeSystem.playerList.put(pUUID, data);
        return new Document("status", "success");
    }

    public static void logout(UUID pUUID) { BungeeSystem.playerList.remove(pUUID); }
    public static boolean exists(UUID pUUID) { return BungeeSystem.playerList.get(pUUID) != null; }

    public static void saveAll(UUID pUUID) {
        try {
            Document found = BungeeSystem.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (found == null) {
                BungeeSystem.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            BungeeSystem.mongo.collections.get("players").findOneAndUpdate(found, BungeeSystem.playerList.get(pUUID).getDoc());
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
