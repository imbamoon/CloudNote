package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.Utils.NetworkState;
import com.example.administraor.cloudnote.activity.activity.model.ClickableImageSpan;
import com.example.administraor.cloudnote.activity.activity.model.ClickableMovementMethod;
import com.example.administraor.cloudnote.activity.activity.model.NoteDB;
import com.example.administraor.cloudnote.activity.activity.model.Note;
import com.example.administraor.cloudnote.activity.activity.Utils.DbUtil;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.bmob.v3.listener.DeleteListener;

public class SeeNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTitle, tvContent;
    private ArrayList<Note> noteList;
    private DbUtil dbUtil;
    private NoteDB noteDB;
    private ImageButton btnEdit, btnDelete;
    private String title, date, objectId, content, username;
    private int position;
    public GateApplication app;
    private float x, y;
    private ClickableImageSpan clickableImageSpan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_note);

        app = (GateApplication) getApplication();
        username = app.getUsername();
        tvTitle = (TextView) findViewById(R.id.title);
        tvContent = (TextView) findViewById(R.id.content);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnEdit = (ImageButton) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        noteDB = NoteDB.getInstance(this);
        dbUtil = new DbUtil();
        noteList = dbUtil.getNotes(noteDB, username);
        position = (int) getIntent().getExtras().get("position");
        title = noteList.get(position).getTitle();
        date = noteList.get(position).getDate();
        objectId = noteList.get(position).getObjectid();
        tvTitle.setText(title);
        content = noteList.get(position).getContents();
        SpannableString ss = new SpannableString(content);
        //匹配图片
        matchImage(ss);
        //匹配视频
        matchVideo(ss);
        tvContent.setText(ss);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back1);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        tvContent.setOnTouchListener(this);
    }

    /**
     * 将play_video资源文件转化为bitmap
     *
     * @param context
     * @return
     */
    private Bitmap drawPlayVideoToBitmap(Context context) {
        //从资源中取出一副图片
        Drawable drawable = context.getResources().getDrawable(R.mipmap.play_video, null);
        //设置图片大小
        if (drawable != null) {
//            WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
            drawable.setBounds(0, 0, 500, 500);
            //从原始bitmap创建一个bitmap
            Bitmap bitmap = Bitmap.createBitmap(500, 500, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565);
            //创建一个带有已创建bitmap的画布
            Canvas canvas = new Canvas(bitmap);
            //将图片画在bitmap的画布上
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelete:
                Dialog alertDialog = new AlertDialog.Builder(SeeNoteActivity.this)
                        .setTitle("确定删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbUtil.deleteNote(NoteDB.getInstance(SeeNoteActivity.this), title, date);
                                if (new NetworkState().isNetworkConnected(SeeNoteActivity.this)) {
                                    Note note = new Note();
                                    note.setObjectId(objectId);
                                    note.delete(SeeNoteActivity.this, new DeleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(SeeNoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                        }
                                    });
                                } else {
                                    Toast.makeText(SeeNoteActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
                                }
                                startActivity(new Intent(SeeNoteActivity.this, MainActivity.class));

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();
                break;
            case R.id.btnEdit:
                Intent intent = new Intent(SeeNoteActivity.this, EditNoteActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("content",content);
                intent.putExtra("date", date);
                intent.putExtra("objectId", objectId);
                intent.putExtra("position",position);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dbUtil = new DbUtil();
        app = (GateApplication) getApplication();
        username = app.getUsername();
        noteList = dbUtil.getNotes(noteDB, username);
        title = noteList.get(position).getTitle();
        date = noteList.get(position).getDate();
        objectId = noteList.get(position).getObjectid();
        tvTitle.setText(title);
        String content = noteList.get(position).getContents();
        SpannableString ss = new SpannableString(content);

        //匹配图像
        matchImage(ss);

        //匹配视频
        matchVideo(ss);
        tvContent.setText(ss);
    }

    /**
     * 匹配图片
     *
     * @param ss
     */
    private void matchImage(SpannableString ss) {
        Pattern patternDraw = Pattern.compile( "(/storage/emulated/|storage/sdcard)([^.]+\\([^)]*\\)|[^.]+)\\.(png|webp|jpeg|jpg)");
        final Matcher matcherDraw = patternDraw.matcher(ss.toString());
        while (matcherDraw.find()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(matcherDraw.group(),options);
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;
            int scale = Math.min(imgWidth / 300, imgHeight / 300);//获取缩放比例
            scale = scale == 0 ? 1 : scale;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(matcherDraw.group(),options);
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            ss.setSpan(imageSpan, matcherDraw.start(), matcherDraw.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 匹配视频
     *
     * @param ss
     */
    private void matchVideo(SpannableString ss) {
        Pattern patternVideo = Pattern.compile("(/storage/emulated/|storage/sdcard)([^.]+\\([^)]*\\)|[^.]+)\\.(mp4|3gp)");
        final Matcher matcherVideo = patternVideo.matcher(ss.toString());
        while (matcherVideo.find()) {
            final String path = matcherVideo.group();
            Bitmap bitmapPlayVideo = drawPlayVideoToBitmap(this);
            clickableImageSpan = new ClickableImageSpan(this, bitmapPlayVideo) {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(SeeNoteActivity.this, PlayVideoActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
            };
            //if (clickableImageSpan.getDrawable().getBounds().contains((int) x, (int) y)) {
            tvContent.setMovementMethod(ClickableMovementMethod.getInstance());
            //}
            ss.setSpan(clickableImageSpan, matcherVideo.start(), matcherVideo.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            x = event.getX();
//            y = event.getY();
//        }
//        return true;
//    }
}