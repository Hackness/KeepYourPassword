package ru.hackness.KeepYourPassword.data;

import java.util.LinkedHashMap;

/**
 * Created by Hack
 * Date: 09.04.2017 0:23
 *
 * Object of some data entry
 */
public class DataEntry extends LinkedHashMap<String, String> {
    public static final String LOCATION = "Location";
    public static final String LOGIN = "Login";
    public static final String PASSWORD = "Password";

    public DataEntry(String location, String login, String password) {
        put(LOCATION, location);
        put(LOGIN, login);
        put(PASSWORD, password);
    }

    public DataEntry() {

    }

    public DataEntry(DataEntry entry) {
        super(entry);
    }

    public boolean eqLocation(String location) {
        return getLocation().equalsIgnoreCase(location);
    }

    public boolean eqLogin(String login) {
        return getLogin().equalsIgnoreCase(login);
    }

    public String getLocation() {
        return get(LOCATION);
    }

    public String getLogin() {
        return get(LOGIN);
    }

    public String getPassword() {
        return get(PASSWORD);
    }
}
