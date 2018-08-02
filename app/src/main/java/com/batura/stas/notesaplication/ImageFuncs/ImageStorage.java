package com.batura.stas.notesaplication.ImageFuncs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by seeyou on 01.08.2018.
 * Класс предназначен для создания, хранилища фотографий для приложения
 *
 */

public final class ImageStorage extends Activity {

    private static String IMAGE_DIR_NAME = ".notes_images_dir";

    public static  String saveToSdCard(Bitmap bitmap, String filename) {

        String stored = null;

        //ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        //File file = wrapper.getDir(IMAGE_DIR_NAME,MODE_PRIVATE);        //File file = wrap.getDir("Images",MODE_PRIVATE);

        //File file = getFilesDir();
        //Log.i("File", file.toString());
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) ;
        boolean temp = sdcard.mkdirs();
        //String yes = Environment.getExternalStorageState(Environment.DIRECTORY_PICTURES);
        File internal = Environment.getRootDirectory();

        File folder = new File(sdcard.getAbsoluteFile(), IMAGE_DIR_NAME);//the dot makes this directory hidden to the user
        boolean success  = folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename + ".jpg") ;
        if (file.exists())
            return stored ;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stored;
    }

    public static File getImage(String imagename) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/" + IMAGE_DIR_NAME + imagename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(String imagename)
    {
        Bitmap b = null ;
        File file = ImageStorage.getImage("/"+imagename+".jpg");
        String path = file.getAbsolutePath();

        if (path != null)
            b = BitmapFactory.decodeFile(path);

        if(b == null ||  b.equals(""))
        {
            return false ;
        }
        return true ;
    }
}
