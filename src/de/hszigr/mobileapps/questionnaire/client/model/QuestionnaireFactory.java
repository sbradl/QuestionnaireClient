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

        final InputSource source = new InputSource(new StringReader(xml));
        final XPath xpath = XPathFactory.newInstance().newXPath();
        
        final Questionnaire questionnaire = new Questionnaire();
        
        NodeList questionNodes = (NodeList) xpath.evaluate("//question", source,
                XPathConstants.NODESET);

        for (int i = 0; i < questionNodes.getLength(); ++i) {
            Element questionNode = (Element) questionNodes.item(i);
            
            String typeName = questionNode.getAttribute("type").toUpperCase();
            QuestionType type = Enum.valueOf(QuestionType.class, typeName);
            
            String id = questionNode.getAttribute("id");
            String text = questionNode.getElementsByTagName("text").item(0).getTextContent();
            
            Question question = new Question(type, id, text);
            
            if(type == QuestionType.CHOICE || type == QuestionType.MULTICHOICE) {
                NodeList choiceNodes = questionNode.getElementsByTagName("choice");
                
                for(int j = 0; j < choiceNodes.getLength(); ++j) {
                    Element choiceNode = (Element) choiceNodes.item(j);
                    
                    final String choiceId = choiceNode.getAttribute("id");
                    final String value = choiceNode.getTextContent();
                    final Choice choice = new Choice(choiceId, value);
                    question.addChoice(choice);
                }
            }
            
            questionnaire.addQuestion(question);
        }

        return questionnaire;
    }

    private QuestionnaireFactory() {

    }
}
