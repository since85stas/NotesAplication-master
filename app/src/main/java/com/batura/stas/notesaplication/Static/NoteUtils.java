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
}
