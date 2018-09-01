package com.batura.stas.notesaplication.Other;

import android.view.Menu;

import java.util.ArrayList;

/**
 * NavMenuClass holds the NavigationView menu and an ArrayList which holds items to be populated in menu.
 */

public class NavMenuClass{
    Menu menu;
    ArrayList items;

    public NavMenuClass(Menu menu,ArrayList items){
        this.items = items;
        this.menu = menu;
    }

    public Menu getMenu(){
        return menu;
    }

    public ArrayList getItems(){
        return items;
    }

}
