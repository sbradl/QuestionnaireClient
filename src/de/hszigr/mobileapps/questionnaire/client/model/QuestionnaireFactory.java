package de.hszigr.mobileapps.questionnaire.client.model;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class QuestionnaireFactory {

    public static Questionnaire createQuestionnaireFromXml(final String xml)
            throws IOException, XPathExpressionException {

        InputSource source = new InputSource(new StringReader(xml));

        Questionnaire questionnaire = new Questionnaire();

        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList questionNodes = (NodeList) xpath.evaluate("//question", source,
                XPathConstants.NODESET);

        for (int i = 0; i < questionNodes.getLength(); ++i) {
            Element questionNode = (Element) questionNodes.item(i);
            String type = questionNode.getAttribute("type").toUpperCase();
            
            questionnaire.addQuestion(new Question(Enum.valueOf(QuestionType.class, type)));
        }

        return questionnaire;
    }

    private QuestionnaireFactory() {

    }
}
