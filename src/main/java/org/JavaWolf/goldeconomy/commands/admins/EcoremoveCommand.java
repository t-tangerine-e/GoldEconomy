package org.JavaWolf.goldeconomy.commands.admins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.JavaWolf.goldeconomy.database.Handler.removeFromBalance;
import static org.JavaWolf.goldeconomy.utils.Utils.GetMessage;

public class EcoremoveCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public EcoremoveCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args) {
        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();
        String coin;
        Player receiver;
        double amount;






        if (!sender.hasPermission("GoldEconomy.eco-removeCommandPermission")){
            sender.sendMessage(GetMessage(messages_path, "INVALID_PERMISSIONS"));
            plugin.getLogger().warning("user " + sender.getName() + " tryed to executed /eco-remove command.");
            plugin.getLogger().warning("user dont dispose for permission: GoldEconomy.eco-removeCommandPermission");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /eco-remove <player> <amount> <GOLD/SILVER>");
            return true;
        }


        receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null || !receiver.isOnline()) {
            sender.sendMessage(GetMessage(messages_path, "PLAYER_OFFLINE").replace("%player%", args[0]));
            return true;
        }


        // get amount
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(GetMessage(messages_path, "INVALID_AMOUNT"));
            return true;
        }

        // Get coin type (GOLD/SILVER)

        coin = args[2].toUpperCase();


        if (!coin.equals("GOLD") && !coin.equals("SILVER")) {

            sender.sendMessage(GetMessage(messages_path, "INVALID_COIN_TYPE"));
            return true;

        }
        String coin_color;
        if (coin.equals("GOLD")) {
            coin_color = "§6";
        } else {
            coin_color = "§7";
        }


        boolean status = removeFromBalance(String.valueOf(receiver.getUniqueId()), amount, coin, databaseFile);

        String success = "§aSuccessful removed §5" + amount + "§a " + coin_color + coin + "§a from §9" + receiver.getName() + "§a's balance.";
        String failure = "§4Failed to remove §5"+ amount + "§4 of " + coin_color + coin + "§4 from §9"  + receiver.getName() + "§4's balance.";
        if (status)
            sender.sendMessage(success);
        else
            sender.sendMessage(failure);










        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,@NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        } else if (args.length == 2) {

            completions.add("<amount>");
        } else if (args.length == 3) {

            completions.add("GOLD");
            completions.add("SILVER");
        }

        return completions;
    }
}

