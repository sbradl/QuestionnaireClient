package de.hszigr.mobileapps.questionnaire.client.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.model.QuestionnaireFactory;
import de.hszigr.mobileapps.questionnaire.client.rest.RestClient;

public class QuestionnaireService {
    
    private final RestClient client = new RestClient();
    
    public Map<String, String> getQuestionnaireOverview(final String baseUrl) throws IOException, XPathExpressionException {
        String data = client.get(baseUrl + "/list.xml");
        
        Map<String, String> questionnaires = new HashMap<String, String>();
        
        final InputSource source = new InputSource(new StringReader(data));
        final XPath xpath = XPathFactory.newInstance().newXPath();
        
        NodeList questionnaireNodes = (NodeList) xpath.evaluate("//questionnaire", source, XPathConstants.NODESET);
        
        for(int i = 0; i < questionnaireNodes.getLength(); ++i) {
            Element questionnaireNode = (Element) questionnaireNodes.item(i);
            questionnaires.put(questionnaireNode.getAttribute("id"), questionnaireNode.getAttribute("title"));
        }
        
        return questionnaires;
    }

    public Questionnaire getQuestionnaire(final String baseUrl, final String id) throws IOException, XPathExpressionException {
        
        String data = client.get(baseUrl + "/get/" + id + ".xml");

        return QuestionnaireFactory.createQuestionnaireFromXml(data);
    }
    
}
