package de.hszigr.mobileapps.questionnaire.client.model;

public class Choice {

    private final String id;
    private final String value;
    
    public Choice(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
    
}
