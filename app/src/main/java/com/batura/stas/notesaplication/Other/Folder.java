package com.batura.stas.notesaplication.Other;

public class Folder {
    private int folderId;
    private String folderName;

    public int getFolderId() {
        return folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public Folder () {}

    public Folder(int id, String name) {
        folderId = id;
        folderName = name;
    }


}
