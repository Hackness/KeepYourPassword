package ru.hackness.KeepYourPassword.properties;

import ru.hackness.KeepYourPassword.Main;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Hack
 * Date: 20.08.2017 6:35
 *
 */
public class ConfigLoader {
    private final java.util.Properties properties = new java.util.Properties();
    private final File propFile = new File(Properties.CONFIG_FILE);
    private static final ConfigLoader instance = new ConfigLoader();
    private static final Map<Class<?>, Function<String, ?>> parseMap;

    static {
        parseMap = new HashMap<>(17);
        parseMap.put(Integer.class, Integer::parseInt);
        parseMap.put(int.class, Integer::parseInt);
        parseMap.put(Short.class, Short::parseShort);
        parseMap.put(short.class, Short::parseShort);
        parseMap.put(Float.class, Float::parseFloat);
        parseMap.put(float.class, Float::parseFloat);
        parseMap.put(Double.class, Double::parseDouble);
        parseMap.put(double.class, Double::parseDouble);
        parseMap.put(Long.class, Long::parseLong);
        parseMap.put(long.class, Long::parseLong);
        parseMap.put(Boolean.class, Boolean::parseBoolean);
        parseMap.put(boolean.class, Boolean::parseBoolean);
        parseMap.put(String.class, s -> s);
        parseMap.put(Character.class, s -> s.charAt(0));
        parseMap.put(char.class, s -> s.charAt(0));
        parseMap.put(Byte.class, Byte::parseByte);
        parseMap.put(byte.class, Byte::parseByte);
    }

    public static ConfigLoader getInstance() {
        return instance;
    }

    public void load() {
        try (InputStream input = new FileInputStream(Properties.CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Failed to load config file.");
        }
        Stream.of(Properties.class.getFields())
                .filter(f -> f.isAnnotationPresent(Configurable.class) || f.isAnnotationPresent(OptionalConfig.class))
                .forEach(f -> {
                    try {
                        String configVal = properties.getProperty(f.getName(), null);
                        if (configVal != null)
                            f.set(Properties.class, parseValue(configVal, f.getType()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassCastException e) {
                        Main.showError("Cannot parse config value: " + f.getName()
                                + ". Default value of the config will be applied.");
                    }
                });
        updateFile();
        System.out.println("Config loaded.");
    }

    @SuppressWarnings("unchecked")
    private <T> T parseValue(String value, Class<T> clazz) {
        Function<String, T> caster = (Function<String, T>) parseMap.get(clazz);
        if (caster == null)
            throw new ClassCastException(clazz + " cannot casted from string.");
        else
            return caster.apply(value);
    }

    private String valueToString(Object value) {
        if (parseMap.containsKey(value.getClass()))
            return String.valueOf(value);
        else
            return null; //TODO;
    }

    private void updateFile() {
        StringBuilder sb = new StringBuilder();
        if (!propFile.exists())
            try {
                Files.createFile(propFile.toPath());
                System.out.println("Config file not found. New file created.");
            } catch (IOException e) {
                Main.showError("Cannot create config file: " + propFile.getPath());
            }
        if (properties.isEmpty())
            sb.append("[General]\n");
        getFieldsToAdd().forEach(field -> {
            Object val = null;
            try {
                val = field.get(Properties.class);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sb.append("#").append(field.getAnnotation(Configurable.class).value()).append("\n")
                    .append(field.getName()).append(" = ").append(valueToString(val)).append("\n\n");
        });
        try {
            Files.write(propFile.toPath(), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            Main.showError("Cannot write configurations to: " + propFile);
        }
    }

    private List<Field> getFieldsToAdd() {
        List<Field> fieldsToAdd = new ArrayList<>();
        Stream.of(Properties.class.getFields())
                .filter(f -> f.isAnnotationPresent(Configurable.class))
                .forEach(field -> {
                    if (properties.getProperty(field.getName(), null) == null)
                        fieldsToAdd.add(field);
                });
        return fieldsToAdd;
    }
}
