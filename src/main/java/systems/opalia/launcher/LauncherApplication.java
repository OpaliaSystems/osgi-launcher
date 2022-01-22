package systems.opalia.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;


public final class LauncherApplication {

    public static void main(String[] args) {

        final var props = new Properties();

        props.put(Launcher.PROPERTY_AUTO_SHUTDOWN, "true");

        props.putAll(System.getProperties());

        for (final var arg : args)
            props.putAll(loadPropertiesFromFile(arg));

        final var launcher = new Launcher(props);

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
