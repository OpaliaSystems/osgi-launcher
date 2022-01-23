package it;

import org.junit.jupiter.api.*;
import org.osgi.service.log.LoggerFactory;
import systems.opalia.launcher.Launcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggingTest {

    Launcher launcher;

    @BeforeAll
    public void setup()
            throws Exception {

        System.setProperty(Launcher.PROPERTY_FORCED_ROOT_LOG_LEVEL, "all");
        System.setProperty(Launcher.PROPERTY_PROVIDE_LOGGING_SERVICE, "true");

        launcher = new Launcher();

        launcher.setup();
    }

    @AfterAll
    public void shutdown()
            throws Exception {

        launcher.shutdown();
    }

    @Test
    @Order(1)
    void check_logging_service()
            throws Exception {

        final var factory = launcher.getServiceManager().getService(LoggerFactory.class);
        final var logger = factory.getLogger(this.getClass());

        assertThat(logger.getName(), equalTo(this.getClass().getName()));

        logger.trace("Hello world from trace logging!");
        logger.debug("Hello world from debug logging!");
        logger.info("Hello world from info logging!");
        logger.warn("Hello world from warn logging!");
        logger.error("Hello world from error logging!");
        logger.audit("Hello world from audit logging!");
    }
}
