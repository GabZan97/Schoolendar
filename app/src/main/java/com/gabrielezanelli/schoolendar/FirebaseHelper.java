package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.database.Subject;
import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseHelper {
    private static com.google.firebase.auth.FirebaseUser currentUser;
    private static String previousUserID;
    public static Bitmap image;
    private static DatabaseReference currentUserRef;
    private static String defaultUsername;
    private static SpaggiariCredentials spaggiariCredentials;
    private static String TAG_FIREBASE_MANAGER = "Firebase Helper";

    public static void initialize(Context context) {
        defaultUsername = context.getString(R.string.pref_default_username);
    }

    public static void updateUser(com.google.firebase.auth.FirebaseUser currentUser) {
        FirebaseHelper.currentUser = currentUser;
        FirebaseHelper.currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        if (currentUser.isAnonymous()) {
            FirebaseHelper.image = null;
        } else {
            new LoadImageFromFirebaseUser().execute(currentUser.getPhotoUrl());
        }
        // TODO: Download new events from firebase

        if (previousUserID != null && !currentUser.isAnonymous())
            migrateData();
    }

    public static void clearUser() {
        FirebaseHelper.currentUser = null;
        FirebaseHelper.image = null;
        FirebaseHelper.currentUserRef = null;
        FirebaseHelper.spaggiariCredentials = null;
    }

    private static void migrateData() {
        Log.d(TAG_FIREBASE_MANAGER, "Migrating " + previousUserID);
        FirebaseDatabase.getInstance().getReference().child("users").child(previousUserID).child("events")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG_FIREBASE_MANAGER, "Data change");
                        for (DataSnapshot dataEvent : dataSnapshot.getChildren()) {
                            addEvent(dataEvent.getValue(Event.class));
                            Log.d(TAG_FIREBASE_MANAGER, "Adding Event");
                        }
                        FirebaseDatabase.getInstance().getReference().child("users").child(previousUserID).removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void deleteUserAndSaveData() {
        previousUserID = currentUser.getUid();
        /**
         currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override public void onComplete(@NonNull Task<Void> task) {
        Log.d("FirebaseHelper", "Anonymous user deleted");
        }
        });
         */
    }

    // Get Methods [START]
    public static boolean isAnonymous() {
        return currentUser.isAnonymous();
    }

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static String getUsername() {
        return currentUser.getDisplayName();
    }

    public static String getUserID() {
        return currentUser.getUid();
    }

    public static String getEmail() {
        return currentUser.getEmail();
    }

    public static Bitmap getImage() {
        return image;
    }

    public static DatabaseReference getSubjectsRef() {
        return currentUserRef.child("subjects");
    }

    private static DatabaseReference getEventsRef() {
        return currentUserRef.child("events");
    }

    public static DatabaseReference getTasksRef() {
        return currentUserRef.child("tasks");
    }
    // Get Methods [END]

    // Set Methods [START]
    public static void setUsername(final String username) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(adjustUsername(username))
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG_FIREBASE_MANAGER, "Username update succeeded: " + username);
                } else
                    Log.d(TAG_FIREBASE_MANAGER, "Username update failed" + task.getException().toString());
            }
        });
    }

    public static void setPhotoUrl(final Uri photoUrl) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUrl)
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new LoadImageFromFirebaseUser().execute(photoUrl);
                    Log.d(TAG_FIREBASE_MANAGER, "Photo update succeeded");
                }
            }
        });
    }

    public static void setSpaggiariCredentials(SpaggiariCredentials spaggiariCredentials) {
        FirebaseHelper.spaggiariCredentials = spaggiariCredentials;
    }
    // Set Methods [END]


    // Events Methods [START]
    public static void addEvent(Event newEvent) {
        getEventsRef().child(newEvent.getId()).setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "New event added or updated");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void deleteEvent(String eventID) {
        getEventsRef().child(eventID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "Event deleted");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void updateEvent(Event event) {
        addEvent(event);
    }
    // Events Methods [END]


    // Subjects Methods [START]
    public static void addSubject(Subject subject) {
        getSubjectsRef().child(subject.getId()).setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "New subject added or updated");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void deleteSubject(String subjectId) {
        getSubjectsRef().child(subjectId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "Subject deleted");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void updateSubject(Subject subject) {
        addSubject(subject);
    }
    // Subjects Methods [END]


    // Tasks Methods [START]
    public static void addTask(com.gabrielezanelli.schoolendar.database.Task task) {
        getTasksRef().child(task.getId()).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "New task added or updated");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void deleteTask(String taskId) {
        getTasksRef().child(taskId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d(TAG_FIREBASE_MANAGER, "Task deleted");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void updateTask(com.gabrielezanelli.schoolendar.database.Task task) {
        addTask(task);
    }
    // Tasks Methods [END]



    private static String adjustUsername(String username) {
        if (username == null)
            return defaultUsername;
        if (username.equals(""))
            return defaultUsername;
        return username;
    }
}

class LoadImageFromFirebaseUser extends AsyncTask<Uri, Void, Void> {
    @Override
    protected Void doInBackground(Uri... params) {
        try {
            URL url = new URL("" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            FirebaseHelper.image = myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

