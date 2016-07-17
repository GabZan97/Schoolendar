package com.gabrielezanelli.schoolendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gabrielezanelli.schoolendar.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "schoolendar.db";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper databaseHelperInstance = null;
    /**
     * DAO: Data Access Object used to interact with SQLite in order to perform CRUD operations.
     */
    private static Dao<Event, String> eventsDao = null;
    private static Dao<Subject, String> subjectsDao = null;
    private static Dao<Task, String> tasksDao = null;

    public static DatabaseHelper getInstance(Context context) {
        if (databaseHelperInstance == null)
            databaseHelperInstance = new DatabaseHelper(context);
        return databaseHelperInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        try {
            eventsDao = getDao(Event.class);
            subjectsDao = getDao(Subject.class);
            tasksDao = getDao(Task.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Event Methods [START]
    public void addEvent(Event event) {
        try {
            eventsDao.create(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEventById(String eventId)  {
        try {
            eventsDao.deleteById(eventId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Event getEventById(String eventId)  {
        try {
            return eventsDao.queryForId(eventId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> getAllEvents(boolean onlyFutureEvents)  {
        QueryBuilder queryBuilder = eventsDao.queryBuilder().orderBy("event_date", true);
        try {
        if (onlyFutureEvents) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);


                queryBuilder.where().ge("event_date", cal.getTimeInMillis()).query();

        }
        return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // Add date filter in millis
    public List<Event> getEventsByQueryText(String query, boolean onlyFutureEvents) {
        try {
        QueryBuilder queryBuilder = eventsDao.queryBuilder().orderBy("event_date", true);

            queryBuilder.where().like("title", "%" + query + "%")
                    .or().like("subject_id", query + "%")
                    .or().like("type", query + "%");

            if (onlyFutureEvents) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);

                queryBuilder.where().ge("event_date", cal.getTimeInMillis()).query();
            }


        return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void deleteEventsBySubjectId(String subjectId) {
        try {
            DeleteBuilder deleteBuilder = eventsDao.deleteBuilder();
            deleteBuilder.where().eq("subject_id", subjectId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        try {
            eventsDao.update(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Event.EventType> getEventTypesInDate(Calendar date) {
        List<Event.EventType> eventTypes = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        try {
            events = eventsDao.queryBuilder()
                    .where().eq("event_date", date.getTimeInMillis())
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!events.isEmpty()) {
            for (Event event : events) {
                Event.EventType type = Event.EventType.valueOf(event.getType());
                if (!eventTypes.contains(type))
                    eventTypes.add(type);
            }
        }
        return eventTypes;
    }

    public boolean areThereEventsInDate(Calendar date) {
        Map<String, Object> dateFilter = new HashMap<>();
        dateFilter.put("event_date", (date.getTimeInMillis()));

        try {
            List<Event> eventsFound = eventsDao.queryForFieldValuesArgs(dateFilter);
            if (eventsFound.isEmpty())
                return false;
            else {
                Log.d("Event found in date: ", "" + date.getTime());
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    // Event Methods [END]


    // Subjects Methods [START]
    public void addSubject(Subject subject) {
        try {
            subjectsDao.create(subject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Subject> getAllSubjects()  {
        try {
            List<Subject> subjects = subjectsDao.queryForAll();
            for(int i=0;i<subjects.size();i++){
                if(subjects.get(i).equals(Subject.NO_SUBJECT))
                    subjects.remove(i);
            }
            return subjects;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void deleteSubjectById(String subjectId) {
        try {
            subjectsDao.deleteById(subjectId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSubject(Subject subject){
        try {
            subjectsDao.update(subject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Subjects Methods [END]


    // Tasks Methods [START]
    public List<Task> getTasksByEventId(String eventId)  {
        try {
            return tasksDao.queryForEq("event_id", eventId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addTask(Task task) {
        try {
            tasksDao.create(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task){
        try {
            tasksDao.update(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTasksByEventId(String eventId) {
        try {
            DeleteBuilder deleteBuilder = tasksDao.deleteBuilder();
            deleteBuilder.where().eq("event_id",eventId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTaskById(String taskId) {
        try {
            tasksDao.deleteById(taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Task Methods [END]

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            /** Create the Table */
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, Subject.class);
            TableUtils.createTable(connectionSource, Task.class);

            subjectsDao.create(Subject.NO_SUBJECT);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            /** In order to upgrade the Table, drop the previous one and recreate it */
            TableUtils.dropTable(connectionSource, Event.class, false);
            TableUtils.dropTable(connectionSource, Subject.class, false);
            TableUtils.dropTable(connectionSource, Task.class, false);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
