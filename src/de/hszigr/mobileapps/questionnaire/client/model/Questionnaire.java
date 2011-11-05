package de.hszigr.mobileapps.questionnaire.client.model;

import java.util.ArrayList;
import java.util.Collection;


public class Questionnaire {

    private Collection<Question> questions = new ArrayList<Question>();
    
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public Collection<Question> getQuestions() {
        return questions;
    }

    
}
