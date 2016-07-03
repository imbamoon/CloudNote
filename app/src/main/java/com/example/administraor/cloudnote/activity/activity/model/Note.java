package com.example.administraor.cloudnote.activity.activity.model;


import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * 这是主要的note类
 * <p/>
 * Created by Administraor on 2015/12/4.
 */

public class Note extends BmobObject {
    //以下为我想用数据库处理时的设置,先实现文本的存储，实现这个之后再实现图片的存储
    private String title;
    private String contents;
    private String date;
    private String objectid;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;

    }

    public Note() {
    }

    public Note(String title, String contents, String date, String objectid,String username) {
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.objectid = objectid;
        this.username = username;
    }

    public Note(String title, String contents, String date,String username) {
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.username = username;
    }

    public Note(String title, String date) {
    }

    public String getObjectid(){
        return objectid;
    }

    public void setObjectid(String objectid){
        this.objectid = objectid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }


}
