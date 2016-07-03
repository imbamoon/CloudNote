package com.example.administraor.cloudnote.activity.activity.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

/**
 * Created by zyw on 2016/6/12.
 */
public abstract class ClickableImageSpan extends ImageSpan {


    public ClickableImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public abstract void onClick(View view);
}
