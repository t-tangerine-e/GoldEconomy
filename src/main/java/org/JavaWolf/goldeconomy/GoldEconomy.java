package org.JavaWolf.goldeconomy;

import org.JavaWolf.goldeconomy.commands.BaltopCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcogiveCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcoremoveCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcoresetCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcosetCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.JavaWolf.goldeconomy.commands.BalanceCommand;
import org.JavaWolf.goldeconomy.commands.ExchangeCommand;
import org.JavaWolf.goldeconomy.commands.PayCommand;
import org.JavaWolf.goldeconomy.listeners.JoinListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.JavaWolf.goldeconomy.Setup.*;
import static org.JavaWolf.goldeconomy.database.Handler.TouchDatabase;
import static org.JavaWolf.goldeconomy.database.Handler.createDB;
import static org.JavaWolf.goldeconomy.utils.Utils.SaveLog;


public final class GoldEconomy extends JavaPlugin {

    @Override
    public void onEnable() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String currentDate = dateFormat.format(new Date());
        String log = "NEW LOGS: ------------------[ " +currentDate + "]------------------" ;

        File Transactions = new File(getDataFolder(), "transactions.log");
        SaveLog(Transactions, log);

        // register join listener
        // insert new field in the db if player is not in
        JoinListener joinListener = new JoinListener(this);
        getServer().getPluginManager().registerEvents(joinListener, this);






        // old structure: getCommand("baltop").setExecutor(new BaltopCommand(this));
        // may cause warning during compiling / pushing, those below are the same process without warns

        // register /pay command
        PluginCommand payCommand = getCommand("pay");
        if (payCommand != null) {
            payCommand.setExecutor(new PayCommand(this));
        } else {
            getLogger().warning("The 'pay' command was not found!");

        }

        // register /balance command


        PluginCommand balanceCommand = getCommand("balance");
        if (balanceCommand != null) {
            balanceCommand.setExecutor(new BalanceCommand(this));
        } else {
            getLogger().warning("The 'balance' command was not found!");

        }




        // register /baltop command

        PluginCommand baltopCommand = getCommand("baltop");
        if (baltopCommand != null) {
            baltopCommand.setExecutor(new BaltopCommand(this));
        } else {
            getLogger().warning("The 'baltop' command was not found!");

        }



        // if config has exchange true
        if (getConfig().getBoolean("EXCHANGE_CURRENCYES")){

            // register /exchange command

            PluginCommand exchange = getCommand("eco-set");
            if (exchange != null) {
                exchange.setExecutor(new ExchangeCommand(this));
            } else {
                getLogger().warning("The 'exchange' command was not found!");

            }


        }


        // admin part

        // register /eco-set command
        PluginCommand eco_set = getCommand("eco-set");
        if (eco_set != null) {
            eco_set.setExecutor(new EcosetCommand(this));
        } else {
            getLogger().warning("The 'eco-set' command was not found!");

        }


        // register /eco-reset command
        PluginCommand eco_reset = getCommand("eco-reset");
        if (eco_reset != null) {
            eco_reset.setExecutor(new EcoresetCommand(this));
        } else {
            getLogger().warning("The 'eco-reset' command was not found!");

        }


        // register /eco-give command
        PluginCommand eco_give = getCommand("eco-give");
        if (eco_give != null) {
            eco_give.setExecutor(new EcogiveCommand(this));
        } else {
            getLogger().warning("The 'eco-give' command was not found!");

        }

        // register /eco-remove command
        PluginCommand eco_remove = getCommand("eco-remove");
        if (eco_remove != null) {
            eco_remove.setExecutor(new EcoremoveCommand(this));
        } else {
            getLogger().warning("The 'eco-remove' command was not found!");

        }


    }


    @Override
    public void onLoad(){
        boolean trans_file;

        File dataFolder = getDataFolder();

        // create data folder
        if (!getDataFolder().exists()) {
            boolean status = getDataFolder().mkdirs();
            if (!status)
                getLogger().warning("errori during folder creation");
        }

        // create config.yml
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            CopyConfigYML(this, configFile);

        }
        // create messages.yml
        File messagesFile = new File(dataFolder, "messages.yml");
        if (!messagesFile.exists()) {
            CopyMessagesYML(this, messagesFile);

        }



        // create database.db
        String databaseFile = new File(dataFolder, "database.db").getPath();
        File file = new File(databaseFile);

        if (!file.exists()) {
            if (TouchDatabase(databaseFile)){
                createDB(databaseFile);
            }
        }



        File Transactions = new File(dataFolder, "transactions.log");
        if (!Transactions.exists()) {

            try {
                trans_file = Transactions.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!trans_file){
                getLogger().warning("error during transaction file creation.");
            }

        }





        }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
