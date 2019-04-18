package com.batura.stas.notesaplication.AuthFunc;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.batura.stas.notesaplication.MainActivity;
import com.batura.stas.notesaplication.NoteCursorAdapter;
import com.batura.stas.notesaplication.Other.Folder;
import com.batura.stas.notesaplication.R;
import com.batura.stas.notesaplication.data.DbFirePresenter;
import com.batura.stas.notesaplication.data.NoteContract;
import com.batura.stas.notesaplication.data.NoteFirePresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AuthMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = AuthMainActivity.class.getName();

    private Button btnWriteToDb, btnPullFromDb, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private String mUsername;

    private NoteCursorAdapter mCursorAdapter;
    private Loader<Cursor> mNoteLoader;

    private List<NoteFirePresenter> notesFromFire;
    private List<Folder> foldersFromFire;

    private static final int NOTE_LOADER = 0;
    private static final int FOLDERS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
//        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mUsername = firebaseAuth.getCurrentUser().getUid();
                // database intance

                mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("bases").child(mUsername);
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AuthMainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnWriteToDb = (Button) findViewById(R.id.change_email_button);
        btnPullFromDb = (Button) findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.sign_out);

        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnWriteToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteLoader = getLoaderManager().initLoader(NOTE_LOADER, null, AuthMainActivity.this);
                getLoaderManager().initLoader(FOLDERS_LOADER,null,AuthMainActivity.this);
//                DbFirePresenter db = new DbFirePresenter(mUsername,"text");
//                mMessagesDatabaseReference.push().setValue(db);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthMainActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AuthMainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnPullFromDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesFromFire = new ArrayList<>();
                foldersFromFire = new ArrayList<>();

                mMessagesDatabaseReference.child("notes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.i(TAG,"note" + dataSnapshot.getValue( NoteFirePresenter.class ) );
                        GenericTypeIndicator<List<NoteFirePresenter>> t = new GenericTypeIndicator<List<NoteFirePresenter>>() {};
                        notesFromFire = dataSnapshot.getValue(t);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                mMessagesDatabaseReference.child("notes").addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Log.i(TAG,"note" + dataSnapshot.getValue( NoteFirePresenter.class ) );
//                        NoteFirePresenter note = dataSnapshot.getValue(NoteFirePresenter.class);
//                        notesFromFire.add(note);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {     }
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    }
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });
//
//                mMessagesDatabaseReference.child("folders").addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Log.i(TAG,"note" + dataSnapshot.getValue( Folder.class ) );
//                        Folder folder = dataSnapshot.getValue(Folder.class);
//                        foldersFromFire.add(folder);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                Log.i(TAG,"end pull" );
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.VISIBLE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.VISIBLE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AuthMainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(AuthMainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthMainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AuthMainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AuthMainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AuthMainActivity.this, SignupActivity.class));
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AuthMainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

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

//                // задается условия для поиска ключ слов в БД
//                String selection = setupSelectionString();
//                ///String orderBy   = setupOrderByString();
//                String[] selectionArgs = {String.valueOf(mCurrentFolder.getFolderId())};
//                String[] selectionArgs = {String.valueOf(0)};
                return new CursorLoader(this,
                        NoteContract.NoteEntry.CONTENT_URI_ALL,
                        projection,
                        null,
                        null,
                        null
                );
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case NOTE_LOADER:
//                mCursorAdapter.swapCursor(data);
                DbFirePresenter db = new DbFirePresenter(mUsername, data );
                writeNotesToFireBase(db.getNotesPresenter());
//                mMessagesDatabaseReference.push().setValue(db);
                break;
            case FOLDERS_LOADER:
                getFoldersFormDb(data);
                break;
        }
    }

    private void writeNotesToFireBase(List<NoteFirePresenter> list) {

        mMessagesDatabaseReference.child("notes").setValue(list);
    }

    private void getFoldersFormDb(Cursor cursor) {
        List<Folder> folders = new ArrayList<Folder>();
        if (cursor.moveToFirst()) {
            int idColomn = cursor.getColumnIndex(NoteContract.NoteEntry._ID);
            int folderNameColomn = cursor.getColumnIndex(NoteContract.NoteEntry.FOLDER_NAME);

            do  {
                int folderId = cursor.getInt(idColomn);
                String folderName = cursor.getString(folderNameColomn);
                Folder newFol = new Folder(folderId, folderName);
                folders.add(newFol);
            } while (cursor.moveToNext());

            Log.i(TAG, "getFoldersFormDb: ");
        }
        writeFoldersToFireBase(folders );
    }

    private void writeFoldersToFireBase(List<Folder> list) {

        mMessagesDatabaseReference.child("folders").setValue(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AuthMainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
