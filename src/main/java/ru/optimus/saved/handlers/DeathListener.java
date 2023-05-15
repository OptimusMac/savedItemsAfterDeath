package ru.optimus.saved.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import ru.optimus.saved.Main;
import ru.optimus.utils.SerializationUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class DeathListener implements Listener {


    @EventHandler
    public void onSavePlayer(PlayerDeathEvent e) {
        ItemStack[] main = parse(e.getEntity().getInventory().getStorageContents());
        ItemStack[] armor = parse(e.getEntity().getInventory().getArmorContents());
        String serializedSave = getItemToKey(main);
        String serializedArmor = getItemToKey(armor);
        String serializedOffHand;
        if (e.getEntity().getInventory().getItemInOffHand() == null || e.getEntity().getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
            serializedOffHand = "null";
        } else {
            serializedOffHand = SerializationUtils.serializeClass(e.getEntity().getInventory().getItemInOffHand());
        }
        Main.getInstance().getSqliteManager().insertPlayerData(e.getEntity().getName(), serializedSave, serializedArmor, serializedOffHand);
        e.getDrops().removeIf(ex -> !Main.getInstance().getMaterialNoSave().contains(ex.getType().name()));
    }

    private ItemStack[] parse(ItemStack[] itemStacks){
        ItemStack[] items = new ItemStack[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            if(itemStacks[i] == null){
                items[i] = null;
                continue;
            }
            if(Main.getInstance().getMaterialNoSave().contains(itemStacks[i].getType().name())) continue;
            items[i] = itemStacks[i];
        }
        return items;
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        String deserializedSave = Main.getInstance().getSqliteManager().getPlayerSaveData(e.getPlayer().getName());
        String deserializedArmor = Main.getInstance().getSqliteManager().getPlayerArmorData(e.getPlayer().getName());
        String deserializedOffHand = Main.getInstance().getSqliteManager().getPlayerOffHandData(e.getPlayer().getName());
        if (deserializedSave != null && !deserializedSave.equals("")) {
            for (String s : deserializedSave.split(",")) {
                if (s.equals("null")) continue;
                ItemStack itemStack = SerializationUtils.deserializeClass(s);
                if (Main.getInstance().getMaterialNoSave().contains(itemStack.getType().name())) continue;
                e.getPlayer().getInventory().addItem(itemStack);
            }
        }
        if (deserializedArmor != null && !deserializedArmor.equals("")) {
            ItemStack[] armor = new ItemStack[4];
            for (int i = 0; i < deserializedArmor.split(",").length; i++) {
                String s = deserializedArmor.split(",")[i];
                if (s.equals("null")) continue;
                ItemStack itemStack = SerializationUtils.deserializeClass(s);
                if (Main.getInstance().getMaterialNoSave().contains(itemStack.getType().name())) continue;
                armor[i] = itemStack;
            }
            e.getPlayer().getInventory().setArmorContents(armor);
        }
        if (deserializedOffHand != null && !deserializedOffHand.equals("null")) {
            ItemStack itemStack = SerializationUtils.deserializeClass(deserializedOffHand);
            if (!Main.getInstance().getMaterialNoSave().contains(itemStack.getType().name()))
                e.getPlayer().getInventory().setItemInOffHand(itemStack);
        }
        Main.getInstance().getSqliteManager().deletePlayerData(e.getPlayer().getName());
    }


    private String getItemToKey(ItemStack[] itemStacks) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ItemStack stack : itemStacks) {
            stringBuilder.append(stack == null || stack.getType().equals(Material.AIR) ? "null" : SerializationUtils.serializeClass(stack)).append(",");
        }
        return stringBuilder.toString().trim();
    }


}
