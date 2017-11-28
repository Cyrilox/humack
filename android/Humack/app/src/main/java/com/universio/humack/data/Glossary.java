package com.universio.humack.data;

/**
 * Created by Cyril Humbertclaude on 24/04/2015.
 */
public class Glossary extends Data{
    private String term, definition;

    public Glossary(int id, String term, String definition) {
        this.id = id;
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "Glossary{" +
                "id=" + id +
                ", term='" + term + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }
}
