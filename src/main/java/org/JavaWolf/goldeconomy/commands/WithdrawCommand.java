package org.JavaWolf.goldeconomy.commands;

import org.JavaWolf.goldeconomy.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.JavaWolf.goldeconomy.database.Handler.getPlayerBalance;
import static org.JavaWolf.goldeconomy.database.Handler.removeFromBalance;

public class WithdrawCommand implements CommandExecutor , TabCompleter {

    private final Plugin plugin;

    public WithdrawCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {

        String messages_path = new File(plugin.getDataFolder(), "messages.yml").getPath();
        String databaseFile = new File(plugin.getDataFolder(), "database.db").getPath();


        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.GetMessage(messages_path, "CONSOLE_UNAVAIBLE"));
            return true;
        }


        if (args.length != 2) {
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

        if (amount < 1){
            sender.sendMessage(Utils.GetMessage(messages_path, "WITHDRAW_LOW"));
            return true;
        }

        String coin = args[1];


        String lore = plugin.getConfig().getString("WITHDRAW_ITEM_LORE");

        String name;
        Player player = (Player) sender;

        if (coin.equalsIgnoreCase("GOLD")){
            if (amount > 1){
                name = plugin.getConfig().getString("WITHDRAWED_GOLD_INGOT_NAME_PLURAL");
            } else {
                name = plugin.getConfig().getString("WITHDRAWED_GOLD_INGOT_NAME_SINGOLAR");
            }

            if( playerChecks( player, coin, databaseFile, amount) ) {

                removeFromBalance(player.getUniqueId().toString(),  amount, coin, databaseFile);

                giveItemPlayer(player, String.valueOf(amount), coin, name, lore, plugin);
            }







        } else if (coin.equalsIgnoreCase("SILVER")) {

            if (amount > 1){
                name = plugin.getConfig().getString("WITHDRAWED_SILVER_INGOT_NAME_PLURAL");
            } else {
                name = plugin.getConfig().getString("WITHDRAWED_SILVER_INGOT_NAME_SINGOLAR");
            }
            if( playerChecks( player, coin, databaseFile, amount) ) {

                removeFromBalance(player.getUniqueId().toString(),  amount, coin, databaseFile);

                giveItemPlayer(player, String.valueOf(amount), coin, name, lore, plugin);
            }

        } else {
            sender.sendMessage(Utils.GetMessage(messages_path, "INVALID_COIN_TYPE"));
            return true;
        }


        return true;

    }

    private static void giveItemPlayer(Player player, String amount, String item, String name, String lore, Plugin plugin) {
        ItemStack BAR = null;
        String color = null;
        if (item.equalsIgnoreCase("GOLD")){
            BAR = new ItemStack(Material.GOLD_INGOT);
            color = plugin.getConfig().getString("GOLD_INGOT_NAME_COLOR");
        } else if (item.equalsIgnoreCase("SILVER")) {
            BAR = new ItemStack(Material.IRON_INGOT);
            color = plugin.getConfig().getString("SILVER_INGOT_NAME_COLOR");
        }

        ItemMeta meta = BAR.getItemMeta();


        if (meta != null) {

            item = ChatColor.RESET + color + amount + " " + name;

            meta.setDisplayName(item);
            meta.setLore(Collections.singletonList(lore));

            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            BAR.setItemMeta(meta);
            player.getInventory().addItem(BAR);
        }
    }

    private static boolean playerChecks(Player player, String coinType, String databaseFile, double amount){

        String uuid = player.getUniqueId().toString();

        double balance = getPlayerBalance( uuid,  coinType, databaseFile);

        return balance > amount;
    }



    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("<amount>");
        } else if (args.length == 2) {
            completions.add("GOLD");
            completions.add("SILVER");
        }

        return completions;
    }
}
