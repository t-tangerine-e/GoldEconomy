package org.JavaWolf.goldeconomy.commands.admins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.JavaWolf.goldeconomy.database.AdminDB.addToBalance;
import static org.JavaWolf.goldeconomy.utils.Utils.GetMessage;

public class EcogiveCommand  implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public EcogiveCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();
        String coin;
        Player receiver;
        double amount;


        if (!(sender instanceof Player)) {
            sender.sendMessage(GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("GoldEconomy.eco-giveCommandPermission")){
            player.sendMessage(GetMessage(messages_path, "INVALID_PERMISSIONS"));
            plugin.getLogger().warning("user " + player.getName() + " tryed to executed /eco-give command.");
            plugin.getLogger().warning("user dont dispose for permission: GoldEconomy.eco-giveCommandPermission");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /eco-give <player> <amount> <GOLD/SILVER>");
            return true;
        }


        receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null || !receiver.isOnline()) {
            player.sendMessage(GetMessage(messages_path, "PLAYER_OFFLINE").replace("%player%", args[0]));
            return true;
        }

        // get amount
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(GetMessage(messages_path, "INVALID_AMOUNT"));
            return true;
        }

        // Get coin type (GOLD/SILVER)

        coin = args[2].toUpperCase();


        if (!coin.equals("GOLD") && !coin.equals("SILVER")) {

            player.sendMessage(GetMessage(messages_path, "INVALID_COIN_TYPE"));
            return true;

        }


        boolean status = addToBalance(String.valueOf(receiver.getUniqueId()), amount, coin, databaseFile);

        String success = "§aSuccessful added" + amount + " to " + receiver.getName() + "'s balance.";
        String failure = "§4Failed to add "+ amount + " of " + coin + " to "  + receiver.getName() + "'s balance.";
        if (status)
            player.sendMessage(success);
        else
            player.sendMessage(failure);










        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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

