package manager.properties;

import javafx.stage.Stage;
import manager.controller.WindowMainController;

import java.util.concurrent.TimeUnit;

/**
 * Created by Hack
 * Date: 20.08.2017 6:01
 */
public class Properties {
    public static String LOGIN_PASSWORD = "";
    public static Stage PRIMARY_STAGE_REF = null;
    public static WindowMainController MAIN_CONTROLLER_REF = null;
    public static boolean DEBUG_MODE = false;
    public static boolean DATA_LOADED = false;
    public static boolean AUTHORIZED = false;
    @OptionalConfig
    public static String CRYPT_ALGORITHM = "AES";
    @OptionalConfig
    public static String DIR = System.getenv("appdata") + "\\Hacknessdev\\KeepYourPassword";
    public static String DATA_FILE = DIR + "\\data";
    public static String LOG_DIR = DIR + "\\log";
    public static String BACKUP_DIR = DIR + "\\backup";
    public static String CONFIG_FILE = DIR + "\\options.ini";
    @OptionalConfig
    public static long LOG_LIFETIME = TimeUnit.DAYS.toMillis(1);
    @Configurable("Allows the program to process scheduled logout.")
    public static boolean SCHEDULED_LOGOUT = true;
    @Configurable(value = "Delay before the program will stop the session after window deactivate. In minutes.", min = 1)
    public static int LOGOUT_DELAY_MIN = 5;

}
