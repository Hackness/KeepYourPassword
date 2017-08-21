package manager.data;

import manager.Main;
import manager.listener.Listeners;
import manager.listener.impl.OnPwdHashCheck;
import manager.properties.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Hack
 * Date: 09.04.2017 0:08
 *
 * Object of holder that will be serialize
 */
public class DataHolder extends ArrayList<DataEntry> {
    private final String pwdHash;

    public DataHolder() {
        pwdHash = PBKDF2Hash.createHash(Properties.LOGIN_PASSWORD);
    }

    public DataEntry get(String location, String login) {
        return stream().filter(data -> data.eqLocation(location) && data.eqLogin(login)).findFirst().get();
    }

    public List<DataEntry> get(String location) {
        return stream().filter(data -> data.eqLocation(location)).collect(Collectors.toList());
    }

    public boolean hashCheck(String pwd) {
        boolean result = PBKDF2Hash.validatePassword(pwd, pwdHash);
        Listeners.onAction(OnPwdHashCheck.class, check -> check.onAction(result));
        return result;
    }
}
