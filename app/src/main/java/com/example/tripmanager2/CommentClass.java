package com.example.tripmanager2;

public class CommentClass {

    public String author;
    public String iconUrl;
    public String rating;
    public String relativeTime;
    public String text;

    public CommentClass(){};
    public CommentClass(String _author, String _iconUrl, String _rating, String _relativeTime, String _text){
        author = _author;
        iconUrl = _iconUrl;
        rating = _rating;
        relativeTime = _relativeTime;
        text = _text;

    }

}
