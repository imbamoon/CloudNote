package com.example.administraor.cloudnote.activity.activity.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.controller.GateApplication;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Note> notes;
    private Context context;
    private GateApplication app;
    //声明接口的变量
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    /**
     * 定义接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view);
    }

    /**
     * 暴露给外面的调用者
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 构造函数
     *
     * @param notes
     * @param context
     */
    public RecyclerViewAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onItemClickListener, onItemLongClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        app = new GateApplication();
        //String path = app.getPath(position);
        holder.titleText.setText(notes.get(position).getTitle());
        holder.date.setText(notes.get(position).getDate());
        holder.cardView.setTag(position);

        SharedPreferences preferences = context.getSharedPreferences("SmallImage:" + notes.get(position).getDate(), Context.MODE_PRIVATE);
        String image0 = preferences.getString("0", "");
        if (!image0.equals("")) {
            byte[] images = org.apache.commons.codec.binary.Base64.decodeBase64(image0.getBytes());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(images);
            holder.imageView1.setImageBitmap(BitmapFactory.decodeStream(inputStream));
        }

        String image1 = preferences.getString("1", "");
        if (!image1.equals("")) {
            byte[] images = org.apache.commons.codec.binary.Base64.decodeBase64(image1.getBytes());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(images);
            holder.imageView2.setImageBitmap(BitmapFactory.decodeStream(inputStream));
        }

        String image2 = preferences.getString("video", "");
        if (image2.equals(1 + "")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), R.mipmap.play_video, options);
            int width = options.outWidth;
            int height = options.outHeight;
            int scale = Math.min(width / 80, height / 80);
            scale = scale == 0 ? 1 : scale;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap bitmapScaled = BitmapFactory.decodeResource(context.getResources(), R.mipmap.play_video);
            holder.imageView3.setImageBitmap(bitmapScaled);
        }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Bind(R.id.titleText)
        TextView titleText;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.card_view)
        CardView cardView;
        @Bind(R.id.image1)
        ImageView imageView1;
        @Bind(R.id.image2)
        ImageView imageView2;
        @Bind(R.id.image3)
        ImageView imageView3;
        OnItemClickListener onItemClickListener;
        OnItemLongClickListener onItemLongClickListener;

        public ViewHolder(View view, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
            super(view);
            ButterKnife.bind(this, view);
            this.onItemClickListener = onItemClickListener;
            this.onItemLongClickListener = onItemLongClickListener;
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v);
        }

        @Override
        public boolean onLongClick(View v) {
            onItemLongClickListener.onItemLongClick(v);
            return false;
        }
    }
}
