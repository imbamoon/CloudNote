package com.example.administraor.cloudnote.activity.activity.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administraor.cloudnote.R;

import java.util.ArrayList;

/**
 * Created by zyw on 2016/6/24.
 */
public class SetRecyclerViewAdapter extends RecyclerView.Adapter<SetRecyclerViewAdapter.SetViewHolder> {

    private ArrayList<String> sets;
    private Context context;

    public SetRecyclerViewAdapter(ArrayList<String> sets, Context context) {
        this.sets = sets;
        this.context = context;
    }

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_list_cell, null);
        SetViewHolder viewHolder = new SetViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SetRecyclerViewAdapter.SetViewHolder holder, int position) {
        holder.tvSet.setText(sets.get(position));
        holder.ivArrow.setImageResource(R.mipmap.arrow);
    }


    @Override
    public int getItemCount() {
        return sets.size();
    }

    public class SetViewHolder extends RecyclerView.ViewHolder {
        TextView tvSet;
        ImageView ivArrow;

        public SetViewHolder(View itemView) {
            super(itemView);
            tvSet = (TextView) itemView.findViewById(R.id.tv_set);
            ivArrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
        }
    }
}



