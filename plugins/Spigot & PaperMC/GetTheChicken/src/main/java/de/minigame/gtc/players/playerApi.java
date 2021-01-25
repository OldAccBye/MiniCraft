package de.minigame.gtc.players;

import java.util.HashMap;
import java.util.UUID;

public class playerApi {
    public static final HashMap<UUID, playerData> playerList = new HashMap<>();

    public static void login(UUID pUUID) {
        playerData pData = new playerData();
        pData.pUUID = pUUID;
        pData.inRound = false;
        pData.kills = 0;
        playerList.put(pUUID, pData);
    }

    public static void logout(UUID pUUID) { playerList.remove(pUUID); }
}