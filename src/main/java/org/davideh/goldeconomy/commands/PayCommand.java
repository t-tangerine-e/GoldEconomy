package org.davideh.goldeconomy.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.davideh.goldeconomy.database.Handler.*;
import static org.davideh.goldeconomy.utils.Utils.GetMessage;
import static org.davideh.goldeconomy.utils.Utils.SaveLog;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public PayCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String coin;
        Player receiver;
        double amount;
        boolean transaction = false;

        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();
        // String message = GetMessage(filePath, key);



        // Phase 1: check command validity
        if (!(sender instanceof Player)) {
            sender.sendMessage(GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }

        Player player = (Player) sender;

        // arg len check
        if (args.length < 2) {
            player.sendMessage("Usage: /pay <player> <amount> <GOLD/SILVER>");
            return true;
        }

        // save reciver
        receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null || !receiver.isOnline()) {
            player.sendMessage(GetMessage(messages_path, "PLAYER_OFFLINE").replace("%player%", args[0]));
            return true;
        }
        if (receiver.equals(sender)){
            player.sendMessage(GetMessage(messages_path, "CHOSE_ANOTHER_PLAYER"));
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

        // Phase 2: check sender balance & execution

        double sender_balance = getPlayerBalance(String.valueOf(((Player) sender).getUniqueId()), coin, databaseFile);

        if (sender_balance < amount) {
            sender.sendMessage(GetMessage(messages_path, "NOT_ENOUGH_MONEY"));
            return true;
        } else {

            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");


            String log = currentTime.format(formatter) +"\tSender : " + sender.getName() + "\t| Amount: " + amount + "\t-> Reciver: "+ receiver.getName();
            File Transactions = new File(plugin.getDataFolder(), "transactions.log");
            SaveLog(Transactions, log);
            transaction = executeTransaction(String.valueOf(((Player) sender).getUniqueId()),String.valueOf(receiver.getUniqueId()), amount, coin, databaseFile);

        }

        if (transaction) { // %sender% , %reciver% , %cointype% , %amount%
            sender.sendMessage(GetMessage(messages_path, "TRANSACTION_SUCCESS_SENDER").replace("%sender%", sender.getName()).replace("%reciver%", receiver.getName()).replace("%cointype%", coin).replace("%amount%", String.valueOf(amount)));
            receiver.sendMessage(GetMessage(messages_path, "TRANSACTION_SUCCESS_RECIVER").replace("%sender%", sender.getName()).replace("%reciver%", receiver.getName()).replace("%cointype%", coin).replace("%amount%", String.valueOf(amount)));
        } else {
            sender.sendMessage(GetMessage(messages_path, "TRANSACTION_FAILURE_SENDER").replace("%sender%", sender.getName()).replace("%reciver%", receiver.getName()).replace("%cointype%", coin).replace("%amount%", String.valueOf(amount)));
        }



        return true;
    }

    // struct:
    //         /pay <player> <amount> <GOLD/SILVER>

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }

        } else if (args.length == 2) {
            completions.add("amount");
        } else if (args.length == 3) {
            completions.add("GOLD");
            completions.add("SILVER");
        }


        List<String> suggestions = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();

        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(currentArg)) {
                suggestions.add(completion);
            }
        }

        return suggestions;
    }





}

