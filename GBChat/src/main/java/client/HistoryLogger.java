package client;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryLogger {

    private static List<String> log = new ArrayList<>();

    public static List<String> getLog() {
        return log;
    }

    public static void logHistory(String login, String nick, String logLine){
        String logFileName = String.format("Logs/history_%s.log", login);
        File logFile = new File(logFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter br = new BufferedWriter(new FileWriter(logFile, true))){
            br.write(String.format("%s: %s%n", nick, logLine));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readHistoryLogFile(File logFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))){
            log.clear();
            while (br.ready()){
                log.add(br.readLine());
            }
            Collections.sort(log, Comparator.reverseOrder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
