package uy.edu.um.doors;

import uy.edu.um.tad.list.MyLinkedListImpl;

public class Evento {
    private String tipo;
    private MyLinkedListImpl<String> instrucciones;

    public Evento(String tipo){
        this.tipo = tipo;
        this.instrucciones = new MyLinkedListImpl<>();
    }

    public String getType() {
        return tipo;
    }

    public MyLinkedListImpl<String> getInstructions() {
        return instrucciones;
    }
}
