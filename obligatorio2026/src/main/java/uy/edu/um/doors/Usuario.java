package uy.edu.um.doors;

public class User {
    private int uid;
    private String alias;
    private String type;

    public User(int uid, String alias, String type){
        this.uid = uid;
        this.alias = alias;
        this.type = type;
    }

    public int getUid() {
        return uid;
    }

    public String getAlias() {
        return alias;
    }

    public String getTipo() {
        return type;
    }
}
