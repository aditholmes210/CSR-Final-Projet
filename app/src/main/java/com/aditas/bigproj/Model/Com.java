package com.aditas.bigproj.Model;

public class Com {

    private String comment;
    private String publish;

    public Com(String comment, String publish){
        this.comment = comment;
        this.publish = publish;
    }

    public Com(){}

    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }

    public String getPublish(){
        return publish;
    }
    public void setPublish(String publish){
        this.publish = publish;
    }
}
