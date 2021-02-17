package de.minicraft.minibasics.player;

import de.minicraft.minibasics.Configs;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public String group = "default";
    public List<String> permissions = new ArrayList<>();

    public PlayerData(String group) { this.group = group; }

    public void updatePermissions() {
        this.permissions.clear();

        for (String permissionKey : Configs.permissionsList.getKeys(false)) {
            this.permissions.addAll(Configs.permissionsList.getStringList(permissionKey + ".permissions"));
            if (permissionKey.equals(this.group)) break;
        }
    }
}
