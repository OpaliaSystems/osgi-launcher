package systems.opalia.launcher.logging.formatter;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;


public class DefaultFormatter
        implements Formatter {

    @Override
    public FormattingTuple format(String messagePattern, Object argument) {

        return MessageFormatter.format(messagePattern, argument);
    }

    @Override
    public FormattingTuple format(String messagePattern, Object argument1, Object argument2) {

        return MessageFormatter.format(messagePattern, argument1, argument2);
    }

    @Override
    public FormattingTuple arrayFormat(String messagePattern, Object[] arguments) {

        return MessageFormatter.arrayFormat(messagePattern, arguments);
    }
}
