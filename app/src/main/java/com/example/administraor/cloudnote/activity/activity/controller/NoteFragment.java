package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.Utils.NetworkState;
import com.example.administraor.cloudnote.activity.activity.model.NoteDB;
import com.example.administraor.cloudnote.activity.activity.model.RecyclerViewAdapter;
import com.example.administraor.cloudnote.activity.activity.model.Note;
import com.example.administraor.cloudnote.activity.activity.Utils.DbUtil;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.DeleteListener;

/**
 * 显示笔记列表
 */
public class NoteFragment extends Fragment implements View.OnClickListener, RecyclerViewAdapter.OnItemClickListener, RecyclerViewAdapter.OnItemLongClickListener {
    String TAG = "NoteFragment";
    @Bind(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    @Bind(R.id.addNoteBtn)
    FloatingActionButton mAddNoteBtn;
    public RecyclerViewAdapter mRecAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteDB noteDB;
    private String username;
    private ArrayList<Note> notes;
    private DbUtil dbUtil;
    public GateApplication app;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, null);
        ButterKnife.bind(this, view);

        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getActivity(), EditNoteActivity.class);
        intent.putExtra("title", "");
        intent.putExtra("content", "");
        intent.putExtra("position",mRecAdapter.getItemCount());
        startActivity(intent);
    }


    @Override
    public void onItemClick(View view) {
        Intent intent = new Intent(getActivity(), SeeNoteActivity.class);
        int position = (int) view.getTag();
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final View view) {
        Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("确定删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = (int) view.getTag();
                        DbUtil dbUtil = new DbUtil();
                        ArrayList<Note> noteList = dbUtil.getNotes(noteDB, username);
                        String title = noteList.get(position).getTitle();
                        String date = noteList.get(position).getDate();
                        String objectId = noteList.get(position).getObjectid();
                        dbUtil.deleteNote(noteDB, title, date);
                        if (objectId != null){
                            if (new NetworkState().isNetworkConnected(NoteFragment.this.getActivity())) {
                                Note note = new Note();
                                note.setObjectId(objectId);
                                note.delete(NoteFragment.this.getActivity(), new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(NoteFragment.this.getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                    }
                                });
                            }else{
                                Toast.makeText(NoteFragment.this.getActivity(), "当前无网络", Toast.LENGTH_SHORT).show();
                            }
                        }
                        init();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
    }

    private void init(){
        app = (GateApplication) getActivity().getApplication();
        username = app.getUsername();
        noteDB = NoteDB.getInstance(getActivity());
        dbUtil = new DbUtil();
        notes = dbUtil.getNotes(noteDB, username);
        mRecyclerView.setHasFixedSize(true);          //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecAdapter = new RecyclerViewAdapter(notes, getActivity());
        mAddNoteBtn.setOnClickListener(this);
        mRecAdapter.setOnItemClickListener(this);
        mRecAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mRecAdapter);
    }
}
