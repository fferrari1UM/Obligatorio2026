package uy.edu.um.doors;

import uy.edu.um.tad.list.MyLinkedListImpl;

public class Event {
    private String type;
    private MyLinkedListImpl<String> instrtuctions;

    public Event(String type){
        this.type = type;
        this.instrtuctions = new MyLinkedListImpl<>();
    }

    public String getType() {
        return type;
    }

    public MyLinkedListImpl<String> getInstrtuctions() {
        return instrtuctions;
    }
}
