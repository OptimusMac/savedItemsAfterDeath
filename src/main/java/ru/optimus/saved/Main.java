package ru.optimus.saved;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.optimus.saved.handlers.DeathListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Main extends JavaPlugin {

    private static Main instance;
    private List<String> materialNoSave;
    public ExecutorService executor;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        load();
        executor = Executors.newSingleThreadExecutor();
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);

    }


    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public List<String> getMaterialNoSave() {
        return materialNoSave;
    }


    public static Main getInstance() {
        return instance;
    }


    private void load() {
        materialNoSave = getInstance().getConfig().getStringList("itemsNoSave");
    }
}
