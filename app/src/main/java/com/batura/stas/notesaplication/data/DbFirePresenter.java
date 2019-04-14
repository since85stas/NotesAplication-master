package com.batura.stas.notesaplication.data;

import java.util.ArrayList;
import java.util.List;

public class DbFirePresenter {

    private String name;
    private String text;
    private List<String> strings;

    public DbFirePresenter(String name, String text) {
        this.name = name;
        this.text = text;
        strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
