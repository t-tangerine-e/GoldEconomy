package org.JavaWolf.goldeconomy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static org.JavaWolf.goldeconomy.database.Handler.getPlayerBalance;
import static org.JavaWolf.goldeconomy.utils.Utils.GetMessage;
import static org.JavaWolf.goldeconomy.utils.Utils.roundDouble;

public class BalanceCommand implements CommandExecutor {

    private final Plugin plugin;

    public BalanceCommand(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String coin;
        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();

        double gold;
        double silver;
        String message;




        if (!(sender instanceof Player)) {
            sender.sendMessage(GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }

        Player player = (Player) sender;

        // /balance SILVER | GOLD || BOTH (null)
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            coin = args[0].toUpperCase();
        } else {
            coin = "x";
        }
        if (!coin.equals("GOLD") && !coin.equals("SILVER")) {
            // both

            gold = roundDouble(getPlayerBalance(String.valueOf(player.getUniqueId()), "GOLD", databaseFile));
            silver = roundDouble(getPlayerBalance(String.valueOf(player.getUniqueId()), "SILVER", databaseFile));

            message = GetMessage(messages_path, "BALANCE_BOTH").replace("%gold%", String.valueOf(gold)).replace("%silver%", String.valueOf(silver));

        } else {
            if (coin.equals("GOLD")){
                // GOLD

                gold = roundDouble(getPlayerBalance(String.valueOf(player.getUniqueId()), "GOLD", databaseFile));
                message = GetMessage(messages_path, "BALANCE_BOTH").replace("%gold%", String.valueOf(gold));

            } else {
                // SILVER

                silver = roundDouble(getPlayerBalance(String.valueOf(player.getUniqueId()), "SILVER", databaseFile));
                message = GetMessage(messages_path, "BALANCE_BOTH").replace("%silver%", String.valueOf(silver));
            }

        }

        player.sendMessage(message);


        return true;
    }


}
