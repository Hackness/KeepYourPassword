package ru.hackness.KeepYourPassword;

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
    /**
     * Copy some String to system clipboard
     */
    public static void copyToClipboard(String value) {
        StringSelection ss = new StringSelection(value);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Create a filename with date and simple comment
     * @param header - simple comment
     * @param extension - extension of file
     * @return - String filename
     */
    public static String makeFileNameWithDate(String header, String extension) {
        Calendar calendar = Calendar.getInstance();
        return header + " " + calendar.get(Calendar.DATE) + "-" + calendar.get(Calendar.MONTH) + "-"
                + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + "-"
                + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND)
                + (!extension.isEmpty() ? "." + extension : "");
    }

    /**
     * Close window by some Node on it
     * @param node
     */
    public static void closeWindow(Node node) {
        ((Stage) node.getScene().getWindow()).close();
    }
}
