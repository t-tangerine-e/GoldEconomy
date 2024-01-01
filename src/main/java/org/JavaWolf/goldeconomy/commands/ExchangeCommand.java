package org.JavaWolf.goldeconomy.commands;

import org.JavaWolf.goldeconomy.database.Handler;
import org.JavaWolf.goldeconomy.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public ExchangeCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args) {


        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();

        // /exchange <amount> <from> <to>

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }
        String UUID = String.valueOf(((Player) sender).getUniqueId());


        if (args.length != 3){
            sender.sendMessage(Utils.GetMessage(messages_path, "INCORRECT_COMMAND"));
            return true;
        }

        double amount;


        try{
            amount = Double.parseDouble(args[0]);
        } catch (Exception e){
            sender.sendMessage(Utils.GetMessage(messages_path, "INVALID_AMOUNT"));
            return true;
        }


        String origin = args[1];
        String exchange_dest = args[2];

        double gold_value = plugin.getConfig().getInt("VALUE_OF_1_GOLD_IN_SILVER");

        if (origin.equalsIgnoreCase(exchange_dest.toUpperCase())){
            sender.sendMessage(Utils.GetMessage(messages_path, "OPERATION_NOT_PERMITTED"));
            return true;
        }





        if (origin.equalsIgnoreCase("GOLD")){
            // to silver
            double silver = amount * gold_value;

            double balance = Handler.getPlayerBalance(UUID, "GOLD", databaseFile);

            if ( balance < amount){
                sender.sendMessage(Utils.GetMessage(messages_path, "NOT_ENOUGH_MONEY"));
                return true;
            } else {

                Handler.removeFromBalance(UUID, amount, "GOLD", databaseFile);
                Handler.addCurrency(UUID, silver, "SILVER", databaseFile);
            }


            sender.sendMessage(Utils.GetMessage(messages_path, "EXCHANGE_SUCCES")
                    .replace("%oc%", "§6")
                    .replace("%origin_amount%", String.valueOf(amount))
                    .replace("%origin_coin%", origin)
                    .replace("%tc%", "§7")
                    .replace("%target_amount%", String.valueOf(silver))
                    .replace("%target_coin%", "SILVER")
            );
        } else if (origin.equalsIgnoreCase("SILVER")){
            // to gold
            double gold = amount / gold_value;

            if (gold < 0.01){
                sender.sendMessage(Utils.GetMessage(messages_path, "EXCHANGE_RATE_LOW"));
                return true;
            }


            DecimalFormat df = new DecimalFormat("#.##");
            gold = Double.parseDouble(df.format(gold));

            double balance = Handler.getPlayerBalance(UUID, "GOLD", databaseFile);

            if ( balance < amount){
                sender.sendMessage(Utils.GetMessage(messages_path, "NOT_ENOUGH_MONEY"));
                return true;
            } else {

                Handler.removeFromBalance(UUID, amount, "SILVER", databaseFile);
                Handler.addCurrency(UUID, gold, "GOLD", databaseFile);
            }

            sender.sendMessage(Utils.GetMessage(messages_path, "EXCHANGE_SUCCES")
                            .replace("%oc%", "§7")
                            .replace("%origin_amount%", String.valueOf(amount))
                            .replace("%origin_coin%", origin)
                            .replace("%tc%", "§6")
                            .replace("%target_amount%", String.valueOf(gold))
                            .replace("%target_coin%", "GOLD")
            );


        } else {
            sender.sendMessage(Utils.GetMessage(messages_path, "INVALID_COIN_TYPE"));

            return true;
        }




        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias,@NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("<amount>");
        } else if (args.length == 2) {
            completions.add("<source>");
            completions.add("GOLD");
            completions.add("SILVER");
        } else if (args.length == 3) {
            completions.add("<to>");
            completions.add("GOLD");
            completions.add("SILVER");
        }

        return completions;
    }
}