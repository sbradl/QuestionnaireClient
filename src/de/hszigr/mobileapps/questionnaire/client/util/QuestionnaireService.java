package de.hszigr.mobileapps.questionnaire.client.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.model.QuestionnaireFactory;
import de.hszigr.mobileapps.questionnaire.client.rest.RestClient;



public class QuestionnaireService {
    
    private final RestClient client = new RestClient();

    public Questionnaire getQuestionnaire(final String baseUrl, final String id) throws IOException, XPathExpressionException {
        
        String data = client.get(baseUrl + "/get/" + id + ".xml");

        return QuestionnaireFactory.createQuestionnaireFromXml(data);
    }
    
}
