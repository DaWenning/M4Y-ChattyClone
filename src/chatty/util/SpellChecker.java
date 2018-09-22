package chatty.util;

import chatty.Chatty;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class SpellChecker {

    static Map<String, String> spelling = new HashMap<>();

    public SpellChecker() {
        //Read from spellchecker in settings file
        try {
            String filepath = Chatty.getUserDataDirectory() + "spelling";
            Files.lines(new File(filepath).toPath()).forEach(s -> addToMap(s));
        }
        catch (IOException IO_exception) {
            JOptionPane.showMessageDialog(null, "Es ist ein Fehler beim Spell Check einlesen passiert", "Spell Check Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JOptionPane.showMessageDialog(null, "Spelling eingelesen", "Spelling eingelesen", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToMap(String string) {
        System.out.println(string);
        if (string.trim().contains("#") || string.trim().isEmpty()) return;
        String[] split = string.split(";");
        spelling.put(split[0], split[1]);
    }

    public static String rewrite(String string) {
        String returner = "";

        String[] split = string.split(" ");

        for (int i = 0; i < split.length; i++){
            String s = split[i];
            for (String pattern : spelling.keySet()) {
                if (s.matches(pattern)) {
                    split[i] = spelling.get(pattern);
                    break;
                }
            }
        }

        for(String s : split) returner += s + " ";

        return returner;
    }
}


