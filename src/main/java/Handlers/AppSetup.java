package Handlers;

import java.io.InputStream;
import java.util.Properties;

public class AppSetup {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppSetup.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Error loading configurations");
        }
    }

    public static String getK3sHost() { return props.getProperty("k3s.host"); }
    public static String getK3sToken() { return props.getProperty("k3s.token"); }
    public static String getPrometheusUrl() { return props.getProperty("prometheus.url"); }
}
