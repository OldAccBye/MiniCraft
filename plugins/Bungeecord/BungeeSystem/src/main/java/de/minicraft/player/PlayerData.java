package de.minicraft.player;

import de.minicraft.Configs;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public Document data = null;
    public List<String> permissions = new ArrayList<>(), friendRequest = new ArrayList<>();

    public void updatePermissions() {
        for (String permissionKey : Configs.permissionsList.getKeys()) {
            this.permissions.addAll(Configs.permissionsList.getStringList(permissionKey));
            if (permissionKey.equals(this.data.getString("group"))) break;
        }
    }
}