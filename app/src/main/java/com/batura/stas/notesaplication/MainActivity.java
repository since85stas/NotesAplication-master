/*
 * MainActivity.java
 *
 * version 1.1
 *
 * Автор Батура Стас 10.07.2018
 */

package com.batura.stas.notesaplication;

import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.batura.stas.notesaplication.ImageFuncs.ImageStorage;
import com.batura.stas.notesaplication.Other.AboutActivity;
import com.batura.stas.notesaplication.Other.CircleTransform;
import com.batura.stas.notesaplication.Other.Folder;
import com.batura.stas.notesaplication.Other.Password;
import com.batura.stas.notesaplication.Other.PrivacyPolicyActivity;
import com.batura.stas.notesaplication.data.NoteContract;
import com.batura.stas.notesaplication.data.NoteDbHelper;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
        , SimpleDialog.OnDialogResultListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTE_LOADER = 0;
    private static final int FOLDERS_LOADER = 1;
    private static final int PASS_OK = 11;

    //intent Const
    public final static String PASS_INTENT_TYPE = MainActivity.class.getPackage() + ".passIntentType";
    public final static String TYPE_CHECK = "chek";
    public final static String TYPE_SET = "set";
    public final static String TYPE_DEL = "del";

    //preferences keys
    private static final String PREF_NAME = "NotesPref";
    private static final String HAS_PASS = "hasPass";
    public static final String PASSWORD = "password";
    public static final String SORTED_BY = "sorted";
    public static final String ORDER_SPINNER_MODE = "Mode"; // положение переключателя
    public static final String NUMBER_OF_OPENS = "Opens"; // количество запусков приложения
    public static final String IS_RATED = "Rated";       // прошли ли по ссылке в маркет
    public static final int NUMBER_OPEN_NUM = 5;
    public int mNumberOfOpens;
    public int mRated;
    public int mSortBySpinner = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    //dialogs tags
    private static final String CHOICE_DIALOG_FOLDER = "folder_choise";
    private static final String INPUT_DIALOG_FOLDER = "input_folder";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    private NoteDbHelper mDbHelper;
    private NoteCursorAdapter mCursorAdapter;
    private Spinner mOrderBySpinner;
    private SearchView mSearchView;
    private String mSearchString = null; // search request
    // по какому столбцу упорядочивание списка
    private String mOrderByLoaderString = NoteContract.NoteEntry.COLUMN_NOTE_TIME;
    private Disposable disposable;

    private SharedPreferences mSettings;
    private boolean mHasPass;
    private String mPass = "";
    private boolean mPasswordCorrect;

    Folder mMainFolder = new Folder(0, "main");
    Folder mCurrentFolder;

    private Loader<Cursor> mNoteLoader;
    private Loader<Cursor> mFolderLoader;
    private ArrayList<Folder> mFolders;// = new ArrayList<Folder>();


    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }



    @Override
    protected void onPause() {
        super.onPause();
        getSupportLoaderManager().destroyLoader(NOTE_LOADER);
        saveSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CursorAdapter.r;
        loadSettings();
        //onCreate();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        mNumberOfOpens++;  // после закрытия кол запусков увел на 1
        saveSettings();
        super.onStop();
    }



    /**
     * Perform the deletion of the note in the database.
     *
     * @param uri
     */
    private void deleteNote(Uri uri) {
        // Only perform the delete if this is an existing pet.
        if (uri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(uri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        //finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // root directory always in
        //mFolders.add(mMainFolder);

        //mHasPass = true;
        mSettings = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadSettings();

        mCurrentFolder = mMainFolder;

        if (mNumberOfOpens % NUMBER_OPEN_NUM == 0 && mRated != 1) {
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            myDialogFragment.show(transaction, "Dialog");
        }

        if (mHasPass && !mPasswordCorrect) {
            Intent intent = new Intent(MainActivity.this, Password.class);
            //intent.putExtra(Password.PASS_OK_INTENT);
            intent.putExtra(PASS_INTENT_TYPE, TYPE_CHECK);
            startActivityForResult(intent, PASS_OK);
        }

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        setContentView(R.layout.activity_main);

        setupNavigationDraver();

        //Locale locale = new Locale("en"); // задаем локаль, пока принудительно
        //Locale.setDefault(locale);
        // определяем спинер для упорядочивания заметок
        mOrderBySpinner = (Spinner) findViewById(R.id.orderBySpinner);
        setupOrderSpinner();

        disposable = RxView.clicks(findViewById(R.id.fab))
                .compose(rxPermissions.ensureEach(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(new Consumer<Permission>() {
                               public void accept(Permission permission) {
                                   Log.i(TAG, "Permission result " + permission);
                                   if (permission.granted) {
                                       Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                                       intent.putExtra(EditorActivity.NOTE_FOLD_ID_INTENT,mCurrentFolder.getFolderId());
                                       startActivity(intent);
                                       Log.i(TAG, "Permission for storage granted");

                                   } else if (permission.shouldShowRequestPermissionRationale) {
                                       // Denied permission without ask never again
                                       Toast.makeText(MainActivity.this,
                                               "Denied permission without ask never again",
                                               Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                                       intent.putExtra(EditorActivity.NOTE_FOLD_ID_INTENT,mCurrentFolder.getFolderId());
                                       startActivity(intent);
                                   } else {
                                       // Denied permission with ask never again
                                       // Need to go to the settings
                                       Toast.makeText(MainActivity.this,
                                               "Permission denied, can't load images",
                                               Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                                       intent.putExtra(EditorActivity.NOTE_FOLD_ID_INTENT,mCurrentFolder.getFolderId());
                                       startActivity(intent);
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) {
                                Log.e(TAG, "onError", t);
                            }
                        },
                        new Action() {
                            @Override
                            public void run() {
                                Log.i(TAG, "OnComplete");
                            }
                        });

        mFolderLoader = getLoaderManager().initLoader(FOLDERS_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // список директорий
//        mFolders = new ArrayList<Folder>();
//        mFolders.add(mMainFolder);

        /* задаем listView для списка заметок
         * проверяем изменолись ли данные
         * при нажатии на заметку вызывается редактирование заметки класс EditorAcrtivity
         */
        ListView noteListView = (ListView) findViewById(R.id.list);
        registerForContextMenu(noteListView);
        mCursorAdapter = new NoteCursorAdapter(this, null);
        noteListView.setAdapter(mCursorAdapter);
        mCursorAdapter.notifyDataSetChanged();
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                intent.putExtra(EditorActivity.NOTE_FOLD_ID_INTENT,mCurrentFolder.getFolderId());
                startActivity(intent);
            }
        });
//
        // вызываем намерение на поиск выражения в тексте заметки
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchString = intent.getStringExtra(SearchManager.QUERY);
            //System.out.println(query);
        }

        mNoteLoader = getLoaderManager().initLoader(NOTE_LOADER, null, this);

    }


    private void setupNavigationDraver() {

        Handler handler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // get menu
        //Menu menu = navigationView.getMenu();

        //NavMenuClass navMenuObject = new NavMenuClass(menu,items);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.actionBarColor));

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        String[] activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Stanislav Batura");
        txtWebsite.setText("stanislav.batura85@gmail.com");

        //imgNavHeaderBg.setImageResource(R.drawable.before_cookie);
        imgNavHeaderBg.setImageResource(R.drawable.drawer_back);

        Glide.with(this).load(R.drawable.cat_portrait_cute_animal)
                .bitmapTransform(new CircleTransform(this))
                .into(imgProfile);

        // showing dot next to notifications label
        //navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.nav_main:
                        toolbar.setTitle(R.string.app_name);
                        mCurrentFolder = mMainFolder;
                        getLoaderManager().restartLoader(NOTE_LOADER,null,MainActivity.this);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_set_pass:
                        navItemIndex = 3;
                        if (!mHasPass) {
                            Intent intent = new Intent(MainActivity.this, Password.class);
                            intent.putExtra(PASS_INTENT_TYPE, TYPE_SET);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, Password.class);
                            intent.putExtra(PASS_INTENT_TYPE, TYPE_DEL);
                            startActivity(intent);
                        }
                        break;
                    case R.id.nav_rate:
                        mRated = 1; //если прошли по ссылке то болье диалог не вылезает
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(getString(R.string.appMarketLink)));
                        startActivity(intent);
                        navItemIndex = 4;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        return true;
                    default:
                        navItemIndex = 0;
                }

                for (int i = 0; i < mFolders.size() ; i++) {
                    if(menuItem.getItemId() == mFolders.get(i).getFolderId()) {
                        toolbar.setTitle(mFolders.get(i).getFolderName());
                        mCurrentFolder = mFolders.get(i);
                        getLoaderManager().restartLoader(NOTE_LOADER,null,MainActivity.this);
                        drawer.closeDrawers();
                    }
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
//                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        super.onBackPressed();
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
        switch (item.getItemId()) {
            case (R.id.action_add_folder):
                SimpleListDialog.build()
                        .title("Selet one")
                        .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                        .items(getBaseContext(), R.array.folder_choise)
                        .show(this, CHOICE_DIALOG_FOLDER);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_contex_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Get readable database
        mDbHelper = new NoteDbHelper(getBaseContext());
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.contex_edit:
                Toast.makeText(MainActivity.this,
                        "Edit",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, info.id);
                intent.setData(currentPetUri);
                startActivity(intent);
                //editElement(info.position);
                return true;
            case R.id.contex_delete:
                Toast.makeText(MainActivity.this,
                        "Delete",
                        Toast.LENGTH_SHORT).show();
                Uri currentDelPetUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, info.id);
// For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                String[] projection = new String[]{
                        NoteContract.NoteEntry.IMAGE_NAME_01,
                        NoteContract.NoteEntry._ID
                };
                String selection = NoteContract.NoteEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(currentDelPetUri))};
                cursor = database.query(
                        NoteContract.NoteEntry.IMAGE_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                if (cursor.moveToFirst()) {
                    int image01colomnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.IMAGE_NAME_01);
                    String imagesNames = cursor.getString(image01colomnIndex);
                    String[] imageNamesMassive = imagesNames.split(" ");
                    if (imageNamesMassive[0] != "") {
                        for (String anImageNamesMassive : imageNamesMassive) {
                            ImageStorage.deleteFromSd(anImageNamesMassive);
                        }
                    }
                    //deleteElement(info.position);
                    Log.i(TAG, "onContextItemSelected: " + imagesNames);
                }
                cursor.close();

                // удаляем заметку
                deleteNote(currentDelPetUri);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }


    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        switch (dialogTag) {
            case CHOICE_DIALOG_FOLDER: /** {@link MainActivity#showDirectChoice}, {@link MainActivity#showMultiChoice} **/
                ArrayList<String> labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);
                String result = labels.get(0);
                if (result != null) {
                    if (result.equals(getResources().getString(R.string.create_folder))) {
                        Log.i(TAG, "onResult: create");
                        SimpleInputDialog.build()
                                .title("Folder name")
                                .show(this, INPUT_DIALOG_FOLDER);
                    } else if (result == getResources().getString(R.string.delete_folder)) {
                        Log.i(TAG, "onResult: dele");
                    } else if (result == getResources().getString(R.string.cancel)) {
                        Log.i(TAG, "onResult: can");
                    } else {
                        Log.i(TAG, "onResult: else");
                    }
                }
                Toast.makeText(this, android.text.TextUtils.join(", ", labels), Toast.LENGTH_SHORT).show();
                return true;
            case INPUT_DIALOG_FOLDER:
                String name = extras.getString(SimpleInputDialog.TEXT);
                saveFolderInDb(name);
                return true;
        }
        return false;
    }

    private void saveFolderInDb(String folderName) {
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        if (folderName.isEmpty() || folderName.equals("")) {
            Toast.makeText(this, "Empty folder name", Toast.LENGTH_SHORT).show();
            throw (new IllegalArgumentException("wrong empty folder"));
        }
        values.put(NoteContract.NoteEntry.FOLDER_NAME, folderName);

        Uri folderUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI_FOLDERS, values);
        if (folderUri != null) {
            Toast.makeText(this, "Folder created", Toast.LENGTH_SHORT).show();
        }

        getLoaderManager().destroyLoader(FOLDERS_LOADER);
        int id = (int)ContentUris.parseId(folderUri);
        Menu menu = navigationView.getMenu();
        menu.add(R.id.notesGroupNew,id,0,folderName);
        Log.i(TAG, "saveFolderInDb: " + folderUri.toString());
        mFolders.add(new Folder(id,folderName));

    }

    private void getFoldersFormDb(Cursor cursor) {
        mFolders = new ArrayList<Folder>();
        if (cursor.moveToFirst()) {
            int idColomn = cursor.getColumnIndex(NoteContract.NoteEntry._ID);
            int folderNameColomn = cursor.getColumnIndex(NoteContract.NoteEntry.FOLDER_NAME);

            do  {
                int folderId = cursor.getInt(idColomn);
                String folderName = cursor.getString(folderNameColomn);
                Folder newFol = new Folder(folderId, folderName);
                mFolders.add(newFol);
            } while (cursor.moveToNext());

            Log.i(TAG, "getFoldersFormDb: ");
        }
        Menu menu = navigationView.getMenu();

        for (int i = 0; i < mFolders.size(); i++) {
            menu.add(R.id.notesGroupNew, mFolders.get(i).getFolderId(), 0, mFolders.get(i).getFolderName())
                .setIcon(R.drawable.baseline_create_new_folder_white_24);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // задаем проекцию по которой будет происходить считывние из БД
        switch (id) {
            case (NOTE_LOADER):
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
                        NoteContract.NoteEntry.COLUMN_NOTE_TIME,
                        NoteContract.NoteEntry.COLUMN_NOTE_FOLDER
                };

                // задается условия для поиска ключ слов в БД
                String selection = setupSelectionString();
                ///String orderBy   = setupOrderByString();
                String[] selectionArgs = {String.valueOf(mCurrentFolder.getFolderId())};

                return new CursorLoader(this,
                        NoteContract.NoteEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        mOrderByLoaderString);
            case(FOLDERS_LOADER):
                String[] projectionLoader = new String[]{
                        NoteContract.NoteEntry._ID,
                        NoteContract.NoteEntry.FOLDER_NAME,
                 };

                return new CursorLoader(this,
                        NoteContract.NoteEntry.CONTENT_URI_FOLDERS,
                        projectionLoader,
                        null,
                        null,
                        null);
        }

        return null;
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
        switch (loader.getId()) {
            case NOTE_LOADER:
                mCursorAdapter.swapCursor(data);
                break;
            case FOLDERS_LOADER:
                getFoldersFormDb(data);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PASS_OK) {
            if (resultCode == RESULT_OK) {
                mPasswordCorrect = data.getBooleanExtra(Password.PASS_OK_INTENT, false);
            } else {
                finish();
            }
        }
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(HAS_PASS, mHasPass);
        editor.putString(PASSWORD, mPass);
        editor.putString(SORTED_BY, mOrderByLoaderString);
        //editor.putInt(ORDER_SPINNER_MODE, mSortBySpinner);
        editor.putInt(NUMBER_OF_OPENS, mNumberOfOpens);
        editor.putInt(IS_RATED, mRated);
        editor.apply();
    }

    private void loadSettings() {
        if (mSettings.contains(HAS_PASS)) {
            mHasPass = mSettings.getBoolean(HAS_PASS, false);
        }
        if (mSettings.contains(PASSWORD) && mHasPass) {
            mPass = mSettings.getString(PASSWORD, "1");
        }
        if (mSettings.contains(SORTED_BY)) {
            mOrderByLoaderString = mSettings.getString(SORTED_BY, NoteContract.NoteEntry.COLUMN_NOTE_TIME);
        }
        //mMachSpinnerMode = mSettings.getInt(VELOCITY_SPINNER_MODE,MACH_MODE);
        mNumberOfOpens = mSettings.getInt(NUMBER_OF_OPENS, 1);
        mRated = mSettings.getInt(IS_RATED, 0);
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
        mOrderBySpinner.setSelection(getSpinnerPosition());
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

    private int getSpinnerPosition() {
        int spinnerPos = 0;
        if (mOrderByLoaderString == NoteContract.NoteEntry.COLUMN_NOTE_TIME) {
            spinnerPos = 0;
        } else if (mOrderByLoaderString == NoteContract.NoteEntry.COLUMN_NOTE_COLOR) {
            spinnerPos = 1;
        } else if (mOrderByLoaderString == NoteContract.NoteEntry._ID) {
            spinnerPos = 2;
        } else if (mOrderByLoaderString == NoteContract.NoteEntry.COLUMN_NOTE_TITLE) {
            spinnerPos = 3;
        } else if (mOrderByLoaderString == NoteContract.NoteEntry.COLUMN_NOTE_BODY) {
            spinnerPos = 4;
        } else if (mOrderByLoaderString == NoteContract.NoteEntry.COLUMN_NOTE_FAVOURITE) {
            spinnerPos = 5;
        }
        return spinnerPos;
    }

    public void cancelClicked() {
    }

    public void okClicked() {
        mRated = 1; //если прошли по ссылке то болье диалог не вылезает
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.appMarketLink)));
        startActivity(intent);
    }
}
