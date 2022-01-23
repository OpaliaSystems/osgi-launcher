package systems.opalia.launcher.logging;

import org.osgi.service.log.Logger;
import systems.opalia.launcher.logging.formatter.DefaultFormatter;


public class LoggerImpl
        extends AbstractLogger
        implements Logger {

    public LoggerImpl(org.apache.logging.log4j.Logger underlying, DefaultFormatter formatter) {

        super(underlying, formatter);
    }
}
