package com.gabrielezanelli.schoolendar;

import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "event")
@IgnoreExtraProperties
public class Event implements Serializable {

    // TODO: EVERY TIME THIS CLASS CHANGES, RUN THE ORMLITE CONFIG MANAGER
    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false, dataType = DataType.LONG, throwIfNull = true)
    private long id;

    @DatabaseField(columnName = "title", canBeNull = false)
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "type", canBeNull = false)
    private String type;

    @DatabaseField(columnName = "subject")
    private String subject;

    @DatabaseField(columnName = "has_subject", canBeNull = false, dataType = DataType.BOOLEAN)
    private boolean hasSubject;

    @DatabaseField(columnName = "event_date", canBeNull = false, dataType = DataType.LONG )
    private long date;

    @DatabaseField(columnName = "notification_date", dataType = DataType.LONG)
    private long notificationDate;

    @DatabaseField(columnName = "has_notification", canBeNull = false, dataType = DataType.BOOLEAN)
    private boolean hasNotification;


    public enum eventType {Homework, Test, Project, Communication, Note, ClassevivaEvent}

    static List<String> subjectsNames = new ArrayList<>();

    public Event() {
    }

    public Event(String title, String description, eventType type, long date, String subject, long notificationDate) {
        this.title = title;
        this.description = description;
        this.type = type.toString();
        this.subject = subject;
        this.hasSubject = true;
        this.date = date;
        this.notificationDate = notificationDate;
        this.hasNotification = true;
    }

    public Event(String title, String description, eventType type, long date, String subject) {
        this.title = title;
        this.description = description;
        this.type = type.toString();
        this.subject = subject;
        this.hasSubject = true;
        this.date = date;
        this.hasNotification = false;
    }

    public Event(String title, String description, eventType type, long date, long notificationDate) {
        this.title = title;
        this.description = description;
        this.type = type.toString();
        this.hasSubject = false;
        this.date = date;
        this.notificationDate = notificationDate;
        this.hasNotification = true;
    }

    public Event(String title, String description, eventType type, long date) {
        this.title = title;
        this.description = description;
        this.type = type.toString();
        this.hasSubject = false;
        this.date = date;
        this.hasNotification = false;
    }

    public String getTypePlusSubject() {
        if (hasSubject)
            return subject + "'s " + type;
        else
            return type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean hasSubject() {
        return hasSubject;
    }

    public void setHasSubject(boolean hasSubject) {
        this.hasSubject = hasSubject;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(long notificationDate) {
        this.notificationDate = notificationDate;
    }

    public boolean hasNotification() {
        return hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    public static List<String> getSubjectsNames() {
        return subjectsNames;
    }

    public static void setSubjectsNames(List<String> subjectsNames) {
        Event.subjectsNames = subjectsNames;
    }
}


