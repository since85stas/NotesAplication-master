package com.batura.stas.notesaplication.ImageFuncs;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class ImageMy implements Serializable {
    private String mName;
    private Drawable mDrawable;

    ImageMy(String name, Drawable drawable) {
        this.mName = name;
        this.mDrawable=drawable;
    }

    public String getName() {
          return(mName);
    }

    public Drawable getDraw() {return (mDrawable);
    }


}
