package de.minicraft.player.commands;

import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class FriendsCommand extends Command implements TabExecutor {
    public FriendsCommand() {
        super("friends");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer) || args.length > 2) return Collections.emptyList();

        if (args.length == 2) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
            if (pData == null) {
                p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                return Collections.emptyList();
            }

            switch (args[0]) {
                case "add" -> {
                    List<String> players = new ArrayList<>();
                    BungeeSystem.plugin.getProxy().getPlayers().stream().limit(10).filter((player) -> !player.getName().equals(p.getName())).forEach((player) -> players.add(player.getName()));
                    return players;
                }
                case "remove", "chat" -> {
                    List<String> friends = new ArrayList<>();
                    pData.data.getList("friends", String.class).forEach((tUUID) -> {
                        Document friendDocument = BungeeSystem.mongo.player.find(Filters.eq("UUID", tUUID)).projection(fields(include("username"))).first();
                        if (friendDocument != null) friends.add(friendDocument.getString("username"));
                    });
                    return friends;
                }
                case "accept" -> {
                    return pData.friendRequest;
                }
                default -> {
                    return Collections.emptyList();
                }
            }
        }

        return Arrays.asList("add", "remove", "chat", "accept");
    }

    @Override
    public void execute(CommandSender cmdSender, String[] args) {
        if (!(cmdSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) cmdSender;

        if (args.length == 0) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fWähle eine Funktion!"));
            return;
        } else if (args.length == 1) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fWähle einen Spieler!"));
            return;
        }

        if (args[1].equals(p.getName())) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst dich nicht selbst angeben!"));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());

        ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
        PlayerData tData;

        switch (args[0]) {
            case "add" -> {
                { // CHECK
                    if (t == null) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                        return;
                    }

                    if (pData.data.getList("friends", String.class).contains(t.getUniqueId().toString())) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich bereits in deiner Freundesliste!"));
                        return;
                    } else if (pData.data.getList("friends", String.class).size() >= 25) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst nicht mehr als 25 Freunde besitzen!"));
                        return;
                    }

                    tData = BungeeSystem.playerList.get(t.getUniqueId());

                    if (tData.friendRequest.contains(p.getName())) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler besitzt bereits eine Freundesanfrage von dir!"));
                        return;
                    }
                } // CHECK

                tData.friendRequest.add(p.getName());

                // Components
                TextComponent mainComponent = new TextComponent("§3§l[§2SERVER§3§l] §aDer Spieler "),
                        playerComponent = new TextComponent("§b" + p.getName()),
                        acceptComponent = new TextComponent("§6§l[Annehmen]");

                // Player component
                playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Profil anzeigen")));
                playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minicraft.network/p/" + p.getUniqueId().toString()));
                mainComponent.addExtra(playerComponent);

                mainComponent.addExtra(" §ahat dir eine Freundesanfrage gesendet. ");

                // Accept component
                acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Anfrage annehmen")));
                acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + p.getName()));
                mainComponent.addExtra(acceptComponent);

                t.sendMessage(mainComponent);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aFreundesanfrage gesendet!"));
            }
            case "remove" -> {
                String tUUID;

                { // CHECK
                    if (t != null) {
                        if (!pData.data.getList("friends", String.class).contains(t.getUniqueId().toString())) {
                            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
                            return;
                        }

                        tUUID = t.getUniqueId().toString();
                        BungeeSystem.playerList.get(t.getUniqueId()).data.getList("friends", String.class).remove(p.getUniqueId().toString());
                        t.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p.getName() + " §ahat dich aus der Freundesliste entfernt!"));
                    } else {
                        Document tDoc = BungeeSystem.mongo.player.find(Filters.eq("username", args[1])).projection(fields(include("friends"), include("UUID"))).first();
                        if (tDoc == null) {
                            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler existiert nicht!"));
                            return;
                        }

                        if (!pData.data.getList("friends", String.class).contains(tDoc.getString("UUID"))) {
                            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
                            return;
                        }

                        tUUID = tDoc.getString("UUID");
                        tDoc.getList("friends", String.class).remove(p.getUniqueId().toString());
                        BungeeSystem.mongo.player.updateOne(new Document("username", args[1]), new Document("$set", new Document("friends", tDoc.getList("friends", String.class))));
                    }
                } // CHECK

                pData.data.getList("friends", String.class).remove(tUUID);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde aus der Freundesliste entfernt!"));
            }
            case "chat" -> {
                { // CHECK
                    if (t == null) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                        return;
                    }

                    if (args.length < 3) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fEine Nachricht fehlt!"));
                        return;
                    }
                    else if (!pData.data.getList("friends", String.class).contains(t.getUniqueId().toString())) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
                        return;
                    }
                } // CHECK

                StringJoiner message = new StringJoiner(" ");
                for (String msg : Arrays.copyOfRange(args, 2, args.length))
                    message.add(msg);

                t.sendMessage(new TextComponent("§7[§dFC§7] §r" + p.getName() + ": " + message));
                p.sendMessage(new TextComponent("§7[§dFC§7] §r" + p.getName() + ": " + message));
            }
            case "accept" -> {
                { // CHECK
                    boolean friendRequest = pData.friendRequest.contains(args[1]);

                    if (t == null) {
                        if (!friendRequest)
                            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler hat dir keine Freundesanfrage gesendet und ist nicht auf dem Netzwerk!"));
                        else {
                            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht mehr auf dem Netzwerk!"));
                            pData.friendRequest.remove(args[1]);
                        }
                        return;
                    }

                    tData = BungeeSystem.playerList.get(t.getUniqueId());

                    if (!friendRequest) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler hat dir keine Freundesanfrage gesendet!"));
                        return;
                    }

                    pData.friendRequest.remove(args[1]);

                    if (pData.data.getList("friends", String.class).contains(t.getUniqueId().toString())) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDiese Person befindet sich bereits in deiner Freundesliste!"));
                        return;
                    } else if (pData.data.getList("friends", String.class).size() >= 25) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDeine Grenze an 25 Freunde wurde erreicht!"));
                        return;
                    }
                } // CHECK

                tData.data.getList("friends", String.class).add(p.getUniqueId().toString());
                pData.data.getList("friends", String.class).add(t.getUniqueId().toString());
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde in die Freundesliste hinzugefügt!"));
                t.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p.getName()+ " §ahat deine Freundesanfrage angenommen!"));
            }
            default -> p.sendMessage(new TextComponent("§c[FEHLER]: §fFunktion §6" + args[0] + " §fexistiert nicht!"));
        }
    }
}
