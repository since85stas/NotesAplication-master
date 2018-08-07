/*
* MainActivity.java
*
* version 1.1
*
* Автор Батура Стас 10.07.2018
*/

package com.batura.stas.notesaplication;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SearchEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import com.batura.stas.notesaplication.data.NoteContract;
import com.batura.stas.notesaplication.data.NoteDbHelper;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTE_LOADER = 0;

    private NoteDbHelper mDbHelper;
    private NoteCursorAdapter mCursorAdapter;
    private Spinner mOrderBySpinner;
    private SearchView mSearchView;
    private String mSearchString = null; // search request
    private String mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_TIME; // по какому столбцу упорядочивание списка

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale locale = new Locale("en"); // задаем локаль, пока принудительно
        Locale.setDefault(locale);

        // определям action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // определяем спинер для упорядочивания заметок
        mOrderBySpinner = (Spinner) findViewById(R.id.orderBySpinner);
        setupOrderSpinner();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a note", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        /* задаем listView для списка заметок
         * проверяем изменолись ли данные
         * при нажатии на заметку вызывается редактирование заметки класс EditorAcrtivity
         */
        ListView noteListView = (ListView) findViewById(R.id.list);
        mCursorAdapter = new NoteCursorAdapter(this, null);
        noteListView.setAdapter(mCursorAdapter);
        mCursorAdapter.notifyDataSetChanged();
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // вызываем намерение на поиск выражения в тексте заметки
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchString = intent.getStringExtra(SearchManager.QUERY);
            //System.out.println(query);
        }


        getLoaderManager().initLoader(NOTE_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        setupSearchResult(searchMenuItem);
        return true;
    }


    private void setupSearchResult(MenuItem searchItem) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//        if(id == R.id.add_dummy_data) {
//            insertNote();
//            //displayDatabaseInfo();
//        }
        return super.onOptionsItemSelected(item);
    }

//        private void insertNote() {
//            SQLiteDatabase db = mDbHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put(NoteContract.NoteEntry.COLUMN_NOTE_TITLE, "Test");
//            values.put(NoteContract.NoteEntry.COLUMN_NOTE_BODY, "Test note");
//            values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, 1);
//            values.put(NoteContract.NoteEntry.COLUMN_NOTE_TIME, 7);
//            // Insert a new row for Toto in the database, returning the ID of that new row.
//            // The first argument for db.insert() is the pets table name.
//            // The second argument provides the name of a column in which the framework
//            // can insert NULL in the event that the ContentValues is empty (if
//            // this is set to "null", then the framework will not insert a row when
//            // there are no values).
//            //Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI,values);
//            long newRowId = db.insert(NoteContract.NoteEntry.IMAGE_TABLE_NAME, null, values);
//        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // задаем проекцию по которой будет происходить считывние из БД
        String[] projection = new String[]{
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE_BODY,
                NoteContract.NoteEntry.COLUMN_NOTE_COLOR,
                NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE,
                NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD,
                NoteContract.NoteEntry.COLUMN_NOTE_PASSWORD_HASH,
                NoteContract.NoteEntry.COLUMN_NOTE_IMAGE,
                NoteContract.NoteEntry.COLUMN_NOTE_WIDGET,
                NoteContract.NoteEntry.COLUMN_NOTE_TIME
        };

        // задается условия для поиска ключ слов в БД
        String selection = setupSelectionString();
        ///String orderBy   = setupOrderByString();

        return new CursorLoader(this,
                NoteContract.NoteEntry.CONTENT_URI,
                projection,
                selection,
                null,
                mOrderByLoaderString);
    }

    /*
       Функция создает строчку для запроса поиска из БД. Если пользователь ввел чтото в строку
       поиска. Пока поиск происходит в двух колонках COLUMN_NOTE_BODY и COLUMN_NOTE_TITLE.
       В дальнейшем возможно будет добавлен поиск по друг колонкам.
     */
    private String setupSelectionString() {
        String selection = null;
        if (mSearchString != null) {
            selection = NoteContract.NoteEntry.COLUMN_NOTE_BODY + " LIKE '%"
                    + mSearchString + "%'"
                    + " OR " + NoteContract.NoteEntry.COLUMN_NOTE_TITLE + " LIKE '%"
                    + mSearchString + "%'";
        }
        return selection;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
//        mCursorAdapter.co

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    /*
     * Задается переменная по которой идет упорядочивание списка заметок. По дефолту установлена
     * переменная COLUMN_NOTE_TIME идет по уменьнению переменной.
     */
    private void setupOrderSpinner() {
        //TextView spinnerDropdownTextView = (TextView)findViewById(R.id.color_spin_dropdown_item);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.order_by_string_drop, R.layout.order_spinner_item);
        adapter.setDropDownViewResource(R.layout.order_spinner_dropdown_item);

        // Вызываем адаптер
        mOrderBySpinner.setAdapter(adapter);

        mOrderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int selectedItemPosition, long selectedId) {
                if (selectedItemPosition == 0) {
                    mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_TIME + " DESC";
                } else if (selectedItemPosition == 1) {
                    mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_COLOR + " DESC";
                } else if (selectedItemPosition == 2) {
                    mOrderByLoaderString = NoteContract.NoteEntry._ID + " DESC";
                } else if (selectedItemPosition == 3) {
                    mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_TITLE + " DESC";
                } else if (selectedItemPosition == 4) {
                    mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_BODY + " DESC";
                } else if (selectedItemPosition == 5) {
                    mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE + " DESC";
                }
                getLoaderManager().restartLoader(NOTE_LOADER, null, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
