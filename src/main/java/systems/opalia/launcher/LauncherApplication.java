package systems.opalia.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;


public final class LauncherApplication {

    public static void main(String[] args) {

        System.setProperty(Launcher.PROPERTY_AUTO_SHUTDOWN, "true");

        for (final var arg : args)
            System.getProperties().putAll(loadPropertiesFromFile(arg));

        final var launcher = new Launcher();

        launcher.setup();
    }

    private static Properties loadPropertiesFromFile(String file) {

        return loadPropertiesFromFile(new File(file));
    }

    private static Properties loadPropertiesFromFile(File file) {

        final var props = new Properties();

        try (final var fis = new FileInputStream(file)) {

            props.load(fis);

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        }

        return props;
    }
}
