package uy.edu.um.doors;

import uy.edu.um.tad.list.MyLinkedListImpl;

public class Proceso implements Comparable<Proceso> {
    private int pid;
    private String nombre;
    private Usuario propietario;
    private int prioridad;
    private String estado;
    private MyLinkedListImpl<Evento> eventos;

    public Proceso(int pid, String nombre, Usuario propietario){
        this.pid = pid;
        this.nombre = nombre;
        this.propietario = propietario;
        this.prioridad = 0;
        this.estado = "NEW";
        this.eventos = new MyLinkedListImpl<>();
    }

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public MyLinkedListImpl<Evento> getEventos(){
        return eventos;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Proceso otro){
        return Integer.compare(this.prioridad, otro.prioridad);
    }
}
