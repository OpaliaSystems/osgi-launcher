package systems.opalia.launcher;

import java.util.*;
import java.util.stream.Collectors;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


public final class ServiceHandler {

    private final BundleContext bundleContext;
    private final List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();
    private final List<ServiceReference<?>> serviceReferences = new ArrayList<>();

    public ServiceHandler(BundleContext bundleContext) {

        this.bundleContext = bundleContext;
    }

    public void unregisterServices() {

        serviceRegistrations.forEach(ServiceRegistration::unregister);
        serviceRegistrations.clear();
    }

    public void ungetServices() {

        serviceReferences.forEach(bundleContext::ungetService);
        serviceReferences.clear();
    }

    public ServiceManager getServiceManager() {

        return new ServiceManager();
    }

    public class ServiceManager {

        public <T> void registerService(Class<T> clazz, T service) {

            registerService(clazz, service, null);
        }

        public <T> void registerService(Class<T> clazz, T service, Dictionary<String, ?> properties) {

            serviceRegistrations.add(bundleContext.registerService(clazz, service, properties));
        }

        public <T> T getService(Class<T> clazz) {

            return Optional.ofNullable(bundleContext.getServiceReference(clazz))
                    .flatMap(x -> {

                        serviceReferences.add(x);

                        return Optional.ofNullable(bundleContext.getService(x));
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find service " + clazz.getName()));
        }

        public <T> List<T> getServices(Class<T> clazz, String filter)
                throws InvalidSyntaxException {

            return bundleContext.getServiceReferences(clazz, filter).stream()
                    .map(x -> {

                        serviceReferences.add(x);

                        return bundleContext.getService(x);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
}
