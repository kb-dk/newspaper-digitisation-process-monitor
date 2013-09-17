package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * Simple utility listener to load certain properties before Spring Starts up.
 * <p/>
 * Add this entry to your web.xml:
 * <pre><listener>
 * <listener-class>com.example.app.config.logging.ExternalConfigLoaderContextListener</listener-class>
 * </listener></pre>
 *
 * @author daniel
 */
public class ExternalConfigLoaderContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ExternalConfigLoaderContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String configLocation = sce.getServletContext().getInitParameter("CONFIGDIR");
        if (configLocation == null) {
            configLocation = System.getenv("CONFIGDIR");
        }
        if (!configLocation.startsWith("/")) {
            String webxmlrealPath = sce.getServletContext().getRealPath("/WEB-INF/web.xml");
            configLocation = new File(new File(webxmlrealPath).getParentFile().getParentFile(), configLocation).getAbsolutePath();
        }

        try {
            new LogBackConfigLoader(new File(configLocation, "logback.xml").getAbsolutePath());
        } catch (Exception e) {
            logger.error("Unable to read config file from " + configLocation, e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
