package ru.optimus.utils;

import ru.optimus.saved.SavePlayer;

import java.io.*;
import java.util.Base64;
import java.util.IllegalFormatException;

public class SerializationUtils {

    public static String serializeClass(SavePlayer savePlayer) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(savePlayer);
            byte[] bytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            System.out.println("Ошибка при сериализации объекта: " + e.getMessage());
            return null;
        }
    }

    public static SavePlayer deserializeClass(String serializedString) {
        try {
            byte[] bytes = Base64.getDecoder().decode(serializedString);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object obj = ois.readObject();
                if (obj instanceof SavePlayer) {
                    return (SavePlayer) obj;
                } else {
                    throw new ClassNotFoundException("Неверный тип объекта при десериализации");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации объекта: " + e.getMessage());
            return null;
        }
    }
}
