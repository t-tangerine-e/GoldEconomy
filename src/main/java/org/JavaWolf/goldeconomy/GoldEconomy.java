package org.JavaWolf.goldeconomy;

import org.JavaWolf.goldeconomy.commands.BaltopCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcogiveCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcoremoveCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcoresetCommand;
import org.JavaWolf.goldeconomy.commands.admins.EcosetCommand;
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

        // register /pay command
        getCommand("pay").setExecutor(new PayCommand(this));

        // register /balance command
        getCommand("balance").setExecutor(new BalanceCommand(this));

        // register /baltop command
        getCommand("baltop").setExecutor(new BaltopCommand(this));

        if (getConfig().getBoolean("EXCHANGE_CURRENCYES")){

            // register /exchange command
            getCommand("exchange").setExecutor(new ExchangeCommand(this));

        }


        // admin part

        // register /eco-set command
        getCommand("eco-set").setExecutor(new EcosetCommand(this));

        // register /eco-reset command
        getCommand("eco-reset").setExecutor(new EcoresetCommand(this));

        // register /eco-give command
        getCommand("eco-give").setExecutor(new EcogiveCommand(this));

        // register /eco-remove command
        getCommand("eco-remove").setExecutor(new EcoremoveCommand(this));



    }


    @Override
    public void onLoad(){

        File dataFolder = getDataFolder();

        // create data folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
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
                Transactions.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }





        }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
