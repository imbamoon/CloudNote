package com.example.administraor.cloudnote.activity.activity.controller;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.model.SetRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * 设置界面
 */
public class SetFragment extends Fragment {

    private RecyclerView recyclerView;
    private SetRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private GateApplication app;
    private String username;
    private ArrayList<String> sets;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_set, container, false);

        init();
        return view;
    }

    private void init() {
        app = (GateApplication) getActivity().getApplication();
        username = app.getUsername();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        sets = new ArrayList<>();
        String[] listSet = new String[]{"隐私与安全", "云笔记设置", "云协作设置", "关于云笔记", "意见反馈", "检查更新"};//随时可添加新条目
        for (int i = 0; i < listSet.length; i++) {
            sets.add(i, listSet[i]);
        }
        recyclerView.setHasFixedSize(true);

        adapter = new SetRecyclerViewAdapter(sets, getActivity());
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

}
