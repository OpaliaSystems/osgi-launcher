package systems.opalia.launcher.logging;

import org.osgi.service.log.FormatterLogger;
import systems.opalia.launcher.logging.formatter.PrintfFormatter;


public class FormatterLoggerImpl
        extends AbstractLogger
        implements FormatterLogger {

    public FormatterLoggerImpl(org.apache.logging.log4j.Logger underlying, PrintfFormatter formatter) {

        super(underlying, formatter);
    }
}
