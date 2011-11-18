package de.hszigr.mobileapps.questionnaire.client.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public final class XmlUtils {

    public static Document stringToXml(final String data) {

        try {
            final Reader reader = new StringReader(data);
            final InputSource source = new InputSource(reader);

            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    public static String xmlToString(Document doc) {
        final StringWriter sw = new StringWriter();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Document createEmptyDocument() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        try {
            return factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Should not reach this point");
        }
    }

    private XmlUtils() {

    }
    
}
