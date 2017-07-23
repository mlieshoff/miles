package org.mili.timeclock;

import org.apache.commons.io.FileUtils;
import org.mili.utils.DigitUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Michael Lieshoff, 26.04.17
 */
public class Report {

    public static void main(String[] args) throws ParseException, IOException {
        Map<Integer, File> filesPerDate = new TreeMap<>();
        for (File file : FileUtils.listFiles(Main.DIR, new String[]{"csv"}, false)) {
            String filename = file.getName();
            if (filename.startsWith("timeclock-")) {
                String possibleDate = filename.replace("timeclock-", "").replace(".csv", "");
                try {
                    filesPerDate.put(Integer.parseInt(possibleDate), file);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        List<List<String>> days = new ArrayList<>();
        for (Map.Entry<Integer, File> entry : filesPerDate.entrySet()) {
            int date = entry.getKey();
            File file = entry.getValue();
            String dateString = String.valueOf(date);
            if (date < 100000) {
                dateString += "0";
            }
            days.add(process(dateString, file));
        }
        int hours = 0;
        int minutes = 0;
        days.remove(days.size() - 1);
        for (List<String> day : days) {
            String time = day.get(1);
            String[] hoursAndMinutes = time.split(":");
            String sHours = hoursAndMinutes[0];
            String sMinutes = hoursAndMinutes[1];
            hours += Integer.valueOf(sHours);
            minutes += Integer.valueOf(sMinutes);
            System.out.println(day);
        }
        int totalMillis = ((hours * 60 + minutes) * 60 * 1000) / days.size();
        System.out.println("avg per day: " + new Date(totalMillis));
    }

    enum State {
        IN,
        BRK,
        OUT
    }

    public static List<String> process(String dateString, File file) throws IOException, ParseException {
        DateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yy");
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        DateFormat datetimeFormat = new SimpleDateFormat("yyMMddHHmmss");
        State state = null;
        List<String> lines = FileUtils.readLines(file);
        long firstIn = 0;
        long lastOut = 0;
        long inTime = 0;
        long breakTime = 0;
        long totalWorkTime = 0;
        long totalBreakTime = 0;
        for (String line : lines) {
            line = line.replace("\"", "").trim();
            String[] parts = line.split("[,]");
            String timeString = parts[0];
            long actionTime = datetimeFormat.parse(dateString + timeString).getTime();
            State action = State.valueOf(parts[1]);
            if (state == null || state == State.OUT) {
                if (action == State.IN) {
                    state = State.IN;
                    inTime = actionTime;
                } else if (action == State.OUT && state == null) {
                    totalWorkTime += actionTime;
                    inTime = 0;
                    state = State.OUT;
                }
            } else if (state == State.IN) {
                if (action == State.BRK || action == State.OUT) {
                    totalWorkTime += actionTime - inTime;
                    inTime = 0;
                    breakTime = actionTime;
                    state = action;
                }
            } else if (state == State.BRK) {
                if (action == State.IN) {
                    totalBreakTime += actionTime - breakTime;
                    inTime = actionTime;
                    breakTime = 0;
                    state = State.IN;
                } else if (action == State.OUT) {
                    totalBreakTime += actionTime - breakTime;
                    breakTime = 0;
                    inTime = 0;
                    state = State.OUT;
                }
            }
            if (firstIn == 0) {
                firstIn = inTime;
            }
            if (state == State.OUT) {
                lastOut = actionTime;
            }
        }
        if (state == State.IN) {
            totalWorkTime += System.currentTimeMillis() - inTime;
        }
        long dateMillis = dateFormat.parse(dateString).getTime();
        System.out.printf("%8s %5s (%5s) - IN: %s, OUT: %s\n",
                displayDateFormat.format(dateFormat.parse(dateString)),
                formatTime(totalWorkTime),
                formatTime(totalBreakTime),
                formatTime(firstIn - dateMillis),
                formatTime(lastOut - dateMillis)
        );
        List<String> results = new ArrayList<>();
        results.add(displayDateFormat.format(dateFormat.parse(dateString)));
        results.add(formatTime(totalWorkTime));
        results.add(formatTime(totalBreakTime));
        results.add(formatTime(firstIn - dateMillis));
        results.add(formatTime(lastOut - dateMillis));
        return results;
    }

    private static String formatTime(long totalWorkTime) {
        int hours = (int) (totalWorkTime / 1000 / 60 / 60);
        totalWorkTime -= hours * 1000 * 60 * 60;
        int minutes = (int) (totalWorkTime / 1000 / 60);
        return DigitUtils.formatTwoDigit(hours) + ":" + DigitUtils.formatTwoDigit(minutes);
    }

}
