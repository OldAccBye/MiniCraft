package de.minicraft.player.commands;

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

import java.util.*;

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
                    for (ProxiedPlayer t : BungeeSystem.plugin.getProxy().getPlayers())
                        if (!p.getName().contains(t.getName()) && !pData.data.getList("friends", String.class).contains(t.getName()))
                            players.add(t.getName());

                    return players;
                }
                case "remove", "chat" -> {
                    return pData.data.getList("friends", String.class);
                }
                case "accept" -> {
                    return new ArrayList<>(pData.friendRequest.keySet());
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

        switch (args.length) {
            case 0 -> {
                p.sendMessage(new TextComponent("§c[FEHLER]: §fWähle eine Funktion!"));
                return;
            }
            case 1 -> {
                p.sendMessage(new TextComponent("§c[FEHLER]: §fWähle einen Spieler!"));
                return;
            }
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        switch (args[0]) {
            case "add" -> {
                { // CHECK
                    if (args[1].equals(p.getName())) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst dir nicht selber eine Freundesanfrage senden!"));
                        return;
                    }
                    else if (pData.data.getList("friends", String.class).contains(args[1])) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich bereits in deiner Freundesliste!"));
                        return;
                    }
                    else if (pData.data.getList("friends", String.class).size() >= 25) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst nicht mehr als 25 Freunde haben!"));
                        return;
                    }
                } // CHECK

                ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
                if (t == null) {
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                    return;
                }
                PlayerData tData = BungeeSystem.playerList.get(t.getUniqueId());
                if (tData == null) {
                    t.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                    return;
                }

                if (tData.friendRequest.containsKey(p.getName())) {
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler besitzt bereits eine Freundesanfrage von dir!"));
                    return;
                }

                tData.friendRequest.put(p.getName(), false);

                // Components
                TextComponent mainComponent = new TextComponent("§3§l[§2SERVER§3§l] §aDer Spieler "),
                        playerComponent = new TextComponent("§b" + p.getName()),
                        acceptComponent = new TextComponent("§6§l[Annehmen]");

                // Player component
                playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Profil anzeigen")));
                playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minicraft.network/p/" + p.getName()));
                mainComponent.addExtra(playerComponent);

                mainComponent.addExtra(" §ahat dir eine Freundesanfrage gesendet. ");

                // Accept component
                acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Anfrage annehmen")));
                acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + p.getName()));
                mainComponent.addExtra(acceptComponent);

                p.sendMessage(mainComponent);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aFreundesanfrage gesendet!"));
            }
            case "remove" -> {
                { // CHECK
                    if (!pData.data.getList("friends", String.class).contains(args[1])) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
                        return;
                    }
                } // CHECK

                pData.data.getList("friends", String.class).remove(args[1]);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde aus der Freundesliste entfernt!"));
            }
            case "chat" -> {
                { // CHECK
                    if (args.length < 3) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fEine Nachricht fehlt!"));
                        return;
                    }
                    else if (!pData.data.getList("friends", String.class).contains(args[1])) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
                        return;
                    }
                } // CHECK

                ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
                if (t == null) {
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                    return;
                }

                StringJoiner message = new StringJoiner(" ");
                for (String msg : Arrays.copyOfRange(args, 2, args.length))
                    message.add(msg);

                t.sendMessage(new TextComponent("§7[§dFC§7] §r" + p.getName() + ": " + message));
                p.sendMessage(new TextComponent("§7[§dFC§7] §r" + p.getName() + ": " + message));
            }
            case "accept" -> {
                { // CHECK
                    if (pData.data.getList("friends", String.class).size() >= 25) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst nicht mehr als 25 Freunde haben!"));
                        return;
                    }
                    else if (!pData.friendRequest.containsKey(args[1])) {
                        p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler hat dir keine Anfrage gesendet!"));
                        return;
                    }
                } // CHECK

                ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
                if (t == null) {
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht mehr auf dem Netzwerk!"));
                    pData.friendRequest.remove(args[1]);
                    return;
                }
                PlayerData tData = BungeeSystem.playerList.get(t.getUniqueId());
                if (tData == null) {
                    t.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht auf dem Netzwerk!"));
                    pData.friendRequest.remove(args[1]);
                    return;
                }

                tData.data.getList("friends", String.class).add(p.getName());
                pData.data.getList("friends", String.class).add(args[1]);
                pData.friendRequest.remove(args[1]);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde in die Freundesliste hinzugefügt!"));
                t.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p.getName()+ " §ahat deine Freundesanfrage angenommen!"));
            }
            default -> p.sendMessage(new TextComponent("§c[FEHLER]: §fFunktion §6" + args[0] + " §fexistiert nicht!"));
        }
    }
}
