package com.batura.stas.notesaplication.Static;

import com.batura.stas.notesaplication.R;
import com.batura.stas.notesaplication.data.NoteContract;

/**
 * Created by seeyo on 07.06.2018.
 */

public final class NoteUtils {

    private NoteUtils(){
    }

    public static int getBackColor(int colorId) {

        int backColorResId = 0;
        switch (colorId) {
            case NoteContract.NoteEntry.COLOR_DEFAULT:
                backColorResId = R.color.defaultBack;
                break;
            case NoteContract.NoteEntry.COLOR_RED:
                backColorResId = R.color.redBack;
                break;
            case NoteContract.NoteEntry.COLOR_ORANGE:
                backColorResId = R.color.orangeBack;
                break;
            case NoteContract.NoteEntry.COLOR_YELLOW:
                backColorResId = R.color.yellowBack;
                break;
            case NoteContract.NoteEntry.COLOR_GREEN:
                backColorResId = R.color.greenBack;
                break;
            case NoteContract.NoteEntry.COLOR_BLUE:
                backColorResId = R.color.blueBack;
                break;
            case NoteContract.NoteEntry.COLOR_PURPLE:
                backColorResId = R.color.purpleBack;
                break;
        }
        return backColorResId;
    }

    public static int getBackColorLight(int colorId) {

        int backColorResId = 0;
        switch (colorId) {
            case NoteContract.NoteEntry.COLOR_DEFAULT:
                backColorResId = R.color.defaultBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_RED:
                backColorResId = R.color.redBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_ORANGE:
                backColorResId = R.color.orangeBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_YELLOW:
                backColorResId = R.color.yellowBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_GREEN:
                backColorResId = R.color.greenBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_BLUE:
                backColorResId = R.color.blueBackLight;
                break;
            case NoteContract.NoteEntry.COLOR_PURPLE:
                backColorResId = R.color.purpleBackLight;
                break;
        }
        return backColorResId;
    }


    public static int getBackColorAccent(int colorId) {

        int backColorResId = 0;
        switch (colorId) {
            case NoteContract.NoteEntry.COLOR_DEFAULT:
                backColorResId = R.color.defaultBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_RED:
                backColorResId = R.color.redBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_ORANGE:
                backColorResId = R.color.orangeBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_YELLOW:
                backColorResId = R.color.yellowBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_GREEN:
                backColorResId = R.color.greenBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_BLUE:
                backColorResId = R.color.blueBackAccent;
                break;
            case NoteContract.NoteEntry.COLOR_PURPLE:
                backColorResId = R.color.purpleBackAccent;
                break;
        }
        return backColorResId;
    }

    public static int getColorPosisById (int colorId) {
        int position = 0;
        switch (colorId) {
            case NoteContract.NoteEntry.COLOR_DEFAULT:
                position = 0;
                break;
            case NoteContract.NoteEntry.COLOR_RED:
                position = 1;
                break;
            case NoteContract.NoteEntry.COLOR_ORANGE:
                position = 2;
                break;
            case NoteContract.NoteEntry.COLOR_YELLOW:
                position = 3;
                break;
            case NoteContract.NoteEntry.COLOR_GREEN:
                position = 4;
                break;
            case NoteContract.NoteEntry.COLOR_BLUE:
                position = 5;
                break;
            case NoteContract.NoteEntry.COLOR_PURPLE:
                position = 6;
                break;
        }
        return position;
    }
}
