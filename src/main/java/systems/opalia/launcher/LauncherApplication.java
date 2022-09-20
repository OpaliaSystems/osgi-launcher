package systems.opalia.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;


public final class LauncherApplication {

    public static void main(String[] args) {

        boolean dryRun = false;

        System.setProperty(Launcher.PROPERTY_AUTO_SHUTDOWN, "true");

        for (final var arg : args) {

            if (arg.equals("--dry-run")) {

                dryRun = true;
                continue;
            }

            if (arg.startsWith("--config-file=")) {

                System.getProperties().putAll(loadPropertiesFromFile(arg.substring(14)));
                continue;
            }

            // process other arguments here

            throw new IllegalArgumentException("Cannot process unknown argument " + arg);
        }

        final var launcher = new Launcher();

        launcher.setup(dryRun);
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
