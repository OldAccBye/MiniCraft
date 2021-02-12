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
                        if (!p.getName().contains(t.getName()) && !pData.friends.contains(t.getName()))
                            players.add(t.getName());

                    return players;
                }
                case "remove", "chat" -> {
                    return pData.friends;
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

        if (args[0].contains("chat") && args[1].contains(p.getName())) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst dir nicht selber eine Freundesanfrage senden!"));
            return;
        } if (args[0].contains("chat") && args.length < 3) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fGib eine Nachricht an!"));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        if (args[0].contains("accept") && !pData.friendRequest.containsKey(args[1])) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler hat dir keine Anfrage gesendet!"));
            return;
        } else if (args[0].contains("add") && pData.friends.contains(args[1])) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich bereits in deiner Freundesliste!"));
            return;
        } else if ((args[0].contains("add") || args[0].contains("accept")) && pData.friends.size() >= 25) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDu kannst nicht mehr als 25 Freunde haben!"));
            return;
        } else if ((args[0].contains("chat") || args[0].contains("remove")) && !pData.friends.contains(args[1])) {
            p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht in deiner Freundesliste!"));
            return;
        }

        switch (args[0]) {
            case "add" -> {
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
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler besitzt bereits eine Anfrage von dir!"));
                    return;
                }

                tData.friendRequest.put(p.getName(), false);

                TextComponent mainComponent = new TextComponent("§3§l[§2SERVER§3§l] §aDer Spieler ");
                TextComponent playerComponent = new TextComponent("§b" + p.getName());
                playerComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Profil anzeigen")));
                playerComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minicraft.cf/p/" + p.getName()));
                mainComponent.addExtra(playerComponent);
                mainComponent.addExtra(" §ahat dir eine Freundesanfrage gesendet. ");
                TextComponent acceptComponent = new TextComponent("§6§l[Annehmen]");
                acceptComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7<KLICK>§r Anfrage annehmen")));
                acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + p.getName()));
                mainComponent.addExtra(acceptComponent);

                p.sendMessage(mainComponent);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aFreundesanfrage gesendet!"));
            }
            case "remove" -> {
                pData.friends.remove(args[1]);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde aus der Freundesliste entfernt!"));
            }
            case "chat" -> {
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
                ProxiedPlayer t = BungeeSystem.plugin.getProxy().getPlayer(args[1]);
                if (t == null) {
                    p.sendMessage(new TextComponent("§c[FEHLER]: §fDieser Spieler befindet sich nicht mehr auf dem Netzwerk!"));
                    pData.friendRequest.remove(args[1]);
                    return;
                }

                pData.friends.add(args[1]);
                pData.friendRequest.remove(args[1]);
                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + args[1] + " §awurde in die Freundesliste hinzugefügt!"));
                t.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p.getName()+ " §ahat deine Freundesanfrage angenommen!"));
            }
            default -> p.sendMessage(new TextComponent("§c[FEHLER]: §fFunktion §6" + args[0] + " §fexistiert nicht!"));
        }
    }
}
