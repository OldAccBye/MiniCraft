package de.miniapi.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.miniapi.Configs;
import de.miniapi.MiniApi;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;

public class PlayerApi {
    public static boolean login(Player p) {
        Document playerDoc = getData(p, "playerData");
        if (playerDoc == null) return false;
        playerDoc.replace("username", p.getName());

        PlayerData pData = new PlayerData();
        pData.data = playerDoc;
        pData.prefix = Configs.permissionsList.getString(pData.data.getString("group") + ".prefix");
        pData.gameData = getData(p, "gameData");
        pData.updatePermissions();
        p.updateCommands();
        MiniApi.playerList.put(p.getUniqueId(), pData);

        return MiniApi.playerList.containsKey(p.getUniqueId());
    }

    public static Document getData(Player p, String collection) {
        Document playerDoc = new Document();

        try {
            switch (collection) {
                case "playerData" -> playerDoc = MiniApi.mongo.playerData.find(Filters.eq("UUID", p.getUniqueId().toString())).projection(fields(exclude("_id"), exclude("UUID"), exclude("friends"))).first();
                case "gameData" -> {
                    if (MiniApi.mongo.gameData == null) return null;
                    playerDoc = MiniApi.mongo.gameData.find(Filters.eq("UUID", p.getUniqueId().toString())).projection(fields(exclude("_id"), exclude("UUID"))).first();

                    if (playerDoc == null) {
                        switch (Configs.serverName) {
                            case "ffa" -> playerDoc = new Document("UUID", p.getUniqueId().toString()).append("kills", 0).append("deaths", 0);
                            case "gtc" -> playerDoc = new Document("UUID", p.getUniqueId().toString()).append("won", 0);
                            default -> {
                                return null;
                            }
                        }

                        MiniApi.mongo.gameData.insertOne(playerDoc);
                        playerDoc.remove("UUID");
                        return playerDoc;
                    }
                }
            }
        } catch (MongoException e) {
            e.printStackTrace();
            return null;
        }

        return playerDoc;
    }

    public static void saveAll(UUID pUUID) {
        try {
            PlayerData pData = MiniApi.playerList.get(pUUID);

            if (pData == null) {
                MiniApi.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            MiniApi.mongo.playerData.findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.data));

            if (pData.gameData != null)
                MiniApi.mongo.gameData.findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.gameData));
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void checkPremium(Player p) {
        new BukkitRunnable() {
            long premiumTimestamp = -1;

            @Override
            public void run() {
                if (p == null) {
                    cancel();
                    return;
                }

                if (premiumTimestamp == -1) {
                    PlayerData pData = MiniApi.playerList.get(p.getUniqueId());
                    if (pData == null) return;

                    if (pData.data.getLong("premiumTimestamp") == 0) {
                        cancel();
                        return;
                    }

                    premiumTimestamp = pData.data.getLong("premiumTimestamp");
                }

                long currentTimestamp = new Date().getTime();
                if (premiumTimestamp >= currentTimestamp) return;

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("update");
                out.writeUTF("player");
                p.sendPluginMessage(MiniApi.plugin, "bungeesystem:miniapi", out.toByteArray());

                PlayerData pData = MiniApi.playerList.get(p.getUniqueId());
                pData.data.replace("group", "player");
                pData.data.replace("premiumTimestamp", 0L);
                pData.prefix = Configs.permissionsList.getString("default.prefix");
                pData.updatePermissions();
                p.updateCommands();

                p.sendMessage(Component.text("§3§l[§2SERVER§3§l] §aDeine Premium-Mitgliedschaft ist abgelaufen!"));
                MiniApi.plugin.getLogger().warning("[PR] Premium-Mitgliedschaft von " + p.getName() + " ist abgelaufen!");
                cancel();
            }
        }.runTaskTimer(MiniApi.plugin, 20L, 20L);
    }
}
