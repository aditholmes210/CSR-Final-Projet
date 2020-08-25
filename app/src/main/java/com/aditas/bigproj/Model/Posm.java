package com.aditas.bigproj.Model;

public class Posm {
    private String postid;
    private String postimg;
    private String desc;
    private String publish;

    public Posm(String postid, String postimg, String desc, String publish){
        this.postid = postid;
        this.postimg = postimg;
        this.desc = desc;
        this.publish = publish;
    }

    public Posm(){

    }

    public String getPostid(){
        return postid;
    }
    public void setPostid(String postid){
        this.postid = postid;
    }

    public String getPostimg(){
        return postimg;
    }
    public void setPostimg(String postimg){
        this.postimg = postimg;
    }

    public String getDesc(){
        return desc;
    }
    public void setDesc(String desc){
        this.desc = desc;
    }

    public String getPublish(){
        return publish;
    }
    public void setPublish(String publish){
        this.publish = publish;
    }
}
