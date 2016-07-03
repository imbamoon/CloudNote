package com.example.administraor.cloudnote.activity.activity.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.Utils.NetworkState;
import com.example.administraor.cloudnote.activity.activity.model.ClickableImageSpan;
import com.example.administraor.cloudnote.activity.activity.model.ClickableMovementMethod;
import com.example.administraor.cloudnote.activity.activity.model.Note;
import com.example.administraor.cloudnote.activity.activity.model.NoteDB;
import com.example.administraor.cloudnote.activity.activity.Utils.DbUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class EditNoteActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    String TAG = "editNoteActivitya";
    private Toolbar editBar;
    private MaterialEditText titleEdit;
    private MaterialEditText contentEdit;
    private NoteDB noteDB;
    private DbUtil dbUtil;
    private String noteTitle;
    private String noteContent;
    private String strDate;
    private String title, content;
    private String objectId;
    private String username;
    private Boolean isAddNote;
    private final int SYSTEM_PICTURE = 1;
    private final int IMAGE_CAPTURE = 2;
    private final int GET_VIDEO = 3;
    private final int VIDEO_CAPTURE = 4;
    private Uri outputUri;
    private Bitmap bitmapCapture;
    public GateApplication app;
    private int i;//缩略图计数
    private ClickableImageSpan clickableImageSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        isAddNote = getIntent().getExtras().get("title").toString().equals("");
        editBar = (Toolbar) findViewById(R.id.noteBar);
        titleEdit = (MaterialEditText) findViewById(R.id.titleEdit);
        contentEdit = (MaterialEditText) findViewById(R.id.contentEdit);
        title = getIntent().getExtras().get("title").toString();
        content = getIntent().getExtras().get("content").toString();
        app = (GateApplication) getApplication();
        username = app.getUsername();

        if (title != null && content != null) {
            titleEdit.setText(title);
            SpannableString ss = new SpannableString(content);
            //匹配图片
            matchImage(ss);
            //匹配视频
            matchVideo(ss);
            contentEdit.setText(ss);
        }
        noteDB = NoteDB.getInstance(this);
        editBar.setTitle("编辑笔记");
        setSupportActionBar(editBar);
        //Navigation和MenuItem一定要放在setSupportActionBar之后
        editBar.setOnMenuItemClickListener(this);
        editBar.setNavigationIcon(R.drawable.back1);
        editBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_save:

                noteTitle = titleEdit.getText().toString();
                noteContent = contentEdit.getText().toString();
                if (noteTitle.equals("")) {
                    Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (noteContent.equals("")) {
                    Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    break;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                strDate = formatter.format(curDate);

                saveSmallImage(new SpannableString(noteContent), strDate);//保存缩略图

                //如果是添加note
                if (isAddNote) {

                    final Note note = new Note(noteTitle, noteContent, strDate, username);

                    if (new NetworkState().isNetworkConnected(this)) {
                        //有网未登陆
                        if (username.equals("")) {
                            dbUtil = new DbUtil();
                            dbUtil.insertNote(noteDB, noteTitle, noteContent, strDate, username);
                            finish();
                        } else {
                            //上传至bmob
                            note.save(this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(EditNoteActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
                                    //存入本地数据库
                                    dbUtil = new DbUtil();
                                    String objectid = note.getObjectId();
                                    dbUtil.insertNote(noteDB, noteTitle, noteContent, strDate, objectid, username);
                                    finish();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(EditNoteActivity.this, "创建数据失败：" + s, Toast.LENGTH_SHORT).show();
                                    //存入本地数据库
                                    dbUtil = new DbUtil();
                                    dbUtil.insertNote(noteDB, noteTitle, noteContent, strDate, username);
                                    finish();
                                }
                            });
                        }

                    } else {
                        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        //存入本地数据库
                        dbUtil = new DbUtil();
                        dbUtil.insertNote(noteDB, noteTitle, noteContent, strDate, username);
                        finish();
                    }
                }
                //如果是编辑note
                else if (!isAddNote) {

                    if (new NetworkState().isNetworkConnected(this)) {
                        //未登陆
                        if (username.equals("")) {
                            //更新本地数据库相应条目
                            String oldTitle = getIntent().getExtras().get("title").toString();
                            String oldDate = getIntent().getExtras().get("date").toString();
                            DbUtil dbUtil = new DbUtil();
                            dbUtil.updateNote(noteDB, oldTitle, oldDate, noteTitle, noteContent, strDate, username);
                            finish();
                            break;//finish()后还会继续执行后面代码，所以别忘break
                        }
                        //更新bmob
                        objectId = getIntent().getExtras().get("objectId").toString();
                        Note note = new Note();
                        note.setTitle(noteTitle);
                        note.setContents(noteContent);
                        note.setDate(strDate);
                        note.update(this, objectId, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(EditNoteActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                                //更新本地数据库相应条目
                                String oldTitle = getIntent().getExtras().get("title").toString();
                                String oldDate = getIntent().getExtras().get("date").toString();
                                DbUtil dbUtil = new DbUtil();
                                dbUtil.updateNote(noteDB, oldTitle, oldDate, noteTitle, noteContent, strDate, username);
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(EditNoteActivity.this, R.string.update_fail, Toast.LENGTH_SHORT).show();
                                //更新本地数据库相应条目
                                String oldTitle = getIntent().getExtras().get("title").toString();
                                String oldDate = getIntent().getExtras().get("date").toString();
                                DbUtil dbUtil = new DbUtil();
                                dbUtil.updateNote(noteDB, oldTitle, oldDate, noteTitle, noteContent, strDate, username);
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        //更新本地数据库相应条目
                        String oldTitle = getIntent().getExtras().get("title").toString();
                        String oldDate = getIntent().getExtras().get("date").toString();
                        DbUtil dbUtil = new DbUtil();
                        dbUtil.updateNote(noteDB, oldTitle, oldDate, noteTitle, noteContent, strDate, username);
                        finish();
                    }
                }
                break;
            case R.id.add_photo:
                Dialog dialog = new AlertDialog.Builder(this).setTitle("选择图片来源")
                        .setPositiveButton("系统图片", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                getImage.setType("image/*");
                                startActivityForResult(getImage, SYSTEM_PICTURE);
                            }
                        }).setNegativeButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Date now = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                                String dateString = dateFormat.format(now);
                                File file = new File(Environment.getExternalStorageDirectory(), "(" + dateString + ")" + ".png");//用当前时间命名避免重复
                                outputUri = Uri.fromFile(file);
                                Intent takeCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takeCapture.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                                startActivityForResult(takeCapture, IMAGE_CAPTURE);
                            }
                        }).create();
                dialog.show();

                break;
            case R.id.add_video:
                Dialog alertDialog = new AlertDialog.Builder(EditNoteActivity.this).setTitle("选择视频来源")
                        .setPositiveButton("系统视频", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent getVideo = new Intent(Intent.ACTION_GET_CONTENT);
                                getVideo.addCategory(Intent.CATEGORY_OPENABLE);
                                getVideo.setType("video/*");
                                startActivityForResult(getVideo, GET_VIDEO);
                            }
                        })
                        .setNegativeButton("录像", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                startActivityForResult(videoCapture, VIDEO_CAPTURE);
                            }
                        }).create();
                alertDialog.show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SYSTEM_PICTURE://系统图片
                    Uri originalUriPicture = data.getData();
                    try {
                        Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUriPicture));
                        int imgWidth = originalBitmap.getWidth();
                        int imgHeight = originalBitmap.getHeight();
                        int scale = Math.min(imgWidth / 300, imgHeight / 300);
                        scale = scale == 0 ? 1 : scale;
                        Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, imgWidth / scale, imgHeight / scale, true);
                        insertToEditText(getBitmapMime(bitmap, originalUriPicture, this), contentEdit);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case GET_VIDEO://系统视频
                    Uri originalUriVideo = data.getData();
                    Bitmap bitmapGet = drawPlayVideoToBitmap(this);
                    insertToEditText(getBitmapMime(bitmapGet, originalUriVideo, this), contentEdit);
                    break;

                case VIDEO_CAPTURE://录视频
                    outputUri = data.getData();
                    Bitmap bitmapCapture = drawPlayVideoToBitmap(this);
                    insertToEditText(getBitmapMime(bitmapCapture, outputUri, this), contentEdit);
                    break;

                case IMAGE_CAPTURE://拍照
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(outputUri.getPath(), options);
                    int imgWidth = options.outWidth;
                    int imgHeight = options.outHeight;

                    int scale = Math.min(imgWidth / 300, imgHeight / 300);//获取缩放比例
                    scale = scale == 0 ? 1 : scale;

                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scale;

                    bitmapCapture = BitmapFactory.decodeFile(outputUri.getPath(), options);
                    insertToEditText(getBitmapMime(bitmapCapture, outputUri, this), contentEdit);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

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

    /**
     * 用spannablestring包装path，用imagespan包装bitmap，将bitmap替代path生成新的spannablestring
     *
     * @param bitmap
     * @param uri
     * @param context
     * @return
     */
    private SpannableString getBitmapMime(Bitmap bitmap, Uri uri, Context context) {
        final String path = getRealFilePath(context, uri);
        SpannableString ss = new SpannableString(path);
        ClickableImageSpan clickableImageSpan = new ClickableImageSpan(this,bitmap) {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditNoteActivity.this, PlayVideoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        };
        contentEdit.setMovementMethod(ClickableMovementMethod.getInstance());
        ss.setSpan(clickableImageSpan, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 将构造好的spannablestring插入Editable中
     *
     * @param ss
     * @param editText
     */
    private void insertToEditText(SpannableString ss, MaterialEditText editText) {
        Editable et = editText.getEditableText();
        int start = editText.getSelectionStart();
        if (start < 0 || start >= editText.length()) {
            et.append(ss);
        } else {
            et.insert(start, ss);
        }
        editText.setText(et);
        editText.setSelection(start + ss.length());  //设置光标在插入的图片后面
    }

    /**
     * 通过Uri获取真实文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 匹配图片并按比例缩放后插入SpannableString
     *
     * @param ss
     */
    private void matchImage(SpannableString ss) {
        Pattern patternDraw = Pattern.compile("(/storage/emulated/|storage/sdcard)([^.]+\\([^)]*\\)|[^.]+)\\.(png|webp|jpeg|jpg)");
        final Matcher matcherDraw = patternDraw.matcher(ss.toString());
        while (matcherDraw.find()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(matcherDraw.group(), options);
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;
            int scale = Math.min(imgWidth / 300, imgHeight / 300);//获取缩放比例
            scale = scale == 0 ? 1 : scale;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(matcherDraw.group(), options);
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            ss.setSpan(imageSpan, matcherDraw.start(), matcherDraw.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 保存图片缩略图放在SharedPreferences中
     *
     * @param ss
     */
    public void saveSmallImage(SpannableString ss, String date) {
        Pattern patternImage = Pattern.compile("(/storage/emulated/|storage/sdcard)([^.]+\\([^)]*\\)|[^.]+)\\.(png|webp|jpeg|jpg)");
        Matcher matcherImage = patternImage.matcher(ss.toString());
        Pattern patternVideo = Pattern.compile("(/storage/emulated/|storage/sdcard)([^.]+\\([^)]*\\)|[^.]+)\\.(mp4|3gp)");
        Matcher matcherVideo = patternVideo.matcher(ss.toString());

        i = 0;
        while (matcherImage.find() && i < 2) {//最多存2张图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(matcherImage.group(), options);
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;
            int scale = Math.min(imgWidth / 80, imgHeight / 80);
            scale = scale == 0 ? 1 : scale;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap smallBitmap = BitmapFactory.decodeFile(matcherImage.group(), options);

            //int position = (int) getIntent().getExtras().get("position");
            //File file = new File(Environment.getExternalStorageDirectory(), "(SmallImage" + position + ":" + i + ").png");
            //file.createNewFile();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);//把bitmap对象解析成流
            String image64 = new String(Base64.encodeBase64(outputStream.toByteArray()));
            SharedPreferences preferences = getSharedPreferences("SmallImage:" + date, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(i + "", image64);
            editor.commit();
            //app.setPath(Environment.getExternalStorageDirectory() + "(SmallImage" + position + ":" + i + ").png", position);
            i++;
        }
        if (matcherVideo.find()){
            SharedPreferences preferences = getSharedPreferences("SmallImage:" + date, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("video", 1+"");
            editor.commit();
        }else {
            SharedPreferences preferences = getSharedPreferences("SmallImage:" + date, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("video", 0+"");
            editor.commit();
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
                    final Intent intent = new Intent(EditNoteActivity.this, PlayVideoActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
            };
            //if (clickableImageSpan.getDrawable().getBounds().contains((int) x, (int) y)) {
            contentEdit.setMovementMethod(ClickableMovementMethod.getInstance());
            //}
            ss.setSpan(clickableImageSpan, matcherVideo.start(), matcherVideo.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
