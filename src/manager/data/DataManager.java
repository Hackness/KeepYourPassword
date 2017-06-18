package manager.data;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import manager.Main;
import manager.ThreadPoolManager;
import manager.Util;
import manager.listener.Listeners;
import manager.listener.impl.OnDataInitialized;
import manager.listener.impl.OnDataLoadStart;
import manager.listener.impl.OnDataLoaded;
import oshi.SystemInfo;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Created by Hack
 * Date: 08.04.2017 22:12
 */
public class DataManager {
    private static final String CRYPT_ALGORITHM = "AES";
    private static final String DIR = System.getenv("appdata") + "\\PasswordManager";
    private static final String FILE = DIR + "\\data";
    private static final String LOG_DIR = DIR + "\\log";
    private static final String BACKUP_DIR = DIR + "\\backup";
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
        File dir = new File(DIR);
        SecretKeySpec key = generateKey();
        fileEncryptCipher = Cipher.getInstance(CRYPT_ALGORITHM);
        fileEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
        fileDecryptCipher = Cipher.getInstance(CRYPT_ALGORITHM);
        fileDecryptCipher.init(Cipher.DECRYPT_MODE, key);
        if (!dir.exists())
            try {
                Files.createDirectory(new File(DIR).toPath());
            } catch (IOException e) {
                Main.showError("Cannot create file directory! " +
                        "Check your permissions or try to start the program in administrator mode.");
                e.printStackTrace();
            }
    }

    public static void logging() {
        if (Main.isDebugMode())
            return;
        try {
            File logDir = new File(LOG_DIR);
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
                    new FileOutputStream(LOG_DIR + "\\" + Util.makeFileNameWithDate("stdout", "log")));
            System.setErr(st);
            System.setOut(st);
        } catch (Exception e) {
            Main.showError("Cannot redirect stdout! Oh, nevermind, this error still no one will see.");
            e.printStackTrace();
        }
    }

    /**
     * init data encrypt/decrypt with auth password key. Using only after auth!
     */
    public void init() {
        try {
            if (holder == null) {
                System.out.println("Holder not found. Creating.");
                holder = new DataHolder();
            }
            SecretKeySpec key = generateKey();
            dataEncryptCipher = Cipher.getInstance(CRYPT_ALGORITHM);
            dataEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
            dataDecryptCipher = Cipher.getInstance(CRYPT_ALGORITHM);
            dataDecryptCipher.init(Cipher.DECRYPT_MODE, key);
            Listeners.onAction(OnDataInitialized.class);
        } catch (Exception e) {
            Main.showError("Error while initialize data!");
            e.printStackTrace();
        }
    }

    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new CipherOutputStream(new FileOutputStream(FILE),
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
        if (!new File(FILE).exists())
            return;
        Listeners.onAction(OnDataLoadStart.class);
        try (ObjectInputStream in = new ObjectInputStream(new CipherInputStream(new FileInputStream(FILE),
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

    public ArrayList<DataEntry> getDecryptedData() {
        ArrayList<DataEntry> list = new ArrayList<>();
        holder.forEach(dataEntry -> list.add(decryptData(new DataEntry(dataEntry))));
        return list;
    }

    public void add(DataEntry data) {
        encryptData(data);
        holder.add(data);
        onChange();
    }

    public void remove(String location, String login) {
        holder.remove(holder.get(encryptData(location), encryptData(login)));
        onChange();
    }

    private void onChange() {
        ThreadPoolManager.getInstance().execute(this::save);
        Main.getMainController().updateCache();
    }

    public String decryptData(String data) {
        try {
            return new String(dataDecryptCipher.doFinal(Base64.decode(data.getBytes())));
        } catch (Exception e) {
            Main.showError("Cannot decrypt data! Incompatible hardware, wrong password?");
            e.printStackTrace();
            return null;
        }
    }

    private DataEntry encryptData(DataEntry data) {
        data.entrySet().forEach(entry -> entry.setValue(encryptData(entry.getValue())));
        return data;
    }

    private DataEntry decryptData(DataEntry data) {
        data.entrySet().forEach(entry -> entry.setValue(decryptData(entry.getValue())));
        return data;
    }

    public void editData(String location, String login, UnaryOperator<DataEntry> action) {
        encryptData(action.apply(decryptData(holder.get(encryptData(location), encryptData(login)))));
        onChange();
    }

    public String encryptData(String data) {
        try {
            return Base64.encode(dataEncryptCipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            Main.showError("Cannot encrypt data. How it is can be possible?");
            e.printStackTrace();
            return null;
        }
    }

    private SecretKeySpec generateKey() {
        String k = new SystemInfo().getHardware().getProcessor().getProcessorID() + Main.loginPassword;
        return new SecretKeySpec(k.substring(k.length() > 16 ? k.length() - 16 : 0, k.length()).getBytes(), "AES");
    }

    public DataEntry fullCopy(DataEntry source, DataEntry dest) {
        dest.clear();
        source.forEach(dest::put);
        return dest;
    }

    /**
     * Makes backup of existing data file in new thread.
     *
     * @param name - name that will be used in backups name as header. Can be empty.
     */
    private void makeBackup(String name) {
        ThreadPoolManager.getInstance().execute(() -> {
            File file = new File(FILE);
            if (!file.exists())
                return;
            try {
                File backupDir = new File(BACKUP_DIR);
                if (!backupDir.exists())
                    Files.createDirectory(backupDir.toPath());
                Files.copy(file.toPath(), new File(BACKUP_DIR + "\\" + Util.makeFileNameWithDate(name, "")).toPath());
            } catch (Exception e) {
                Main.showError("Cannot create backup. Looks like your deeds is bad.");
                e.printStackTrace();
            }
        });
    }
}
