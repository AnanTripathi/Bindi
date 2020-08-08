package com.project.bindi;

public class User {
    String Uid;
    String email;
    String name;

    public User(String uid, String email, String name, String age, String gender, String description, String interestedin, String image, String audio, Integer likes) {
        Uid = uid;
        this.email = email;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.description = description;
        this.interestedin = interestedin;
        this.image = image;
        this.audio = audio;
        this.likes = likes;
    }

    public String getInterestedin() {
        return interestedin;
    }

    public void setInterestedin(String interestedin) {
        this.interestedin = interestedin;
    }

    public Integer getLikes() {
        return likes;
    }
    public void increaseLikes(){
        likes++;
    }
    public void doubleIncreaseLike(){
        likes++;
        likes++;
    }
    public void decreaseLikes(){
        likes--;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    String age;
    String gender;
    String description;
    String interestedin;
    String image;
    String audio;
    Integer likes;

//    public User(String uid, String email, String name, String age, String gender, String description, String image, String audio) {
//        Uid = uid;
//        this.email = email;
//        this.name = name;
//        this.age = age;
//        this.gender = gender;
//        this.description = description;
//        this.image = image;
//        this.audio = audio;
//    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public User() {

    }

    public boolean isProfileComplete(){
        if(audio==null||name==null||email==null||age==null||gender==null||description==null||name.equals("")||email.equals("")||age.equals("")||gender.equals("")||description.equals("")||image==null||image.equals("")||audio.equals("")){
            return false;
        }
        return true;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }


}
