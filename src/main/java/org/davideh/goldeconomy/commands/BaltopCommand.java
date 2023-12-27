package org.davideh.goldeconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;

import static org.davideh.goldeconomy.database.Handler.*;
import static org.davideh.goldeconomy.utils.Utils.GetMessage;


public class BaltopCommand implements CommandExecutor {

    private final Plugin plugin;

    public BaltopCommand(Plugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();
        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();

        Map<String, Double> sortedSums = getSortedMap(databaseFile, plugin.getConfig().getInt("VALUE_OF_1_GOLD_IN_SILVER"));

        StringBuilder baltop = new StringBuilder();

        String header = GetMessage(messages_path, "BALTOP_HEADER");
        String body = GetMessage(messages_path, "BALTOP_BODY");
        String footer = GetMessage(messages_path, "BALTOP_FOOTER");

        String message = header;
        String username, gold, silver;

        int loopCounter = 0;
        for (Map.Entry<String, Double> entry : sortedSums.entrySet()) {

            if (loopCounter >= 10) {
                break;
            }

            username = getUsername(databaseFile, entry.getKey()); // Assuming the method exists
            gold = String.valueOf(getPlayerBalance(entry.getKey(), "GOLD", databaseFile)); // Assuming the method exists
            silver = String.valueOf(getPlayerBalance(entry.getKey(), "SILVER", databaseFile)); // Assuming the method exists

            String formattedBody = body.replace("%player%", username)
                    .replace("%gold%", gold)
                    .replace("%silver%", silver);

            message += "\n" + formattedBody;

            loopCounter++;
        }


        message += "\n" + footer;


        // header
        // %username% : %gold% %silver% (for each)
        // footer


        commandSender.sendMessage(message);

        return false;
    }
}
