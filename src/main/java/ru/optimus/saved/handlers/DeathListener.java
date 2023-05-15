package ru.optimus.saved.handlers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.optimus.saved.Main;
import ru.optimus.saved.SavePlayer;
import ru.optimus.utils.SerializationUtils;

import java.util.concurrent.CompletableFuture;

public class DeathListener implements Listener {


    @EventHandler
    public void onSavePlayer(PlayerDeathEvent e) {
        Entity entity = e.getEntity();
        NBTEntity nbtEntity = new NBTEntity(entity);
        NBTCompound nbtCompound = nbtEntity.getOrCreateCompound("saveItems");
        SavePlayer savePlayer = new SavePlayer(e.getEntity());
        String serializedSave = SerializationUtils.serializeClass(savePlayer);
        nbtCompound.setString("serializedItems", serializedSave);
        nbtEntity.mergeCompound(nbtCompound);
        e.getDrops().removeIf(ex -> !Main.getInstance().getMaterialNoSave().contains(ex.getType().name()));
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        CompletableFuture.runAsync(() -> {
            NBTEntity nbtEntity = new NBTEntity(e.getPlayer());
            NBTCompound nbtCompound = nbtEntity.getCompound("saveItems");
            if (nbtCompound == null) return;
            String deserializedSave = nbtCompound.getString("serializedItems");
            SavePlayer savePlayer = SerializationUtils.deserializeClass(deserializedSave);
            if (savePlayer == null) return;
            savePlayer.sendAll();
            nbtEntity.removeKey("saveItems");
        });
    }


}
