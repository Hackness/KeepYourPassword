package manager;

import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Calendar;

/**
 * Created by Hack
 * Date: 08.04.2017 3:01
 */
public class Util {
    public static void copyToClipboard(String value) {
        StringSelection ss = new StringSelection(value);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    public static String makeFileNameWithDate(String header, String extension) {
        Calendar calendar = Calendar.getInstance();
        return header + " " + calendar.get(Calendar.DATE) + "-" + calendar.get(Calendar.MONTH) + "-"
                + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + "-"
                + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND)
                + (!extension.isEmpty() ? "." + extension : "");
    }

    public static void closeWindow(Node node) {
        ((Stage) node.getScene().getWindow()).close();
    }
}
