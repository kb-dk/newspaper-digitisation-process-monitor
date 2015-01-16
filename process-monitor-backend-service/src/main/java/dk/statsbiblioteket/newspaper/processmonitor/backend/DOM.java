package dk.statsbiblioteket.newspaper.processmonitor.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class DOM {

    private static final Logger log = LoggerFactory.getLogger(DOM.class);

    /**
     * Parses an XML document from a String to a DOM.
     *
     * @param xmlString      a String containing an XML document.
     * @param namespaceAware if {@code true} the parsed DOM will reflect any
     *                       XML namespaces declared in the document
     *
     * @return The document in a DOM or {@code null} on errors.
     */
    public static Document stringToDOM(String xmlString, boolean namespaceAware) {
        try {
            InputSource in = new InputSource();
            in.setCharacterStream(new StringReader(xmlString));

            DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
            dbFact.setNamespaceAware(namespaceAware);


            final DocumentBuilder documentBuilder = dbFact.newDocumentBuilder();
            documentBuilder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    log.warn("Caught exception trying to parse xml",exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    log.warn("Caught exception trying to parse xml", exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    log.warn("Caught exception trying to parse xml", exception);
                }
            });
            return documentBuilder.parse(in);
        } catch (IOException e) {
            log.warn("I/O error when parsing XML :" + e.getMessage() + "\n" + xmlString, e);
        } catch (SAXException e) {
            log.warn("Parse error when parsing XML :" + e.getMessage() + "\n" + xmlString, e);
        } catch (ParserConfigurationException e) {
            log.warn("Parser configuration error when parsing XML :" + e.getMessage() + "\n" + xmlString, e);
        }
        return null;
    }
}
