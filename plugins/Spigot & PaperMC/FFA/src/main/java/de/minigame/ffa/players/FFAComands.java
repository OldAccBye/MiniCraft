package de.minigame.ffa.players;

import de.minigame.ffa.FFA;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class FFAComands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String arg2, String[] args) {
        if (!(s instanceof Player)) return false;
        Player p = (Player) s;

        File file = new File("plugins//FFA//spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        Location loc = p.getLocation();
        FFA.spawnLoc = loc;
        cfg.set("spawn.x", loc.getX());
        cfg.set("spawn.y", loc.getY());
        cfg.set("spawn.z", loc.getZ());
        cfg.set("spawn.yaw", loc.getYaw());
        cfg.set("spawn.pitch", loc.getPitch());
        try {
            cfg.save(file);
            p.sendMessage("§3FFA §7| §fSPAWN GESETZT!");
        } catch (IOException e) {
            e.printStackTrace();
            p.sendMessage("§3FFA §7| §fSPAWN NICHT GESETZT!");
        }

        return true;
    }
}
