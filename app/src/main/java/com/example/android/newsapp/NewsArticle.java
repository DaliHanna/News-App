package com.example.android.newsapp;

public class NewsArticle {
    private String mTitle;
    private String mAuthor;
    private String mImage;
    private String mDate;
    private String mUrl;
    private String mSection;

    public NewsArticle(String mTitle, String mAuthor, String mImage, String mDate, String mUrl, String mSection) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mImage = mImage;
        this.mDate = mDate;
        this.mUrl = mUrl;
        this.mSection = mSection;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getSeciton() {
        return mSection;
    }
}
