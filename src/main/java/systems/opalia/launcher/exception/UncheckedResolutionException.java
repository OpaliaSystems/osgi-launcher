package systems.opalia.launcher.exception;

import java.util.Objects;


public class UncheckedResolutionException
        extends RuntimeException {

    public UncheckedResolutionException(String message, Exception cause) {

        super(message, Objects.requireNonNull(cause));
    }

    public UncheckedResolutionException(Exception cause) {

        super(Objects.requireNonNull(cause));
    }

    @Override
    public synchronized Exception getCause() {

        return (Exception) super.getCause();
    }
}
