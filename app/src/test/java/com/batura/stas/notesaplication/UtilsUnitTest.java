package com.batura.stas.notesaplication;

import org.junit.Before;
import org.junit.Test;

import static com.batura.stas.notesaplication.Static.NoteUtils.*;
import static com.batura.stas.notesaplication.data.NoteContract.NoteEntry.*;
import static org.assertj.core.api.Assertions.assertThat;


public class UtilsUnitTest {

    @Before
    public void createConst() {

    }

    @Test
    public void getBackColorTest() {
        assertThat(getBackColor(COLOR_DEFAULT)).isEqualTo(R.color.defaultBack);
        assertThat(getBackColor(-1000)).isEqualTo(R.color.defaultBack);
    }

}
