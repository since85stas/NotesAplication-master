package com.batura.stas.notesaplication.ImageFuncs;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.batura.stas.notesaplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.stream.Stream;

/**
 * Created by seeyou on 01.08.2018.
 * Класс предназначен для создания, хранилища фотографий для приложения
 *
 */

public class ImageStorage extends Activity {

    private static String IMAGE_DIR_NAME = ".notes_images_dir";

    public static String saveToSdCard(Bitmap bitmap, String filename) {

        String stored = null;
        File sdcard = Environment.getExternalStorageDirectory() ;
        File folder = new File(sdcard.getAbsoluteFile(), IMAGE_DIR_NAME);//the dot makes this directory hidden to the user
        boolean isWrite = isExternalStorageWritable();
        boolean fold = folder.isDirectory();
        if (!fold) {
            boolean yes = folder.mkdirs();
        }
        //File file = new File(folder.getAbsoluteFile(), filename + ".jpg") ;
        File file = new File(folder.getAbsoluteFile(), filename ) ;
        if (file.exists())
            return stored ;
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            stored = "success";
            //Toast.makeText(this, getString(R.string.editor_insert_note_failed), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stored;
    }

    public static String deleteFromSd(String fileName) {
        String delete = null;
        File sdcard = Environment.getExternalStorageDirectory() ;
        File folder = new File(sdcard.getAbsoluteFile(), IMAGE_DIR_NAME);//the dot makes this directory hidden to the user
        boolean isWrite = isExternalStorageWritable();
        boolean fold = folder.isDirectory();
        File file = new File(folder.getAbsoluteFile(), fileName ) ;
        boolean exist = file.exists();
        if (exist) {
            file.delete();
            delete = "delete";
        }

        return delete;
    }


    public static File getImage(String imagename) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/" + IMAGE_DIR_NAME +"/"+ imagename );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }

    public static Bitmap getImageBitmap (String imagename) {
        Bitmap b = null ;
        File file = ImageStorage.getImage(imagename);
        String path = file.getAbsolutePath();

        if (path != null)
            b = BitmapFactory.decodeFile(path);

        if(b == null )
        {
            return b ;
        }

        return b;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
