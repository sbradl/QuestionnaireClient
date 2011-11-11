package de.hszigr.mobileapps.questionnaire.client.model;

import java.util.ArrayList;
import java.util.Collection;


public class Questionnaire {

    private final String id;
    private final Collection<Question> questions = new ArrayList<Question>();
    
    public Questionnaire(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public Collection<Question> getQuestions() {
        return questions;
    }

    
}
