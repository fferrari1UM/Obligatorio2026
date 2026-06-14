package uy.edu.um.doors;

public class Usuario {
    private int uid;
    private String alias;
    private String tipo;

    public Usuario(int uid, String alias, String tipo){
        this.uid = uid;
        this.alias = alias;
        this.tipo = tipo;
    }

    public int getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    public String getTipo() {
        return tipo;
    }
}
