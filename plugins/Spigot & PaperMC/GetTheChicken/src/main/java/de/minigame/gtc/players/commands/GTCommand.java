package de.minigame.gtc.players.commands;

import de.minigame.gtc.GTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GTCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String arg, String[] args) {
        if (!(s instanceof Player)) return false;

        Player p = (Player) s;

        if (args.length == 1) {
            switch (args[0]) {
                case "start" -> {
                    if (!GTC.preRoundStarted && !GTC.roundStarted)
                        GTC.startPreRound();
                }
                case "stop" -> {
                    if (GTC.preRoundStarted && !GTC.roundStarted)
                        GTC.stopPreRound();
                }
                default -> p.sendMessage("Wähle zwischen start und stop.");
            }
        }

        return true;
    }
}
