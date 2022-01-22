package systems.opalia.launcher.exception;

import java.util.Objects;
import org.osgi.framework.BundleException;


public class UncheckedBundleException
        extends RuntimeException {

    public UncheckedBundleException(String message, BundleException cause) {

        super(message, Objects.requireNonNull(cause));
    }

    public UncheckedBundleException(BundleException cause) {

        super(Objects.requireNonNull(cause));
    }

    @Override
    public synchronized BundleException getCause() {

        return (BundleException) super.getCause();
    }
}
