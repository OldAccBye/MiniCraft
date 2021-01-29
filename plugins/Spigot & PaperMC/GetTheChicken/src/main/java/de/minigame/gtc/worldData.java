package de.minigame.gtc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class worldData {
    public boolean roundStarted = false, preRoundStarted = false;
    public int roundTaskId, preRoundTaskId, preRoundSeconds, roundSeconds, playTime, preTime, players, maxPlayers, playersToStart;
    public Location spawnLocation, roundLocation;
    public List<Location> chickenLocation = new ArrayList<>();
    public Entity lastChicken;
    public UUID topPlayer;
}