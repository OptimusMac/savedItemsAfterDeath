package ru.optimus.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;

public class SerializationUtils {

    public static String serializeClass(ItemStack itemStack) {
        String encodedObject;
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = null;
            os = new BukkitObjectOutputStream(io);
            os.writeObject(itemStack);
            os.flush();
            byte[] serializedObject = io.toByteArray();
            encodedObject = Base64.getEncoder().encodeToString(serializedObject);
            return encodedObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static ItemStack deserializeClass(String serializedString) {
        byte[] serializedObjectOutput = Base64.getDecoder().decode(serializedString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedObjectOutput);
        BukkitObjectInputStream bukkitObjectInputStream = null;
        try {
            bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            ItemStack item = (ItemStack) bukkitObjectInputStream.readObject();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
