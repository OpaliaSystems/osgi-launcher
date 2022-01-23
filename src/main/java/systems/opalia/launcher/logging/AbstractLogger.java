package systems.opalia.launcher.logging;

import org.apache.logging.log4j.Level;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerConsumer;
import org.slf4j.helpers.FormattingTuple;
import systems.opalia.launcher.logging.formatter.Formatter;


public class AbstractLogger
        implements Logger {

    private final org.apache.logging.log4j.Logger underlying;
    private final Formatter formatter;

    protected AbstractLogger(org.apache.logging.log4j.Logger underlying, Formatter formatter) {

        this.underlying = underlying;
        this.formatter = formatter;
    }

    @Override
    public String getName() {

        return underlying.getName();
    }

    @Override
    public boolean isTraceEnabled() {

        return underlying.isTraceEnabled();
    }

    @Override
    public void trace(String message) {

        if (isTraceEnabled()) {

            underlying.trace(message);
        }
    }

    @Override
    public void trace(String messagePattern, Object argument) {

        if (isTraceEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument);
            underlying.trace(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void trace(String messagePattern, Object argument1, Object argument2) {

        if (isTraceEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
            underlying.trace(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void trace(String messagePattern, Object... arguments) {

        if (isTraceEnabled()) {

            FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
            underlying.trace(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public <E extends Exception> void trace(LoggerConsumer<E> consumer)
            throws E {

        if (isTraceEnabled()) {

            consumer.accept(this);
        }
    }

    @Override
    public boolean isDebugEnabled() {

        return underlying.isDebugEnabled();
    }

    @Override
    public void debug(String message) {

        if (isDebugEnabled()) {

            underlying.debug(message);
        }
    }

    @Override
    public void debug(String messagePattern, Object argument) {

        if (isDebugEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument);
            underlying.debug(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void debug(String messagePattern, Object argument1, Object argument2) {

        if (isDebugEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
            underlying.debug(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void debug(String messagePattern, Object... arguments) {

        if (isDebugEnabled()) {

            FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
            underlying.debug(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public <E extends Exception> void debug(LoggerConsumer<E> consumer)
            throws E {

        if (isDebugEnabled()) {

            consumer.accept(this);
        }
    }

    @Override
    public boolean isInfoEnabled() {

        return underlying.isInfoEnabled();
    }

    @Override
    public void info(String message) {

        if (isInfoEnabled()) {

            underlying.info(message);
        }
    }

    @Override
    public void info(String messagePattern, Object argument) {

        if (isInfoEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument);
            underlying.info(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void info(String messagePattern, Object argument1, Object argument2) {

        if (isInfoEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
            underlying.info(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void info(String messagePattern, Object... arguments) {

        if (isInfoEnabled()) {

            FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
            underlying.info(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public <E extends Exception> void info(LoggerConsumer<E> consumer)
            throws E {

        if (isInfoEnabled()) {

            consumer.accept(this);
        }
    }

    @Override
    public boolean isWarnEnabled() {

        return underlying.isWarnEnabled();
    }

    @Override
    public void warn(String message) {

        if (isWarnEnabled()) {

            underlying.warn(message);
        }
    }

    @Override
    public void warn(String messagePattern, Object argument) {

        if (isWarnEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument);
            underlying.warn(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void warn(String messagePattern, Object argument1, Object argument2) {

        if (isWarnEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
            underlying.warn(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void warn(String messagePattern, Object... arguments) {

        if (isWarnEnabled()) {

            FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
            underlying.warn(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public <E extends Exception> void warn(LoggerConsumer<E> consumer)
            throws E {

        if (isWarnEnabled()) {

            consumer.accept(this);
        }
    }

    @Override
    public boolean isErrorEnabled() {

        return underlying.isErrorEnabled();
    }

    @Override
    public void error(String message) {

        if (isErrorEnabled()) {

            underlying.error(message);
        }
    }

    @Override
    public void error(String messagePattern, Object argument) {

        if (isErrorEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument);
            underlying.error(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void error(String messagePattern, Object argument1, Object argument2) {

        if (isErrorEnabled()) {

            FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
            underlying.error(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public void error(String messagePattern, Object... arguments) {

        if (isErrorEnabled()) {

            FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
            underlying.error(ft.getMessage(), ft.getThrowable());
        }
    }

    @Override
    public <E extends Exception> void error(LoggerConsumer<E> consumer)
            throws E {

        if (isErrorEnabled()) {

            consumer.accept(this);
        }
    }

    @Override
    public void audit(String message) {

        underlying.log(Level.ALL, message);
    }

    @Override
    public void audit(String messagePattern, Object argument) {

        FormattingTuple ft = formatter.format(messagePattern, argument);
        underlying.log(Level.ALL, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void audit(String messagePattern, Object argument1, Object argument2) {

        FormattingTuple ft = formatter.format(messagePattern, argument1, argument2);
        underlying.log(Level.ALL, ft.getMessage(), ft.getThrowable());
    }

    @Override
    public void audit(String messagePattern, Object... arguments) {

        FormattingTuple ft = formatter.arrayFormat(messagePattern, arguments);
        underlying.log(Level.ALL, ft.getMessage(), ft.getThrowable());
    }
}

