package de.hszigr.mobileapps.questionnaire.client.model;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.hszigr.mobileapps.questionnaire.client.util.XmlUtils;

public final class QuestionnaireFactory {

    public static Questionnaire createQuestionnaireFromXml(final String xml) throws IOException {

        final Document source = XmlUtils.stringToXml(xml);

        final Element questionnaireElement = (Element) source.getDocumentElement();
        final String questionnaireId = questionnaireElement.getAttribute("id");

        final Questionnaire questionnaire = new Questionnaire(questionnaireId);

        final NodeList questionNodes = questionnaireElement.getElementsByTagName("question");

        processQuestionNodes(questionnaire, questionNodes);

        return questionnaire;
    }

    private static Question processQuestionNode(final NodeList questionNodes, int i) {
        final Element questionNode = (Element) questionNodes.item(i);

        final String typeName = questionNode.getAttribute("type").toUpperCase();
        final QuestionType type = Enum.valueOf(QuestionType.class, typeName);

        final String id = questionNode.getAttribute("id");
        final String text = questionNode.getElementsByTagName("text").item(0).getTextContent();

        final Question question = new Question(type, id, text);

        if (type == QuestionType.CHOICE || type == QuestionType.MULTICHOICE) {
            processChoices(questionNode, question);
        }

        return question;
    }
    
    private static void processQuestionNodes(final Questionnaire questionnaire,
            final NodeList questionNodes) {
        for (int i = 0; i < questionNodes.getLength(); ++i) {
            final Question question = processQuestionNode(questionNodes, i);

            questionnaire.addQuestion(question);
        }
    }
    
    private static void processChoices(final Element questionNode, final Question question) {
        final NodeList choiceNodes = questionNode.getElementsByTagName("choice");

        for (int j = 0; j < choiceNodes.getLength(); ++j) {
            processChoice(question, choiceNodes, j);
        }
    }

    private static void processChoice(final Question question, final NodeList choiceNodes, int j) {
        final Element choiceNode = (Element) choiceNodes.item(j);

        final String choiceId = choiceNode.getAttribute("id");
        final String value = choiceNode.getTextContent();
        final Choice choice = new Choice(choiceId, value);
        question.addChoice(choice);
    }
    
    private QuestionnaireFactory() {

    }
}
