package org.JavaWolf.goldeconomy.commands.admins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.JavaWolf.goldeconomy.database.AdminDB.setBalance;
import static org.JavaWolf.goldeconomy.utils.Utils.GetMessage;

public class EcosetCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public EcosetCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();

        if (!(sender instanceof Player)) {
            sender.sendMessage(GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("GoldEconomy.eco-setCommandPermission")){
            player.sendMessage(GetMessage(messages_path, "INVALID_PERMISSIONS"));
            plugin.getLogger().warning("user " + player.getName() + " tryed to executed /eco-set command.");
            plugin.getLogger().warning("user dont dispose for permission: GoldEconomy.eco-setCommandPermission");
            return true;
        }

        // /eco-set <player> <amount> <coin>


        if (args.length < 3) {
            sender.sendMessage("Usage: /eco-set <player> <amount> <coin>");
            return true;
        }


        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(GetMessage(messages_path, "PLAYER_OFFLINE").replace("%player%", target.getName()));
            return true;
        }


        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(GetMessage(messages_path, "INVALID_AMOUNT"));
            return true;
        }


        String coin = args[2].toUpperCase();
        if (!coin.equals("GOLD") && !coin.equals("SILVER")) {
            sender.sendMessage("Invalid coin type! Use GOLD or SILVER.");
            return true;
        }


        boolean result = setBalance(String.valueOf(target.getUniqueId()), amount, coin, databaseFile);
        String coin_color;
        if (coin.equals("GOLD")) {
            coin_color = "§6";
        } else {
            coin_color = "§7";
        }


        // use "string" + var + "string" if this represent a performance issue > 800ns
        String success = "§aSuccessful set §5%target%§a's " + coin_color+ "%currency%§a balance to §9%amount%";
        String failure = "§4Failed to set §5%target%§4's " + coin_color + "%currency%§4 to desidered value";
        if (result){
            player.sendMessage(success
                                    .replace("%target%", target.getName())
                                    .replace("%currency%", coin)
                                    . replace("%amount%", String.valueOf(amount)));
        } else {
            player.sendMessage(failure
                                    .replace("%target%", target.getName())
                                    .replace("%currency%", coin));
        }



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