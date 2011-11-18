package de.hszigr.mobileapps.questionnaire.client.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.model.QuestionnaireFactory;
import de.hszigr.mobileapps.questionnaire.client.rest.RestClient;

public class QuestionnaireService {
    
    private final RestClient client = new RestClient();
    
    public Map<String, String> getQuestionnaireOverview(final String baseUrl) throws IOException {
        final String data = client.get(baseUrl + "/list.xml");
        
        final Map<String, String> questionnaires = new HashMap<String, String>();
        
        final Document source = XmlUtils.stringToXml(data);
        
        NodeList questionnaireNodes = source.getDocumentElement().getElementsByTagName("questionnaire");
        
        for(int i = 0; i < questionnaireNodes.getLength(); ++i) {
            Element questionnaireNode = (Element) questionnaireNodes.item(i);
            questionnaires.put(questionnaireNode.getAttribute("title"), questionnaireNode.getAttribute("id"));
        }
        
        return questionnaires;
    }

    public Questionnaire getQuestionnaire(final String baseUrl, final String id) throws IOException {
        String data = client.get(baseUrl + "/get/" + id + ".xml");

        return QuestionnaireFactory.createQuestionnaireFromXml(data);
    }
    
    public Document validate(final String baseUrl, final Document data) {
        return XmlUtils.stringToXml(client.put(baseUrl + "/verify.xml", XmlUtils.xmlToString(data)));
    }
    
    public Document send(final String baseUrl, final Document data) {
        return XmlUtils.stringToXml(client.put(baseUrl + "/put.xml", XmlUtils.xmlToString(data)));
    }
    
}
