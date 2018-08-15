package com.batura.stas.notesaplication.ImageFuncs;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class ImageMy implements Serializable {
    private String mName;
    private Drawable mDrawable;
    private Bitmap mBitmap;

    public ImageMy(String name, Bitmap bitmap) {
        this.mName = name;
        //this.mDrawable=drawable;
        this.mBitmap = bitmap;
    }

    public String getName() {
          return(mName);
    }

    public Drawable getDraw() {return (mDrawable);
    }

    public Bitmap getBitmap() {return (mBitmap);}


}
