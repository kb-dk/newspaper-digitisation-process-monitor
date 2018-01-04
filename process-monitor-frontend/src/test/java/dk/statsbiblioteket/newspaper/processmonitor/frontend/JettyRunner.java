package dk.statsbiblioteket.newspaper.processmonitor.frontend;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

/**
 * Runner for stating the application during development
 */
public class JettyRunner {

    public static void main(String[] args) throws Exception {
        Server server = startJettyServer();
        server.join();
        server.dumpStdErr();
    }

    public static Server startJettyServer() throws Exception {
        // Create Jetty Server
        Server server = new Server(8080);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/process-monitor-frontend");
        server.setHandler(webapp);

        Path webxmlPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("test-web.xml").toURI());
        webapp.setOverrideDescriptors(Arrays.asList(webxmlPath.toAbsolutePath().toString()));

        /*All this to just to find the war file with version number...*/
        final Path[] warPath = new Path[1];
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/process-monitor-frontend-*.war");
        Files.walkFileTree(webxmlPath.getParent().getParent(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (matcher.matches(file)){
                    warPath[0] = file;
                    return FileVisitResult.TERMINATE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
        webapp.setWar(warPath[0].toString());

        server.start();
        return server;
    }
}
