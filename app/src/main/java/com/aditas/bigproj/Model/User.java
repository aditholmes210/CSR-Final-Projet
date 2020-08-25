package com.aditas.bigproj.Model;

public class User {

    private String id;
    private String uname;
    private String fname;
    private String imgurl;
    private String bio;

    public User(String id, String uname, String fname, String imgurl, String bio){
        this.id = id;
        this.uname = uname;
        this.fname = fname;
        this.imgurl = imgurl;
        this.bio = bio;
    }

    public User(){

    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getUname(){
        return uname;
    }
    public void setUname(String uname){
        this.uname = uname;
    }

    public String getFname(){
        return fname;
    }
    public void setFname(String fname){
        this.fname = fname;
    }

    public String getImgurl(){
        return imgurl;
    }
    public void setImgurl(String imgurl){
        this.imgurl = imgurl;
    }

    public String getBio(){
        return bio;
    }
    public void setBio(String bio){
        this.bio = bio;
    }
}
