package com.example.administraor.cloudnote.activity.activity.Utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administraor.cloudnote.activity.activity.model.Note;
import com.example.administraor.cloudnote.activity.activity.model.NoteDB;

import java.util.ArrayList;


public class DbUtil {

    public ArrayList<Note> getNotes(NoteDB noteDB, String username) {
        ArrayList<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = noteDB.getReadableDatabase();
        Cursor cursor = db.query("Note", null, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String contents = cursor.getString(cursor.getColumnIndex("contents"));
                String strDate = cursor.getString(cursor.getColumnIndex("date"));
                String objectid = cursor.getString(cursor.getColumnIndex("objectid"));
                noteList.add(new Note(title, contents, strDate, objectid, username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;    //这里需不需要判断notelist是否为null的情况。
    }

    public void insertNote(NoteDB noteDB, String title, String contents, String strDate, String objectid, String username) {
        SQLiteDatabase db = noteDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("contents", contents);
        values.put("date", strDate);
        values.put("objectid", objectid);
        values.put("username", username);
        db.insert("Note", null, values);
        values.clear();
    }

    public void insertNote(NoteDB noteDB, String title, String contents, String strDate, String username) {
        SQLiteDatabase db = noteDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("contents", contents);
        values.put("date", strDate);
        values.put("username", username);
        db.insert("Note", null, values);
        values.clear();
    }

    public void deleteNote(NoteDB noteDB, String title, String strDate) {
        SQLiteDatabase db = noteDB.getWritableDatabase();
        db.delete("Note", "title=? and date=?", new String[]{title, strDate});
    }

    public void updateNote(NoteDB noteDB, String oldTitle, String oldStrDate, String title, String content, String date, String username) {
        SQLiteDatabase db = noteDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("contents", content);
        contentValues.put("date", date);
        db.update("Note", contentValues, "title=? AND date=? AND username=?", new String[]{oldTitle, oldStrDate, username});
    }

    public void updateObjectId(NoteDB noteDB, String title, String date, String objectId) {
        SQLiteDatabase db = noteDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("objectId", objectId);
        db.update("Note", contentValues, "title=? AND date=?", new String[]{title, date});
    }


    public void updateUsername(NoteDB noteDB, String username){
        SQLiteDatabase db = noteDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        db.update("Note", contentValues, "username = ?", new String[]{""});
    }
}

