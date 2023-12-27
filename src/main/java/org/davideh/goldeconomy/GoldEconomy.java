package org.davideh.goldeconomy;

import org.bukkit.plugin.java.JavaPlugin;
import org.davideh.goldeconomy.commands.BalanceCommand;
import org.davideh.goldeconomy.commands.BaltopCommand;
import org.davideh.goldeconomy.commands.PayCommand;
import org.davideh.goldeconomy.listeners.JoinListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.davideh.goldeconomy.Setup.*;
import static org.davideh.goldeconomy.database.Handler.TouchDatabase;
import static org.davideh.goldeconomy.database.Handler.createDB;
import static org.davideh.goldeconomy.utils.Utils.SaveLog;


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


        // ADMIN CONTROL:
        // admin stuff [...]


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
