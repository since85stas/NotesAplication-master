package com.batura.stas.notesaplication.data;

public class NoteFirePresenter {

    public int id;
    public String title;
    public String body;
    public int color;
    public long time;
    public int favorite;
    public int password;
    public int passwordHash;
    public int imageId;
    public int widget;
    public int folder;


    public NoteFirePresenter(int id, String title, String body, int color, long time, int favorite, int password, int passwordHash, int imageId, int widget, int folder) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.color = color;
        this.time  = time;
        this.favorite = favorite;
        this.password = password;
        this.passwordHash = passwordHash;
        this.imageId = imageId;
        this.widget = widget;
        this.folder = folder;

    }
}
