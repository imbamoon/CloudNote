package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.Utils.DbUtil;
import com.example.administraor.cloudnote.activity.activity.Utils.NetworkState;
import com.example.administraor.cloudnote.activity.activity.model.FragmentFactory;
import com.example.administraor.cloudnote.activity.activity.model.Note;
import com.example.administraor.cloudnote.activity.activity.model.NoteDB;
import com.example.administraor.cloudnote.activity.activity.model.RecyclerViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    final String TAG = "MainActivity";

    private Toolbar mainBar;
    private long exitTime = 0;

    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private boolean isFragmentNote;
    private NoteDB noteDB;
    private ArrayList<Note> notesLocal;
    private Note noteLocal;
    private String objectId;
    private String localDate;
    private Note note;
    private DbUtil dbUtil;
    private String username;
    private String noNameTitle, noNameContent, noNameDate, noNameObjectid;
    public GateApplication app;
    private boolean synchronizeSuccess = true;
    private int NOT_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //初始化Bmob
        Bmob.initialize(this, "cb6ae7d27eda798f0a3b95a8c40c2d7e");

        //判断是否需要登陆注册
        app = (GateApplication) getApplication();
        username = app.getUsername();

        //同步数据
        refresh();

        mainBar = (Toolbar) findViewById(R.id.toolbar);
        //获取FragmentManager
        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
                if (checkedId == R.id.tab_note) {

                    mainBar.setTitle("笔记");
                    isFragmentNote = true;
                    invalidateOptionsMenu();
                } else {
                    mainBar.setTitle("设置");
                    isFragmentNote = false;
                    invalidateOptionsMenu();
                }
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        });

        //设置toolbar
        mainBar.setTitle("笔记");
        setSupportActionBar(mainBar);
        mainBar.setOnMenuItemClickListener(this);
        mainBar.setNavigationIcon(R.drawable.user);
        mainBar.setNavigationOnClickListener(new View.OnClickListener() {//导航头像点击事件
            @Override
            public void onClick(View v) {
                username = app.getUsername();
                if (!username.equals("")){
                    Intent intent = new Intent(MainActivity.this,UserActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        //重新载入fragment为什么不管用？
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceByIndex(R.id.tab_note);
        invalidateOptionsMenu();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
        isFragmentNote = true;

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        app = (GateApplication) getApplication();
        username = app.getUsername();
        if (!new NetworkState().isNetworkConnected(this)) {
            Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
        } else if (username.equals("")){

        } else {
            refreshUsername();//同步所有note并将username刷新
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceByIndex(R.id.tab_note);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
        isFragmentNote = true;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_search:
                //这里加上搜索代码
                break;

            case R.id.note_notify:
                //这里加上提醒代码
                break;

            case R.id.refresh:
                username = app.getUsername();
                if (!new NetworkState().isNetworkConnected(this)) { //无网络
                    Toast.makeText(this, "当前无网络", Toast.LENGTH_SHORT).show();
                } else if (username.equals("")) {//有网络未登陆
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {//有网bmob刷新
                    refresh();
                }
                //刷新当前activity
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentFactory.getInstanceByIndex(R.id.tab_note);
                mainBar.setTitle("笔记");
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
                isFragmentNote = true;
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemRefresh = menu.findItem(R.id.refresh);
        MenuItem itemSearch = menu.findItem(R.id.note_search);
        MenuItem itemNotify = menu.findItem(R.id.note_notify);
        MenuItem itemMore = menu.findItem(R.id.more);
        if (isFragmentNote) {
            itemRefresh.setVisible(true);
            itemSearch.setVisible(true);
            itemNotify.setVisible(true);
            itemMore.setVisible(true);
        } else {
            itemRefresh.setVisible(false);
            itemSearch.setVisible(false);
            itemNotify.setVisible(false);
            itemMore.setVisible(false);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 同步有username的note
     */
    public void refresh() {
        //如果username="",无同步操作
        if(username.equals("")){
            return;
        }
        synchronizeSuccess = true;
        //查询本地的笔记
        noteDB = NoteDB.getInstance(this);
        dbUtil = new DbUtil();
        notesLocal = dbUtil.getNotes(noteDB, username);
        //查询本地有objectid的note对应的bmob的note
        BmobQuery<Note> objectIdQuery = new BmobQuery<>();
        //将客户端有objectid但被删除的重新下载
        BmobQuery<Note> downloadQuery = new BmobQuery<>();
        //组装条件查询
        List<BmobQuery<Note>> andQuerys = new ArrayList<>();
        if (notesLocal.size() != 0) {
            for (int i = 0; i < notesLocal.size(); i++) {

                noteLocal = notesLocal.get(i);
                //获取本地数据库的objectid和localDate
                objectId = noteLocal.getObjectid();
                localDate = noteLocal.getDate();

                if (objectId == null) {//无objectid（上传）
                    final String localTitle = noteLocal.getTitle();
                    final String localContent = noteLocal.getContents();
                    final String localDate = noteLocal.getDate();
                    note = new Note(localTitle, localContent, localDate, username);
                    //上传至bmob
                    note.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //objectId存入本地数据库
                            String objectid = note.getObjectId();
                            dbUtil.updateObjectId(noteDB, localTitle, localDate, objectid);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            synchronizeSuccess = false;
                        }
                    });

                } else {//有objectid更新
                    //查找bmob中的note并和本地的note比较时间（更新）
                    objectIdQuery.getObject(this, objectId, new GetListener<Note>() {
                        @Override
                        public void onSuccess(Note note) {
                            if (!note.getDate().equals(localDate)) {
                                note.setTitle(noteLocal.getTitle());
                                note.setContents(noteLocal.getContents());
                                note.setDate(localDate);
                                //客户端有objectid的更新到bmob
                                note.update(MainActivity.this, objectId, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        synchronizeSuccess = false;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            synchronizeSuccess = false;
                        }
                    });
                }
                //查找本地没有但bmob有的note（下载）
                objectId = noteLocal.getObjectid();
                BmobQuery<Note> q = new BmobQuery<>();
                q.addWhereNotEqualTo("objectId", objectId);
                andQuerys.add(q);
            }
            downloadQuery.and(andQuerys);
            downloadQuery.findObjects(MainActivity.this, new FindListener<Note>() {
                @Override
                public void onSuccess(List<Note> list) {
                    DbUtil dbUtil = new DbUtil();
                    for (Note note : list) {
                        dbUtil.insertNote(noteDB, note.getTitle(), note.getContents(), note.getDate(), note.getObjectId(), note.getUsername());
                    }
                }

                @Override
                public void onError(int i, String s) {
                    synchronizeSuccess = false;
                }
            });
        } else {//重装的app，所有note重新下载
            downloadQuery.findObjects(this, new FindListener<Note>() {
                @Override
                public void onSuccess(List<Note> list) {
                    DbUtil dbUtil = new DbUtil();
                    for (Note note : list) {
                        dbUtil.insertNote(noteDB, note.getTitle(), note.getContents(), note.getDate(), note.getObjectId(), note.getUsername());
                    }
                }

                @Override
                public void onError(int i, String s) {
                    synchronizeSuccess = false;
                }
            });
        }


        if (synchronizeSuccess) {
            Toast.makeText(MainActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 同步所有note并将username刷新
     */
    public void refreshUsername() {
        //更新本地username
        noteDB = NoteDB.getInstance(this);
        dbUtil = new DbUtil();
        username = app.getUsername();
        dbUtil.updateUsername(noteDB, username);
        //更新网上username
        if (new NetworkState().isNetworkConnected(this)) {
            BmobQuery<Note> noNameQuery = new BmobQuery<>();//查找bmob无名note
            noNameQuery.addWhereEqualTo("username", "");
            noNameQuery.setLimit(100);
            final ArrayList<String> objectIds = new ArrayList<>();
            noNameQuery.findObjects(this, new FindListener<Note>() {
                @Override
                public void onSuccess(List<Note> list) {

                    for (Note note : list) {
                        objectIds.add(note.getObjectId());
                        Log.e("objectId", objectIds.get(0));
                    }

                    List<BmobObject> notes = new ArrayList<>();
                    for (int i = 0; i < objectIds.size(); i++) {
                        Note note = new Note();
                        note.setObjectId(objectIds.get(i));
                        note.setUsername(username);
                        notes.add(note);
                    }
                    new BmobObject().updateBatch(MainActivity.this, notes, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("username更新", "成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    System.out.println("查询错误是" + s);
                }
            });

        }
        //先将username填好，再进行正常更新
        refresh();
    }
}
