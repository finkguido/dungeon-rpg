package com.tallerwebi.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lee credenciales de Mercado Pago desde variables de entorno
 * o desde application-local.properties (archivo local, no versionado).
 */
public final class MercadoPagoSettings {

    private static final Properties LOCAL = loadLocalProperties();

    private MercadoPagoSettings() {
    }

    public static String accessToken() {
        return required("MP_ACCESS_TOKEN", "mp.access-token");
    }

    public static String appBaseUrl() {
        String value = firstNonBlank(
                System.getenv("APP_BASE_URL"),
                LOCAL.getProperty("app.base-url"),
                "http://localhost:8080"
        );
        return trimTrailingSlash(value);
    }

    private static String required(String envKey, String propertyKey) {
        String value = firstNonBlank(System.getenv(envKey), LOCAL.getProperty(propertyKey));
        if (value == null) {
            throw new IllegalStateException(
                    "Falta " + envKey + ". Definí la variable de entorno o creá " +
                            "src/main/resources/application-local.properties " +
                            "(copiá application-local.properties.example)."
            );
        }
        return value;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private static String trimTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static Properties loadLocalProperties() {
        Properties properties = new Properties();
        try (InputStream in = MercadoPagoSettings.class
                .getClassLoader()
                .getResourceAsStream("application-local.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException ignored) {
            // Sin archivo local: se usan solo variables de entorno.
        }
        return properties;
    }
}
