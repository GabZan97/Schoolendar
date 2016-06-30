package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gabrielezanelli.schoolendar.spaggiari.ClassevivaEvent;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "schoolendar";
    private static final int DATABASE_VERSION = 13;
    private static EventManager eventManagerInstance = null;
    /**
     * DAO: Data Access Object used to interact with SQLite in order to perform CRUD operations.
     */
    private static Dao<Event, String> dao = null;


    public static EventManager getInstance(Context context) {
        if (eventManagerInstance == null)
            eventManagerInstance = new EventManager(context);
        return eventManagerInstance;
    }

    private EventManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        try {
            dao = getDao(Event.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEvent(Event newEvent) throws SQLException {
        dao.create(newEvent);
    }

    public void addClassevivaEvent(ClassevivaEvent classevivaEvent) throws SQLException {
        Log.d("Spaggiari", "New ClassevivaEvent received, checking if exists...");

        Map values = new HashMap();
        values.put("title",classevivaEvent.getTitle());
        values.put("description",classevivaEvent.getDescription());
        values.put("type",Event.eventType.ClassevivaEvent);
        values.put("event_date",classevivaEvent.getDate());

        if((dao.queryForFieldValuesArgs(values)).isEmpty()) {
            Event newClassevivaEvent = new Event(
                    classevivaEvent.getTitle(),
                    classevivaEvent.getDescription(),
                    Event.eventType.ClassevivaEvent,
                    classevivaEvent.getDate());
            dao.create(newClassevivaEvent);
            Log.d("Spaggiari", "New ClassevivaEvent added");
        }
    }

    public void deleteEvent(String eventID) throws SQLException {
        dao.deleteById(eventID);
    }

    public Event getEvent(String eventID) throws SQLException {
        return dao.queryForId(eventID);
    }

    public List<Event> getAllEvents(boolean onlyFutureEvents) throws SQLException {
        QueryBuilder queryBuilder = dao.queryBuilder().orderBy("event_date", true);

        if (onlyFutureEvents) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

            queryBuilder.where().ge("event_date", cal.getTimeInMillis()).query();
        }
        return queryBuilder.query();
    }

    // Add date filter in millis
    public List<Event> getEventsFiltered(String filter, boolean onlyFutureEvents) throws SQLException {
        QueryBuilder queryBuilder = dao.queryBuilder().orderBy("event_date", true);
        queryBuilder.where().like("title", "%" + filter + "%")
                .or().like("subject", filter + "%")
                .or().like("type", filter + "%");

        if (onlyFutureEvents) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

            queryBuilder.where().ge("event_date", cal.getTimeInMillis()).query();
        }

        return queryBuilder.query();
    }

    public List<Event.eventType> getEventTypesInDate(Calendar date) {
        List<Event.eventType> eventTypes = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        try {
            events = dao.queryBuilder()
                    .where().eq("event_date",date.getTimeInMillis())
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!events.isEmpty()) {
            for (Event event : events) {
                Event.eventType type = Event.eventType.valueOf(event.getType());
                if(!eventTypes.contains(type))
                    eventTypes.add(type);
            }
        }
        return eventTypes;
    }

    public boolean areThereEventsInDate(Calendar date) {
        Map<String, Object> dateFilter = new HashMap<>();
        dateFilter.put("event_date", (date.getTimeInMillis()));

        try {
            List<Event> eventsFound = dao.queryForFieldValuesArgs(dateFilter);
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

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            /** Create the Table */
            TableUtils.createTable(connectionSource, Event.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            /** In order to upgrade the Table, drop the previous one and recreate it */
            TableUtils.dropTable(connectionSource, Event.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
