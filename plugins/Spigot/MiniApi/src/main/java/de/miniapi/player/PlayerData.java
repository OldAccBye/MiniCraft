package de.miniapi.player;

import de.miniapi.Configs;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public Document data = null, gameData = null;
    public String prefix = null;
    public List<String> permissions = new ArrayList<>();

    public void updatePermissions() {
        this.permissions.clear();

        for (String permissionKey : Configs.permissionsList.getKeys(false)) {
            this.permissions.addAll(Configs.permissionsList.getStringList(permissionKey + ".permissions"));
            if (permissionKey.equals(this.data.getString("group"))) break;
        }
    }
}
