package com.gabrielezanelli.schoolendar.database;

import com.gabrielezanelli.schoolendar.spaggiari.ClassevivaEvent;
import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "events")
@IgnoreExtraProperties
public class Event implements Serializable {

    @DatabaseField(columnName = "id", canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = "title", canBeNull = false)
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "type", canBeNull = false)
    private String type;

    @DatabaseField(columnName = "subject_id", foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar(40) references subjects(id) on delete cascade")
    private Subject subject;

    @DatabaseField(columnName = "event_date", canBeNull = false, dataType = DataType.LONG)
    private long date;

    @DatabaseField(columnName = "notification_date", dataType = DataType.LONG)
    private long notificationDate;

    public enum EventType {Homework, Test, Project, Communication, Note, ClassevivaEvent}

    public Event() {
    }

    public Event(String title, String description, EventType type, long date) {
        this.title = title;
        this.description = description;
        this.type = type.toString();
        this.date = date;
        this.subject = Subject.NO_SUBJECT;
        this.notificationDate = 0;
    }

    public Event(ClassevivaEvent classevivaEvent) {
        this.id = classevivaEvent.getId();
        this.title = classevivaEvent.getTitle();
        this.description = classevivaEvent.getDescription();
        this.type = EventType.ClassevivaEvent.toString();
        this.date = classevivaEvent.getDate();
        this.subject = Subject.NO_SUBJECT;
        this.notificationDate = 0;
    }

    // Set Methods [START]
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setNotificationDate(long notificationDate) {
        this.notificationDate = notificationDate;
    }
    // Set Methods [END]


    // Get Methods [START]
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    public Subject getSubject() {
        return subject;
    }

    public long getNotificationDate() {
        return notificationDate;
    }

    public boolean hasSubject() {
        return subject != null;
    }

    public boolean hasNotification() {
        return notificationDate != 0;
    }
    // Get Methods [END]


    public String chainTypeSubject() {
        if (hasSubject())
            return subject.getName() + "'s " + type;
        else
            return type;
    }
}


