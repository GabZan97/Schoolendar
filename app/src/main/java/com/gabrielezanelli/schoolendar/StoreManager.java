package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.util.Log;

import com.gabrielezanelli.schoolendar.database.DatabaseHelper;
import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.database.Subject;
import com.gabrielezanelli.schoolendar.database.Task;
import com.gabrielezanelli.schoolendar.spaggiari.ClassevivaEvent;

import org.apache.commons.lang.WordUtils;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class StoreManager {
    private static StoreManager storeManager = new StoreManager();
    private static DatabaseHelper databaseHelper = null;

    private StoreManager() {
    }

    public static StoreManager getInstance() {
        return storeManager;
    }


    public static void initialize(Context context) {
        Log.d("Initialize","All in the vita");
        databaseHelper = DatabaseHelper.getInstance(context);
        FirebaseHelper.initialize(context);
        databaseHelper.getWritableDatabase();
    }

    // Add Methods [START]
    public void addSubject(Subject subject) {
        subject.setId(generateUniqueID());
        subject.setName(adjustSubject(subject.getName()));

        databaseHelper.addSubject(subject);
        if(FirebaseHelper.isLogged())
            FirebaseHelper.addSubject(subject);
    }

    public void addEvent(Event event) {
        event.setId(generateUniqueID());

        databaseHelper.addEvent(event);
        if(FirebaseHelper.isLogged())
            FirebaseHelper.addEvent(event);
    }

    public void addClassevivaEvent(ClassevivaEvent classevivaEvent) {
        Event event = new Event(classevivaEvent);
        databaseHelper.addEvent(event);
        if(FirebaseHelper.isLogged())
            FirebaseHelper.addEvent(event);
    }

    public void addTask(Task task) {
        if(task.getText().equals(""))
            return;

        task.setId(generateUniqueID());

        databaseHelper.addTask(task);
        if(FirebaseHelper.isLogged())
            FirebaseHelper.addTask(task);
    }
    // Add Methods [END]


    // Delete Methods [START]
    public void deleteSubject(Subject subject) {
        databaseHelper.deleteSubjectById(subject.getId());
        databaseHelper.deleteEventsBySubjectId(subject.getId());

        if(FirebaseHelper.isLogged())
            FirebaseHelper.deleteSubject(subject.getId());

        // TODO: delete events on Firebase
    }

    public void deleteEvent(Event event)  {
        databaseHelper.deleteEventById(event.getId());
        databaseHelper.deleteTasksByEventId(event.getId());

        if(FirebaseHelper.isLogged())
            FirebaseHelper.deleteEvent(event.getId());

        // TODO: delete tasks on Firebase
    }

    public void deleteTask(Task task) {
        databaseHelper.deleteTaskById(task.getId());

        if(FirebaseHelper.isLogged())
        FirebaseHelper.deleteTask(task.getId());
    }
    // Delete Methods [END]


    // Update Methods [START]
    public void updateSubject(Subject subject) {
        databaseHelper.updateSubject(subject);

        if(FirebaseHelper.isLogged())
            FirebaseHelper.updateSubject(subject);
    }

    public void updateEvent(Event event) {
        databaseHelper.updateEvent(event);

        if(FirebaseHelper.isLogged())
            FirebaseHelper.updateEvent(event);
    }

    public void updateTask(Task task) {
        databaseHelper.updateTask(task);

        if(FirebaseHelper.isLogged())
            FirebaseHelper.updateTask(task);
    }
    // Update Methods [END]


    // Get Methods [START]
    public Event getEvent(String eventId) {
        return databaseHelper.getEventById(eventId);
    }

    public List<Subject> getSubjects() {
        return databaseHelper.getAllSubjects();
    }

    public List<Task> getEventTasks(Event event)  {
        return databaseHelper.getTasksByEventId(event.getId());
    }

    public List<Event> getEvents(boolean onlyFutureEvents) {
        return databaseHelper.getAllEvents(onlyFutureEvents);
    }

    public List<Event> getEventsByQueryText (String query, boolean onlyFutureEvents) {
        return databaseHelper.getEventsByQueryText(query, onlyFutureEvents);
    }
    // Get Methods [END]

    public boolean areThereEventsInDate(Calendar date) {
        return databaseHelper.areThereEventsInDate(date);
    }

    private String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    private static String adjustSubject(String subject) {
        return WordUtils.capitalize(subject.trim());
    }
}
