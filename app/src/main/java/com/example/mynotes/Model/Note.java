package com.example.mynotes.Model;

public class Note {
    private String title;
    private String content;
    private String link;
    private String bkgColor;
    public Note(){};
    public Note(String title, String content, String link, String bkgColor){
        this.title= title;
        this.content= content;
        this.link= link;
        this.bkgColor= bkgColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getBkgColor() {
        return bkgColor;
    }

    public void setBkgColor(String bkgColor) {
        this.bkgColor = bkgColor;
    }
}
