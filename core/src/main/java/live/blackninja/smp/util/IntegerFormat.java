package live.blackninja.smp.util;

import java.util.Locale;

public class IntegerFormat {
    public static String shortInteger(int duration) {
        String string = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (duration / 60 / 60 / 24 >= 1) {
            duration -= duration / 60 / 60 / 24 * 60 * 60 * 24;
        }
        if (duration / 60 / 60 >= 1) {
            hours = duration / 60 / 60;
            duration -= duration / 60 / 60 * 60 * 60;
        }
        if (duration / 60 >= 1) {
            minutes = duration / 60;
            duration -= duration / 60 * 60;
        }
        if (duration >= 1)
            seconds = duration;
        if (hours >= 1) {
            if (hours <= 9) {
                string = String.valueOf(string) + "0" + hours + ":";
            } else {
                string = String.valueOf(string) + hours + ":";
            }
        }
        if (minutes <= 9) {
            string = String.valueOf(string) + "0" + minutes + ":";
        } else {
            string = String.valueOf(string) + minutes + ":";
        }
        if (seconds <= 9) {
            string = String.valueOf(string) + "0" + seconds;
        } else {
            string = String.valueOf(string) + seconds;
        }
        return string;
    }

    public static String getNumberFormat(int integer) {
        String format = String.format(Locale.GERMAN, "%,d", integer);
        return format;
    }

    public static String getFormattedPlayTime(int time) {
        int minutes = time;
        int hours = 0;

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        return hours + " Stunden " + minutes + " Minuten";
    }

    public static String getShortFormattedPlayTime(int time) {
        int minutes = time;
        int hours = 0;

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        return hours + "h ";
    }

    public static String getExactFormattedPlayTime(int time) {
        int minutes = time;
        int hours = 0;
        int days = 0;

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        while (hours >= 24) {
            hours -= 24;
            days++;
        }

        return days + " Tage " + hours + " Stunden " + minutes + " Minuten";
    }

    public static String getFormattedTime(int totalSeconds) {
        int weeks = totalSeconds / (7 * 24 * 3600);
        int remainder = totalSeconds % (7 * 24 * 3600);

        int days = remainder / (24 * 3600);
        remainder %= 24 * 3600;

        int hours = remainder / 3600;
        remainder %= 3600;

        int minutes = remainder / 60;
        int seconds = remainder % 60;

        StringBuilder sb = new StringBuilder();
        if (weeks > 0)   sb.append(weeks).append("w ");
        if (days > 0)    sb.append(days).append("d ");
        if (hours > 0)   sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }


}
