package org.JavaWolf.goldeconomy.commands;

import org.JavaWolf.goldeconomy.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.JavaWolf.goldeconomy.database.Handler.addCurrency;

public class RedeemCommand implements CommandExecutor {

    private final Plugin plugin;

    public RedeemCommand(Plugin plugin) {
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

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemMeta != null) {
            String Name = itemMeta.getDisplayName();

            if (Name.isEmpty()) {
                sender.sendMessage(Utils.GetMessage(messages_path, "NO_REDEEMABLE_ITEM"));
                return true;
            }

            if (itemMeta.hasLore()) {


                StringBuilder Builder = new StringBuilder();
                for (String loreLine : itemMeta.getLore()) {
                    Builder.append(loreLine).append("\n");
                }
                String Lore = Builder.toString();


                // issues between Chatcolor and color codes
                /*
                if (!Lore.equalsIgnoreCase(plugin.getConfig().getString("WITHDRAW_ITEM_LORE"))){
                    sender.sendMessage(Utils.GetMessage(messages_path, "NO_REDEEMABLE_ITEM"));
                    return true;
                }
                */


                player.getInventory().setItemInMainHand(null);

                // Name
                // Lore


                String name1 = plugin.getConfig().getString("WITHDRAWED_GOLD_INGOT_NAME_SINGOLAR");
                String name2 = plugin.getConfig().getString("WITHDRAWED_GOLD_INGOT_NAME_PLURAL");
                String name3 = plugin.getConfig().getString("WITHDRAWED_SILVER_INGOT_NAME_SINGOLAR");
                String name4 = plugin.getConfig().getString("WITHDRAWED_SILVER_INGOT_NAME_PLURAL");



                String[] NamesList = { name1, name2, name3, name4 };
                String value;

                for (String cointype : NamesList) {
                    if (Name.contains(cointype)) {
                        value = getValue(cointype, Name).substring(2);

                        String uuid = player.getUniqueId().toString();

                        String coin;
                        if (Name.toUpperCase().contains("GOLD")){
                            coin = "GOLD";
                        } else  {
                            coin = "SILVER";
                        }

                        boolean status;
                        try {
                            addCurrency( uuid , Double.parseDouble(value), coin, databaseFile);
                            status = true;
                        } catch (Exception e) {
                            System.out.print(e);
                            status = false;
                        }




                        // "You just redeemed %amount% of %cointype%"

                        if (status) {
                            String message = Utils.GetMessage(messages_path, "ITEM_REDEEMED");
                            sender.sendMessage(message.replace("%amount%", value)
                                                      .replace("%cointype%", coin) );
                        } else {
                            sender.sendMessage(Utils.GetMessage(messages_path, "REDEEM_ERROR"));

                            String error_message = ">> REDEEM: \n" + "CoinType: "+ cointype + "\namount: " + value + "\nlore: " + Lore;



                            plugin.getLogger().info(error_message);
                        }
                        return true;


                    }
                }

            } else {
                sender.sendMessage(Utils.GetMessage(messages_path, "NO_REDEEMABLE_ITEM"));
                return true;
            }
        }

        return true;
    }

    private static String getValue(String constantValue, String fullString) {
        int index = fullString.indexOf(constantValue);

        if (index != -1) {
            return fullString.substring(0, index);
        }
        return fullString;
    }





}
