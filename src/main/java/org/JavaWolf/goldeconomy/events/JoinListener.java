package org.JavaWolf.goldeconomy.events;

import org.JavaWolf.goldeconomy.database.Handler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class JoinListener implements Listener {
    private final Plugin plugin;

    public JoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String UUID = String.valueOf(player.getUniqueId());
        String username = player.getName();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();

        if (!Handler.databasePlayerExist(username, databaseFile)){
            double gold = plugin.getConfig().getDouble("DEFAULT_Gold");
            double silver = plugin.getConfig().getDouble("DEFAULT_Silver");

            Handler.newPlayerRow(UUID, username , gold, silver, databaseFile);
        }
        // if uuid of username != uuid, save this new one
        if (!Handler.getUUID(databaseFile, username).equals(UUID)){
            Handler.UpdateUUID(databaseFile, username, UUID);
        }


    }
}
