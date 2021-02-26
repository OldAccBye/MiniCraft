package de.minicraft.player;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {
    public Document data = null;
    public List<String> permissions = new ArrayList<>();
    public HashMap<String, Boolean> friendRequest = new HashMap<>();
}