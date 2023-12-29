package org.JavaWolf.goldeconomy.commands;

import org.JavaWolf.goldeconomy.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class WithdrawCommand implements CommandExecutor {

    private final Plugin plugin;

    public WithdrawCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();

        if (label.equalsIgnoreCase("withdraw")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /withdraw <amount> <GOLD/SILVER>");
                return true;
            }


            double amount;
            try {
                amount = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Utils.GetMessage(messages_path, "INVALID_AMOUNT"));
                return true;
            }
            String coin = args[1];

            if (coin.equalsIgnoreCase("GOLD")){

            }




            return true;
        }
        return false;
    }
}
