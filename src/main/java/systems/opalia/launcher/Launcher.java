package systems.opalia.launcher;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.felix.main.AutoProcessor;
import org.apache.logging.log4j.LogManager;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import systems.opalia.launcher.exception.UncheckedBundleException;


public final class Launcher {

    // about Apache Felix: https://felix.apache.org/documentation/subprojects/apache-felix-framework.html

    public static final String PROPERTY_AUTO_SHUTDOWN = "launcher.auto-shutdown";
    public static final String PROPERTY_AUTO_DEPLOYMENT = "launcher.auto-deployment";
    public static final String PROPERTY_AUTO_DEPLOYMENT_DIRECTORY = "launcher.auto-deployment-directory";
    public static final String PROPERTY_CACHE_DIRECTORY = "launcher.cache-directory";
    public static final String PROPERTY_PID_FILE = "launcher.pid-file";
    public static final String PROPERTY_BOOT_DELEGATIONS = "launcher.boot-delegations";
    public static final String PROPERTY_EXTRA_EXPORT_PACKAGES = "launcher.extra-export-packages";
    public static final String PROPERTY_BUNDLE_ARTIFACTS = "launcher.bundle-artifacts";
    public static final String PROPERTY_REMOTE_REPOSITORIES = "launcher.remote-repositories";
    public static final String PROPERTY_LOCAL_REPOSITORY = "launcher.local-repository";

    private final Logger logger;
    private final Framework framework;
    private final ServiceHandler serviceHandler;
    private final ArtifactResolver artifactResolver;
    private final List<String> bundleArtifactNames;
    private final List<Bundle> bundles;

    public Launcher(Properties props) {

        initLogging(props);

        logger = LoggerFactory.getLogger(Launcher.class);
        framework = getFramework(props);

        bootFramework(props);

        serviceHandler = new ServiceHandler(framework.getBundleContext());
        artifactResolver = new ArtifactResolver(getRemoteRepositories(props), getLocalRepository(props));
        bundleArtifactNames = getBundleArtifacts(props);
        bundles = new ArrayList<>();

        if (getAutoShutdownFlag(props))
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        logger.info("The application is ready for setup");
    }

    public void setup() {

        logger.debug("Resolve bundle artifacts");

        final var artifacts =
                bundleArtifactNames.stream()
                        .map(DefaultArtifact::new)
                        .map(artifactResolver::resolve)
                        .collect(Collectors.toList());

        try {

            logger.debug("Start bundle installation");

            for (final var artifact : artifacts)
                bundles.add(framework.getBundleContext()
                        .installBundle("file://" + artifact.getFile().getAbsolutePath()));

            logger.debug("Trigger start for each bundle");

            for (final var bundle : bundles)
                bundle.start();

            logger.info("The application has been setup");

        } catch (BundleException e) {

            throw new UncheckedBundleException(e);
        }
    }

    public void shutdown() {

        try {

            logger.debug("Perform application shutdown");

            Collections.reverse(bundles);

            for (final var bundle : bundles)
                bundle.stop();

            serviceHandler.unregisterServices();
            serviceHandler.ungetServices();

            framework.stop();

            try {

                framework.waitForStop(0);
                logger.info("The OSGi framework has been shutdown gracefully");

            } catch (InterruptedException e) {

                logger.warn("The shutdown process was interrupted");
                Thread.currentThread().interrupt();
            }

        } catch (BundleException e) {

            logger.error("A framework or bundle specific error occurred during shutdown");
            throw new UncheckedBundleException(e);

        } catch (Exception e) {

            logger.error("An unexpected error occurred during shutdown");
            throw e;

        } finally {

            logger.debug("The application has been shutdown");

            if ("false".equals(System.getProperty("log4j.shutdownHookEnabled", "true")))
                LogManager.shutdown();
        }
    }

    public ServiceHandler.ServiceManager getServiceManager() {

        return serviceHandler.getServiceManager();
    }

    public ArtifactResolver getArtifactResolver() {

        return artifactResolver;
    }

    private Framework getFramework(Properties props) {

        final var serviceLoader = ServiceLoader.load(FrameworkFactory.class);
        final var frameworkFactory = serviceLoader.iterator().next();

        try {

            Files.createDirectories(getCacheDirectory(props));

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        }

        if (!props.contains(Constants.FRAMEWORK_STORAGE))
            props.put(Constants.FRAMEWORK_STORAGE, getCacheDirectory(props).toString());

        if (!props.contains(Constants.FRAMEWORK_STORAGE_CLEAN))
            props.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

        if (!props.contains(Constants.FRAMEWORK_BOOTDELEGATION))
            props.put(Constants.FRAMEWORK_BOOTDELEGATION, String.join(",", getBootDelegations(props)));

        if (!props.contains(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA))
            props.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                    String.join(",", transformExports(getExtraExportPackages(props))));

        return frameworkFactory.newFramework(toConfigMap(props));
    }

    private void bootFramework(Properties props) {

        try {

            createPidFile(getPidFile(props));

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        }

        if (!props.contains(AutoProcessor.AUTO_DEPLOY_DIR_PROPERTY))
            props.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERTY, getAutoDeploymentDirectory(props).toString());

        try {

            logger.debug("Boot OSGi framework");

            framework.init();

            if (getAutoDeploymentFlag(props)) {

                Files.createDirectories(getAutoDeploymentDirectory(props));
                AutoProcessor.process(toConfigMap(props), framework.getBundleContext());
            }

            framework.start();

            logger.info("The OSGi framework has been booted");

        } catch (BundleException e) {

            throw new UncheckedBundleException(e);

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        }
    }

    private Map<String, String> toConfigMap(Properties props) {

        final var config = new HashMap<String, String>();

        for (final var entry : props.entrySet())
            config.put((String) entry.getKey(), (String) entry.getValue());

        return config;
    }

    private void initLogging(Properties props) {

        if (getAutoShutdownFlag(props))
            System.setProperty("log4j.shutdownHookEnabled", "false");
    }

    private boolean getAutoShutdownFlag(Properties props) {

        final var value = props.getProperty(PROPERTY_AUTO_SHUTDOWN);

        if (value == null || value.isEmpty())
            return false; // default value

        return Boolean.parseBoolean(value);
    }

    private boolean getAutoDeploymentFlag(Properties props) {

        final var value = props.getProperty(PROPERTY_AUTO_DEPLOYMENT);

        if (value == null || value.isEmpty())
            return false; // default value

        return Boolean.parseBoolean(value);
    }

    private Path getAutoDeploymentDirectory(Properties props) {

        final var value = props.getProperty(PROPERTY_AUTO_DEPLOYMENT_DIRECTORY);

        if (value == null || value.isEmpty())
            return Paths.get("./tmp/auto-deploy").toAbsolutePath().normalize(); // default value

        return Paths.get(value).toAbsolutePath().normalize();
    }

    private Path getCacheDirectory(Properties props) {

        final var value = props.getProperty(PROPERTY_CACHE_DIRECTORY);

        if (value == null || value.isEmpty())
            return Paths.get("./tmp/felix-cache").toAbsolutePath().normalize(); // default value

        return Paths.get(value).toAbsolutePath().normalize();
    }

    private Path getPidFile(Properties props) {

        final var value = props.getProperty(PROPERTY_PID_FILE);

        if (value == null || value.isEmpty())
            return Paths.get("./tmp/application.pid").toAbsolutePath().normalize(); // default value

        return Paths.get(value).toAbsolutePath().normalize();
    }

    private List<String> getBootDelegations(Properties props) {

        final var value = props.getProperty(PROPERTY_BOOT_DELEGATIONS);

        if (value == null || value.isEmpty())
            return Arrays.asList("javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"); // default value

        return Arrays.stream(value.split(","))
                .filter(x -> !x.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getExtraExportPackages(Properties props) {

        final var value = props.getProperty(PROPERTY_EXTRA_EXPORT_PACKAGES);

        if (value == null || value.isEmpty())
            return Collections.emptyList(); // default value

        return Arrays.stream(value.split(","))
                .filter(x -> !x.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getBundleArtifacts(Properties props) {

        final var value = props.getProperty(PROPERTY_BUNDLE_ARTIFACTS);

        if (value == null || value.isEmpty())
            return Collections.emptyList(); // default value

        return Arrays.stream(value.split(","))
                .filter(x -> !x.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private LinkedHashMap<String, String> getRemoteRepositories(Properties props) {

        final var repositories = new LinkedHashMap<String, String>();
        final var value = props.getProperty(PROPERTY_REMOTE_REPOSITORIES);

        if (value == null || value.isEmpty()) {

            // default value
            repositories.put("Maven Central", "https://repo1.maven.org/maven2/");
            repositories.put("JCenter", "https://jcenter.bintray.com/");
            repositories.put("Sonatype OSS Releases", "https://oss.sonatype.org/content/repositories/releases/");

        } else {

            Arrays.stream(value.split(","))
                    .filter(x -> !x.isBlank())
                    .map(String::trim)
                    .forEach(entry -> {

                        final var pair = Arrays.stream(entry.split(":", 2))
                                .filter(x -> !x.isBlank())
                                .map(String::trim)
                                .collect(Collectors.toList());

                        if (pair.size() != 2)
                            throw new IllegalArgumentException("Cannot parse remote repositories");

                        repositories.put(pair.get(0), pair.get(1));
                    });
        }

        return repositories;
    }

    private String getLocalRepository(Properties props) {

        final var value = props.getProperty(PROPERTY_LOCAL_REPOSITORY);

        if (value == null || value.isEmpty())
            return System.getProperty("user.home") + "/.m2/repository/"; // default value

        return value;
    }

    private void createPidFile(Path file)
            throws IOException {

        Files.createDirectories(file.getParent());

        try (final var writer = Files.newBufferedWriter(file)) {

            writer.write(String.valueOf(ProcessHandle.current().pid()));
        }

        file.toFile().deleteOnExit();
    }

    private List<String> transformExports(List<String> extraExportPackages) {

        final var results = new ArrayList<String>();

        for (final var extraExportPackage : extraExportPackages) {

            final var packageEntries = listPackages(extraExportPackage);

            for (final var packageEntry : packageEntries) {

                final var bundleVersion = packageEntry.getValue().get("Bundle-Version");
                final var specVersion = packageEntry.getValue().get("Specification-Version");
                final var builder = new StringBuilder();

                builder.append(packageEntry.getKey());
                builder.append(";version=\"");

                if (bundleVersion != null)
                    extractSemver(builder, bundleVersion);
                else if (specVersion != null)
                    extractSemver(builder, specVersion);
                else
                    throw new IllegalArgumentException("Cannot get version of package " + packageEntry.getKey());

                builder.append("\"");

                final var result = builder.toString();

                if (!results.contains(result))
                    results.add(result);
            }
        }

        return results;
    }

    private void extractSemver(StringBuilder builder, String value) {

        final var p1 = "^(\\d+)\\.(\\d+)\\.(\\d+)([-.].+)?$"; // semver extraction
        final var p2 = "^0+(?!$)"; // remove leading zero
        final var matcher = Pattern.compile(p1).matcher(value);

        if (!matcher.matches())
            throw new IllegalArgumentException("Unexpected version format");

        final var major = matcher.group(1).replaceFirst(p2, "");
        final var minor = matcher.group(2).replaceFirst(p2, "");
        final var patch = matcher.group(3).replaceFirst(p2, "");

        builder.append(major);
        builder.append('.');
        builder.append(minor);
        builder.append('.');
        builder.append(patch);
    }

    private List<Map.Entry<String, Map<String, String>>> listPackages(String packageName) {

        final var transitive = packageName.endsWith(".*");
        final var name = transitive ? packageName.substring(0, packageName.length() - 2) : packageName;
        final var results = new ArrayList<Map.Entry<String, Map<String, String>>>();

        try {

            final var urls = this.getClass().getClassLoader().getResources(name.replace('.', '/'));

            while (urls.hasMoreElements()) {

                final var url = urls.nextElement();

                if (!url.getProtocol().equals("jar"))
                    throw new IllegalArgumentException("Cannot handle URI scheme " + url.getProtocol());

                final var connection = (JarURLConnection) url.openConnection();
                final var file = connection.getJarFile();
                final var entries = file.entries();
                final var attributes = new HashMap<String, String>();

                for (final var attribute : file.getManifest().getMainAttributes().entrySet())
                    attributes.put(attribute.getKey().toString(), attribute.getValue().toString());

                while (entries.hasMoreElements()) {

                    final var entry = entries.nextElement();

                    if (entry.getName().startsWith(connection.getEntryName() + "/") && entry.getName().endsWith("/")) {

                        final var key = entry.getName().substring(0, entry.getName().length() - 1).replace('/', '.');

                        results.add(Map.entry(key, attributes));
                    }
                }
            }

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        }

        if (!transitive)
            results.removeIf(x -> !x.getKey().equals(name));

        return results;
    }
}
