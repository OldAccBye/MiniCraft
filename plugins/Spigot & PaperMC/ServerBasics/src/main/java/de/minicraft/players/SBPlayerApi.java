package de.minicraft.players;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.SBConfig;
import de.minicraft.ServerBasics;
import org.bson.Document;

import org.bukkit.entity.Player;

public class SBPlayerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    public static final HashMap<UUID, SBPlayerData> playerList = new HashMap<>();

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
            p.kickPlayer("[SBPlayerApi->register] Player could not be saved.");
            return;
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[SBPlayerApi->register] Something went wrong.");
            return;
        }

        SBPlayerData data = new SBPlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.language = "en";
        data.group = "default";
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        playerList.put(pUUID, data);

        p.sendMessage(SBConfig.getLanguageText(pUUID, "languageSetTo").replace("%l%", "en"));
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
            p.kickPlayer("[SBPlayerApi->login] Something went wrong.");
            return false;
        }

        if (playerDoc.getBoolean("banned")) {
            Date date = new Date();
            Long currentDateTime = date.getTime();
            if (playerDoc.getLong("banExpiresTimestamp") > currentDateTime) {
                p.kickPlayer("§cYou have been banned from this network." +
                        "\n\nTime zone §7>>§f Europe/Berlin" +
                        "\n§cDate and time §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDateTime) +
                        "\n§cBanned until §7>>§f " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(playerDoc.getLong("banExpiresTimestamp")) +
                        "\n§cReason §7>>§f " + playerDoc.getString("banReason") +
                        "\n§cBanned from §7>>§f " + playerDoc.getString("bannedFrom"));
                return false;
            }
        }

        SBPlayerData data = new SBPlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.language = playerDoc.getString("language");
        data.group = playerDoc.getString("perm_group");
        data.banned = false;
        data.banSinceTimestamp = 0L;
        data.banExpiresTimestamp = 0L;
        data.banReason = "";
        data.bannedFrom = "";
        playerList.put(pUUID, data);

        p.sendMessage(SBConfig.getLanguageText(pUUID, "languageSetTo").replace("%l%", playerDoc.getString("language")));
        return true;
    }

    public static SBPlayerData get(UUID pUUID) {
        if (!playerList.containsKey(pUUID)) {
            Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-03] Please contact the support!");
            return null;
        }

        return playerList.get(pUUID);
    }

    public static void logout(UUID pUUID) { playerList.remove(pUUID); }

    public static void saveAll(UUID pUUID) {
        try {
            Document found = ServerBasics.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (found == null) {
                ServerBasics.plugin.getLogger().severe("[SBPlayerData->saveAll] Player [" + pUUID.toString() + "] not found!");
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
            ServerBasics.plugin.getLogger().severe("[Permissions] PlayerListener = null");
            return;
        }

        SBPlayerData pData = get(pUUID);
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return;
        }

        // Setzt dem Spieler die neuen vorgegebenen Permissions aus "permissionsList" und wählt die anhand von der Gruppe des Spielers.
        for (String perm : SBConfig.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, true);
    }

    public static void removeAllPerm(UUID pUUID) {
        Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);

        if (p == null) {
            ServerBasics.plugin.getLogger().severe("[Permissions] PlayerListener = null");
            return;
        }

        SBPlayerData pData = get(pUUID);
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return;
        }

        // Entfernt alle Permissions von diesem Spieler indem alle Permissions aus der jetzigen Gruppe auf "false" gesetzt werden.
        for (String perm : SBConfig.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, false);
    }
}