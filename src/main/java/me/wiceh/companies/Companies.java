package me.wiceh.companies;

import me.wiceh.companies.commands.AziendaCommand;
import me.wiceh.companies.commands.CassaCommand;
import me.wiceh.companies.database.Database;
import me.wiceh.companies.listeners.CashRegisterListeners;
import me.wiceh.companies.utils.CashRegisterUtils;
import me.wiceh.companies.utils.CompanyUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Companies extends JavaPlugin {

    private File dataFile;
    private Database database;
    private CompanyUtils companyUtils;
    private CashRegisterUtils cashRegisterUtils;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        loadDataFile();

        this.database = new Database(this);
        this.companyUtils = new CompanyUtils(this);
        this.cashRegisterUtils = new CashRegisterUtils(this);

        getCommand("azienda").setExecutor(new AziendaCommand(this));
        getCommand("cassa").setExecutor(new CassaCommand(this));

        getServer().getPluginManager().registerEvents(new CashRegisterListeners(this), this);
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    private void loadDataFile() {
        dataFile = new File(getDataFolder().getAbsoluteFile() + "/data.db");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getDataFile() {
        return dataFile;
    }

    public Database getDatabase() {
        return database;
    }

    public CompanyUtils getCompanyUtils() {
        return companyUtils;
    }

    public CashRegisterUtils getCashRegisterUtils() {
        return cashRegisterUtils;
    }
}
