package systems.opalia.launcher.logging.formatter;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


class DefaultFormatterTest {

    @Test
    void format_messagePattern_and_argument()
            throws Exception {

        final var ft = new DefaultFormatter().format("[{}]", 42);

        assertThat(ft.getArgArray(), arrayWithSize(1));
        assertThat(ft.getThrowable(), nullValue());
        assertThat(ft.getMessage(), equalTo("[42]"));
    }

    @Test
    void format_messagePattern_and_two_arguments()
            throws Exception {

        final var ft = new DefaultFormatter().format("[{}, {}]", "string", 42);

        assertThat(ft.getArgArray(), arrayWithSize(2));
        assertThat(ft.getThrowable(), nullValue());
        assertThat(ft.getMessage(), equalTo("[string, 42]"));
    }

    @Test
    void arrayFormat_messagePattern_and_argument_array()
            throws Exception {

        final var array = new Object[]{73, "string", 42};
        final var ft = new DefaultFormatter().arrayFormat("[{}, {}, {}]", array);

        assertThat(ft.getArgArray(), arrayWithSize(3));
        assertThat(ft.getThrowable(), nullValue());
        assertThat(ft.getMessage(), equalTo("[73, string, 42]"));
    }

    @Test
    void arrayFormat_with_throwable_multiple_arguments()
            throws Exception {

        final var array = new Object[]{73, "string", 42, new Exception()};
        final var ft = new DefaultFormatter().arrayFormat("[{}, {}, {}]", array);

        assertThat(ft.getArgArray(), arrayWithSize(3));
        assertThat(ft.getThrowable(), notNullValue());
        assertThat(ft.getMessage(), equalTo("[73, string, 42]"));
    }

    @Test
    void format_with_throwable_no_arguments()
            throws Exception {

        final var ft = new DefaultFormatter().format("[string]", new Exception());

        assertThat(ft.getArgArray(), emptyArray());
        assertThat(ft.getThrowable(), notNullValue());
        assertThat(ft.getMessage(), equalTo("[string]"));
    }
}
