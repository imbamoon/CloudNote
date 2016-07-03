package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by zyw on 2016/6/7.
 */
public class GateApplication extends Application {

    private String username = "";
    //private ArrayList<String> paths;

//    public void setPath(String path,int position) {
//        paths = new ArrayList<>();
//        paths.add(position,path);
//    }
//
//    public String getPath(int position) {
//        String path;
//        if (paths!=null){
//            path = paths.get(position);
//            return path;
//        }
//        else {
//            return "false";
//        }
//    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
