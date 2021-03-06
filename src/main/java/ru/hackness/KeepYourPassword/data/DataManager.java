package ru.hackness.KeepYourPassword.data;

import oshi.SystemInfo;
import ru.hackness.KeepYourPassword.Main;
import ru.hackness.KeepYourPassword.ThreadPoolManager;
import ru.hackness.KeepYourPassword.Util;
import ru.hackness.KeepYourPassword.listener.Listeners;
import ru.hackness.KeepYourPassword.listener.impl.OnDataInitialized;
import ru.hackness.KeepYourPassword.listener.impl.OnDataLoadStart;
import ru.hackness.KeepYourPassword.listener.impl.OnDataLoaded;
import ru.hackness.KeepYourPassword.properties.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * Created by Hack
 * Date: 08.04.2017 22:12
 */
public class DataManager {
    private static final long LOG_LIFETIME = TimeUnit.DAYS.toMillis(1);
    private static DataManager instance;
    private DataHolder holder;
    private final Cipher fileEncryptCipher;
    private final Cipher fileDecryptCipher;
    private Cipher dataEncryptCipher;
    private Cipher dataDecryptCipher;

    public static DataManager getInstance() {
        if (instance == null)
            try {
                instance = new DataManager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return instance;
    }

    private DataManager() throws Exception {
        File dir = new File(Properties.DIR);
        SecretKeySpec key = generateKey();
        fileEncryptCipher = Cipher.getInstance(Properties.CRYPT_ALGORITHM);
        fileEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
        fileDecryptCipher = Cipher.getInstance(Properties.CRYPT_ALGORITHM);
        fileDecryptCipher.init(Cipher.DECRYPT_MODE, key);
        if (!dir.exists())
            try {
                Files.createDirectory(new File(Properties.DIR).toPath());
            } catch (IOException e) {
                Main.showError("Cannot create file directory! " +
                        "Check your permissions or try to start the program in administrator mode.");
                e.printStackTrace();
            }
    }

    /**
     * This method will redirect standard output stream into log files
     */
    public static void logging() {
        if (Properties.DEBUG_MODE)
            return;
        try {
            File logDir = new File(Properties.LOG_DIR);
            if (logDir.exists())
                Files.walk(logDir.toPath()).forEach(path -> {
                    if (path.toFile().lastModified() + LOG_LIFETIME < System.currentTimeMillis())
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                });
            else
                Files.createDirectories(logDir.toPath());
            PrintStream st = new PrintStream(
                    new FileOutputStream(Properties.LOG_DIR + "\\" + Util.makeFileNameWithDate("stdout", "log")));
            System.setErr(st);
            System.setOut(st);
        } catch (Exception e) {
            Main.showError("Cannot redirect stdout! Oh, nevermind, this error still no one will see.");
            e.printStackTrace();
        }
    }

    /**
     * init data encryptor/decryptor with auth password key. Using only after auth!
     */
    public void init() {
        try {
            if (holder == null) {
                System.out.println("Holder not found. Creating.");
                holder = new DataHolder();
            }
            SecretKeySpec key = generateKey();
            dataEncryptCipher = Cipher.getInstance(Properties.CRYPT_ALGORITHM);
            dataEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
            dataDecryptCipher = Cipher.getInstance(Properties.CRYPT_ALGORITHM);
            dataDecryptCipher.init(Cipher.DECRYPT_MODE, key);
            Listeners.onAction(OnDataInitialized.class);
        } catch (Exception e) {
            Main.showError("Error while initialize data!");
            e.printStackTrace();
        }
    }

    /**
     * Primary save of encrypted data
     */
    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new CipherOutputStream(new FileOutputStream(Properties.DATA_FILE),
                fileEncryptCipher))) {
            out.writeObject(holder);
        } catch (IOException e) {
            Main.showError("Cannot save data!");
            e.printStackTrace();
        }
    }

    /**
     * load data to the holder, if data exists and is compatible.
     */
    public void load() {
        if (!new File(Properties.DATA_FILE).exists())
            return;
        Listeners.onAction(OnDataLoadStart.class);
        try (ObjectInputStream in = new ObjectInputStream(new CipherInputStream(new FileInputStream(Properties.DATA_FILE),
                fileDecryptCipher))) {
            holder = (DataHolder) in.readObject();
            Listeners.onAction(OnDataLoaded.class);
        } catch (IOException | ClassNotFoundException e) {
            Main.showError("Cannot load data! File are corrupted or incompatible with this hardware. " +
                    "Old data will be saved as backup then overwritten with a new file.");
            makeBackup("OnLoadFail");
            e.printStackTrace();
        }
    }

    public DataHolder getHolder() {
        return holder;
    }

    /**
     * decrypt all data entry's into new list
     * @return - decrypted list
     */
    @Deprecated
    public ArrayList<DataEntry> getDecryptedData() {
        ArrayList<DataEntry> list = new ArrayList<>();
        holder.forEach(dataEntry -> list.add(decryptData(new DataEntry(dataEntry))));
        return list;
    }

    /**
     * Add and encrypt some data entry
     * @param data - non-encrypted data entry
     */
    public void add(DataEntry data) {
        encryptData(data);
        holder.add(data);
        onChange();
    }

    /**
     * find and delete data by location&login pair
     */
    public void remove(String location, String login) {
        holder.remove(holder.get(encryptData(location), encryptData(login)));
        onChange();
    }

    /**
     * update cache on main window and start data save task into new thread.
     * Call this method after any structure changes.
     */
    private void onChange() {
        ThreadPoolManager.getInstance().execute(this::save);
        Properties.MAIN_CONTROLLER_REF.updateCache();
    }

    /**
     * Decrypt some string
     * @param data - encrypted string
     * @return - pure string
     */
    public String decryptData(String data) {
        try {
            return new String(dataDecryptCipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
        } catch (Exception e) {
            Main.showError("Cannot decrypt data! Incompatible hardware, wrong password?");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt some string
     * @param data - non-encrypted string
     * @return - as you expected: encrypted string
     */
    public String encryptData(String data) {
        try {
            return Base64.getEncoder().encodeToString(data.getBytes());
        } catch (Exception e) {
            Main.showError("Cannot encrypt data. How it is can be possible?");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt whole data in entry
     * @param data - non-encrypted data entry
     * @return - this entry
     */
    private DataEntry encryptData(DataEntry data) {
        data.entrySet().forEach(entry -> entry.setValue(encryptData(entry.getValue())));
        return data;
    }

    /**
     * Decrypt whole data in entry
     * @param data - encrypted data entry
     * @return - this entry
     */
    private DataEntry decryptData(DataEntry data) {
        data.entrySet().forEach(entry -> entry.setValue(decryptData(entry.getValue())));
        return data;
    }

    /**
     * Find some data by location&login pair, decrypt, apply an action on it, then encrypt back
     * @param action - desired action with entry as lambda
     */
    public void editData(String location, String login, UnaryOperator<DataEntry> action) {
        encryptData(action.apply(decryptData(holder.get(encryptData(location), encryptData(login)))));
        onChange();
    }

    /**
     * This method generate two different keys: before authentification and after.
     * Before auth we don't know users password and use only hwid as key.
     * After auth password will participate in key creating.
     * @return
     */
    private SecretKeySpec generateKey() {
        String k = new SystemInfo().getHardware().getProcessor().getProcessorID() + Properties.LOGIN_PASSWORD;
        return new SecretKeySpec(k.substring(k.length() > 16 ? k.length() - 16 : 0, k.length()).getBytes(), "AES");
    }

    /**
     * Create a full copy of two existing entry's
     * @param source - source of copied data
     * @param dest - destination of copied data
     * @return - destination entry
     */
    public DataEntry fullCopy(DataEntry source, DataEntry dest) {
        dest.clear();
        source.forEach(dest::put);
        return dest;
    }

    /**
     * Makes backup of existing data file in new thread.
     * @param name - name that will be used in backups name as header. Can be empty.
     */
    private void makeBackup(String name) {
        ThreadPoolManager.getInstance().execute(() -> {
            File file = new File(Properties.DATA_FILE);
            if (!file.exists())
                return;
            try {
                File backupDir = new File(Properties.BACKUP_DIR);
                if (!backupDir.exists())
                    Files.createDirectory(backupDir.toPath());
                Files.copy(file.toPath(),
                        new File(Properties.BACKUP_DIR + "\\" + Util.makeFileNameWithDate(name, "bak")).toPath());
            } catch (Exception e) {
                Main.showError("Cannot create backup. Looks like your deeds is bad.");
                e.printStackTrace();
            }
        });
    }
}
