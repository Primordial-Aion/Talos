package engine.util;

public class Logger {
    private static boolean enabled = true;
    private static String prefix = "[Engine]";

    public static void setPrefix(String prefix) {
        Logger.prefix = prefix;
    }

    public static void disable() {
        enabled = false;
    }

    public static void info(String message) {
        if (enabled) {
            System.out.println(prefix + " [INFO] " + message);
        }
    }

    public static void warn(String message) {
        if (enabled) {
            System.out.println(prefix + " [WARN] " + message);
        }
    }

    public static void error(String message) {
        if (enabled) {
            System.err.println(prefix + " [ERROR] " + message);
        }
    }

    public static void debug(String message) {
        if (enabled) {
            System.out.println(prefix + " [DEBUG] " + message);
        }
    }
}