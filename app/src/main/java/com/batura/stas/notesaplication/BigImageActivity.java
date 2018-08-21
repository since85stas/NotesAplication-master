package com.batura.stas.notesaplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.batura.stas.notesaplication.ImageFuncs.ImageStorage;
import com.batura.stas.notesaplication.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by seeyo on 21.08.2018.
 */

public class BigImageActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider ;
    Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_big_activity);

        String imageName = getIntent().getExtras().getString(BigImageActivity.class.getSimpleName());
        ImageView imageView = findViewById(R.id.bigImage);
        imageView.setImageBitmap(ImageStorage.getImageBitmap(imageName));
        mBitmap = ImageStorage.getImageBitmap(imageName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_big_image, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        //mShareActionProvider =  menuItem.getActionProvider();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider
                .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        mShareActionProvider.setShareIntent(createShareIntent());

        //TODO: решить проблему с невозможностью поделиться новой записью

        return true;
    }

    private Intent createShareIntent() {
        Bitmap icon = mBitmap;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(f.getAbsolutePath()));
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
        return shareIntent;
    }
}
