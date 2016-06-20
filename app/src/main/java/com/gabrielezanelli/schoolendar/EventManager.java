package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "schoolendar";
    private static final int DATABASE_VERSION = 9;
    private static EventManager eventManagerInstance = null;
    /** DAO: Data Access Object used to interact with SQLite in order to perform CRUD operations. */
    private static Dao<Event, Long> dao = null;


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

    public void createEvent(Event newEvent) throws SQLException {
        dao.create(newEvent);
    }

    public Event getEvent(long id) throws SQLException {
        return dao.queryForId(id);
    }

    public List<Event> getAllEvents() throws SQLException {
        return dao.queryForAll();
    }

    public List<Event> getEventsFiltered(Map<String,Object> eventFilter)  throws SQLException {
        return dao.queryForFieldValuesArgs(eventFilter);
    }

    public boolean areThereEventsInDate(Calendar date) {
        Map<String,Object> dateFilter = new HashMap<>();
        dateFilter.put("event_date",(date.getTimeInMillis()));

        try {
            List<Event> eventsFound = dao.queryForFieldValuesArgs(dateFilter);
            if(eventsFound.isEmpty())
                return false;
            else {
                Log.d("Event found in date: ",""+date.getTime());
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
