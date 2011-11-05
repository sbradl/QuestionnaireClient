package de.hszigr.mobileapps.questionnaire.client.model;

import java.util.ArrayList;
import java.util.Collection;

public class Question {

    private final QuestionType type;
    private final String id;
    private final String text;
    
    private final Collection<Choice> choices = new ArrayList<Choice>();
    
    public Question(final QuestionType type, final String id, final String text) {
        this.type = type;
        this.id = id;
        this.text = text;
    }
    
    public void addChoice(final Choice choice) {
        choices.add(choice);
    }

    public QuestionType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
    
    public Collection<Choice> getChoices() {
        return choices;
    }
}
