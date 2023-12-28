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

import static org.JavaWolf.goldeconomy.database.AdminDB.resetBalance;
import static org.JavaWolf.goldeconomy.utils.Utils.GetMessage;

public class EcoresetCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public EcoresetCommand(Plugin plugin) {
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

        if (!player.hasPermission("GoldEconomy.eco-resetCommandPermission")){
            player.sendMessage(GetMessage(messages_path, "INVALID_PERMISSIONS"));
            plugin.getLogger().warning("user " + player.getName() + " tryed to executed /eco-reset command.");
            plugin.getLogger().warning("user dont dispose for permission: GoldEconomy.eco-resetCommandPermission");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /eco-reset <player>");
            return true;
        }


        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(GetMessage(messages_path, "PLAYER_OFFLINE").replace("%player%", target.getName()));
            return true;
        }

        double GOLD = plugin.getConfig().getDouble("DEFAULT_Gold");
        double SILVER = plugin.getConfig().getDouble("DEFAULT_Silver");

        boolean status = resetBalance(String.valueOf(target.getUniqueId()), GOLD, SILVER, databaseFile);

        String success = "§aSuccessful reset "+ target.getName() + " balance.";
        String failure = "§4Failed to reset "+ target.getName() + " balance.";
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
        }

        return completions;
    }
}