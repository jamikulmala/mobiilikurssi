package com.example.muistutussovellus;
/*
Luokka kuvaa yhtä sovelluksen muistutus oliota, joita tallennetaan tietokantaan ja näytetään sovelluksessa.
Sillä on kentät id, otsikko, sisältö, tieto käyttäjästä, jotta sen oikeat muistiinpanot näytetään
oikealle käyttäjälle, aika, sijainti ja tieto onko se asetettu aktiiviseksi. Lisäksi näiden get ja set
metodit ja toString.
 */
public class Note {
    private String id;
    private String title;
    private String content;
    private String userID;
    private String time;
    private String location;
    private boolean active;

    public String getId() { return id; }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUserID() { return userID; }

    public String getTime() { return time; }

    public String getLocation() { return location; }

    public boolean isActive() { return active; }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) { this.content = content; }

    public void setUserID(String userID) { this.userID = userID; }

    public void setTime(String time) { this.time = time; }

    public void setLocation(String location) { this.location = location; }

    public void changeActive(boolean status) { this.active = status; }
}
