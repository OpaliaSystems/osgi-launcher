package systems.opalia.launcher.logging;

import org.apache.logging.log4j.LogManager;
import org.osgi.framework.Bundle;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import systems.opalia.launcher.logging.formatter.DefaultFormatter;
import systems.opalia.launcher.logging.formatter.PrintfFormatter;


public class LoggerFactoryImpl
        implements LoggerFactory {

    @Override
    public Logger getLogger(String name) {

        return createLogger(name, Logger.class);
    }

    @Override
    public Logger getLogger(Class<?> clazz) {

        return createLogger(clazz, Logger.class);
    }

    @Override
    public <L extends Logger> L getLogger(String name, Class<L> loggerClazz) {

        return createLogger(name, loggerClazz);
    }

    @Override
    public <L extends Logger> L getLogger(Class<?> clazz, Class<L> loggerClazz) {

        return createLogger(clazz, loggerClazz);
    }

    @Override
    public <L extends Logger> L getLogger(Bundle bundle, String name, Class<L> loggerClazz) {

        return createLogger(bundle.getSymbolicName() + ":" + name, loggerClazz);
    }

    private <L extends Logger> L createLogger(Object name, Class<L> loggerClazz) {

        if (name instanceof String && loggerClazz == org.osgi.service.log.FormatterLogger.class)
            return (L) new FormatterLoggerImpl(LogManager.getLogger((String) name), new PrintfFormatter());

        if (name instanceof String && loggerClazz == org.osgi.service.log.Logger.class)
            return (L) new LoggerImpl(LogManager.getLogger((String) name), new DefaultFormatter());

        if (name instanceof Class && loggerClazz == org.osgi.service.log.FormatterLogger.class)
            return (L) new FormatterLoggerImpl(LogManager.getLogger((Class) name), new PrintfFormatter());

        if (name instanceof Class && loggerClazz == org.osgi.service.log.Logger.class)
            return (L) new LoggerImpl(LogManager.getLogger((Class) name), new DefaultFormatter());

        throw new IllegalArgumentException("The specified logger type is not supported");
    }
}
