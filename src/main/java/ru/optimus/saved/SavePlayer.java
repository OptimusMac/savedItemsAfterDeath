package ru.optimus.saved;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SavePlayer implements Serializable {

    private UUID uuid;
    private Player player;
    private Collection<ItemStack> mainItemStacks;
    private Collection<ItemStack> armorItemStacks;

    public SavePlayer(Player player) {
        this.uuid = UUID.randomUUID();
        this.player = player;
        this.mainItemStacks = Arrays.stream(player.getInventory().getContents())
            .filter(Objects::nonNull)
            .filter(e -> !Main.getInstance().getMaterialNoSave().contains(e.getType().name()))
            .collect(Collectors.toList());
        this.armorItemStacks = Arrays.stream(player.getInventory().getArmorContents())
            .filter(e -> !Main.getInstance().getMaterialNoSave().contains(e.getType().name()))
            .collect(Collectors.toList());
    }

    public void sendAll() {
        player.getInventory().setArmorContents(armorItemStacks.toArray(new ItemStack[0]));
        player.getInventory().setContents(mainItemStacks.toArray(new ItemStack[0]));
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Collection<ItemStack> getAllItems() {
        Collection<ItemStack> result = new ArrayList<>(mainItemStacks);
        result.addAll(armorItemStacks);
        return result;
    }
}
