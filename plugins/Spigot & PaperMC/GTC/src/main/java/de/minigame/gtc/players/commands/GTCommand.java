package de.minigame.gtc.players.commands;

import de.minigame.gtc.GTC;
import de.minigame.gtc.WorldData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GTCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String arg, String[] args) {
        if (!(s instanceof Player)) return false;

        Player p = (Player) s;
        String worldName = p.getWorld().getName();
        WorldData world = GTC.worldLists.get(worldName);

        if (args.length == 1) {
            switch (args[0]) {
                case "start" -> {
                    if (!world.preRoundStarted && !world.roundStarted)
                        world.startPreRound();
                }
                case "stop" -> {
                    if (world.preRoundStarted && !world.roundStarted)
                        world.stopPreRound();
                }
                default -> p.sendMessage("§3GTC §7| §6Wähle zwischen start und stop.");
            }
        }

        return true;
    }
}
