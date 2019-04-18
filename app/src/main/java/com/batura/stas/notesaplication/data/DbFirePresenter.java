package com.batura.stas.notesaplication.data;

import android.database.Cursor;
import android.util.Log;

import com.batura.stas.notesaplication.Other.Folder;

import java.util.ArrayList;
import java.util.List;

public class DbFirePresenter {

    private String TAG = DbFirePresenter.class.getName();

    private String name;
    private String text;
    private List<String> strings;
    private Cursor cursor;
    private List<NoteFirePresenter> notesPresenter;

    public DbFirePresenter(String name, Cursor cursor) {
        this.name = name;
        this.text = text;
        this.cursor = cursor;

        getDataFromSql(cursor);

        strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
    }

    private void getDataFromSql(Cursor cursor) {

        notesPresenter = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int idIColoumnIndex  =  cursor.getColumnIndex(NoteContract.NoteEntry._ID);
            int titleColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE);
            int bodyColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY);
            int dateColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TIME);
            int colorColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
            int favColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE);
            int notifColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_WIDGET);
            int imageColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE);
            int folderColoumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_FOLDER);


            do  {
                String noteTitle = cursor.getString(titleColoumnIndex);
                String noteBody = cursor.getString(bodyColoumnIndex);
                Long noteTime = cursor.getLong(dateColoumnIndex);
                int noteId = cursor.getInt(idIColoumnIndex);
                int noteColor = cursor.getInt(colorColoumnIndex);
                int noteIsFavor = cursor.getInt(favColoumnIndex);
                int noteNotifIsOn = cursor.getInt(notifColoumnIndex);
                int hasImages = cursor.getInt(imageColoumnIndex);
                int folderId = cursor.getInt(folderColoumnIndex);
                NoteFirePresenter noteFirePresenter = new NoteFirePresenter( noteId,
                        noteTitle,
                        noteBody,
                        noteColor,
                        noteTime,
                        noteIsFavor,
                        0,
                        0,
                        hasImages,
                        noteNotifIsOn,
                        folderId
                );
                notesPresenter.add(noteFirePresenter);
            } while (cursor.moveToNext());

            Log.i(TAG, "getFoldersFormDb: ");
        }
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public List<NoteFirePresenter> getNotesPresenter() {
        return notesPresenter;
    }
}
