package systems.opalia.launcher.logging.formatter;

import java.util.Objects;
import org.slf4j.helpers.FormattingTuple;


public class PrintfFormatter
        implements Formatter {

    public FormattingTuple format(String messagePattern, Object argument) {

        return arrayFormat(messagePattern, new Object[]{argument});
    }

    public FormattingTuple format(String messagePattern, Object argument1, Object argument2) {

        return arrayFormat(messagePattern, new Object[]{argument1, argument2});
    }

    public FormattingTuple arrayFormat(String messagePattern, Object[] arguments) {

        Objects.requireNonNull(arguments);

        final var throwable = getThrowableCandidate(arguments);
        final var array = throwable == null ? arguments : trimmedCopy(arguments);

        return new FormattingTuple(String.format(messagePattern, array), array, throwable);
    }

    private Throwable getThrowableCandidate(Object[] array) {

        if (array.length == 0)
            return null;

        final var last = array[array.length - 1];

        if (last instanceof Throwable)
            return (Throwable) last;

        return null;
    }

    private Object[] trimmedCopy(Object[] array) {

        if (array.length == 0)
            return new Object[0];

        final var trimmed = new Object[array.length - 1];

        System.arraycopy(array, 0, trimmed, 0, array.length - 1);

        return trimmed;
    }
}
