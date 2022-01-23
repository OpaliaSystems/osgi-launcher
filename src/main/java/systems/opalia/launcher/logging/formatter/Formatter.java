package systems.opalia.launcher.logging.formatter;

import org.slf4j.helpers.FormattingTuple;


public interface Formatter {

    FormattingTuple format(String messagePattern, Object argument);

    FormattingTuple format(String messagePattern, Object argument1, Object argument2);

    FormattingTuple arrayFormat(String messagePattern, Object[] arguments);
}
