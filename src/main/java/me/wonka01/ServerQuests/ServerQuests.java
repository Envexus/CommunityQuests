package me.wonka01.ServerQuests;

import me.wonka01.ServerQuests.configuration.JsonQuestSave;
import me.wonka01.ServerQuests.events.*;
import me.wonka01.ServerQuests.events.questevents.*;
import me.wonka01.ServerQuests.gui.TypeGui;
import me.wonka01.ServerQuests.gui.StopGui;
import me.wonka01.ServerQuests.questcomponents.ActiveQuests;
import org.bukkit.Bukkit;
import me.wonka01.ServerQuests.commands.EventsCommands;
import me.wonka01.ServerQuests.configuration.QuestLibrary;
import me.wonka01.ServerQuests.gui.StartGui;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class ServerQuests extends JavaPlugin {

    public static Economy economy = null;
    public QuestLibrary questLibrary;
    private EventsCommands commandExecutor;
    private StartGui startGui;
    private StopGui stopGui;
    private ActiveQuests activeQuests;
    private JsonQuestSave jsonSave;

    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        commandExecutor = new EventsCommands();
        commandExecutor.setup();

        loadConfig();
        loadQuestLibraryFromConfig();
        loadSaveData();

        if(!setupEconomy())
        {
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Warning! No economy plugin found, a cash reward can not be added" +
                    " to a quest.");
        }

        loadStartEventGui();
        registerEvents();
        getServer().getPluginManager().registerEvents(startGui, this);
        StopGui stopGui = new StopGui();
        getServer().getPluginManager().registerEvents(stopGui, this);
    }

    @Override
    public void onDisable() {

        getLogger().info("onDisable is called!");
        jsonSave.saveQuestsInProgress();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void loadSaveData(){
        JsonQuestSave saveJson = new JsonQuestSave(getDataFolder(), activeQuests);
        jsonSave = saveJson;
        if(jsonSave.getOrCreateQuestFile()){
            jsonSave.readAndInitializeQuests();
        }
    }

    public void loadQuestLibraryFromConfig() {
        ConfigurationSection serverQuestSection = getConfig().getConfigurationSection("ServerQuests");
        questLibrary = new QuestLibrary();
        questLibrary.loadQuestConfiguration(serverQuestSection);
        ActiveQuests activeQuests = new ActiveQuests();
        this.activeQuests = activeQuests;
    }

    public void loadStartEventGui()
    {
        TypeGui typeGui = new TypeGui();
        typeGui.initializeItems();
        getServer().getPluginManager().registerEvents(typeGui, this);
        StartGui startGui = new StartGui(typeGui);
        startGui.initializeItems();
        this.startGui = startGui;
        stopGui = new StopGui();
    }

    public void reloadConfiguration(){
        reloadConfig();
        saveConfig();
        ConfigurationSection serverQuestSection = getConfig().getConfigurationSection("ServerQuests");
        questLibrary = new QuestLibrary();
        questLibrary.loadQuestConfiguration(serverQuestSection);
    }

    public QuestLibrary getQuestLibrary(){
        return questLibrary;
    }

    public StartGui getStartGui()
    {
        return startGui;
    }
    public StopGui getStopGui()
    {
        return stopGui;
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            Bukkit.getServer().getConsoleSender().sendMessage(economy.currencyNameSingular());
        }
        return (economy != null);
    }

    private void registerEvents()
    {
        getServer().getPluginManager().registerEvents(startGui, this);
        getServer().getPluginManager().registerEvents(stopGui, this);

        getServer().getPluginManager().registerEvents(new BreakEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new CatchFishEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new KillPlayerEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new MobKillEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new ProjectileKillEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new PlaceEvent(activeQuests), this);
        getServer().getPluginManager().registerEvents(new ShearEvent( activeQuests), this);
        getServer().getPluginManager().registerEvents(new TameEvent(activeQuests), this);
        //getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
    }
}