package com.gabrielezanelli.schoolendar.database;

import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tasks")
@IgnoreExtraProperties

public class Task implements Serializable {

    @DatabaseField(columnName = "id", canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = "event_id", canBeNull = false,  foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar(40) references event(id) on delete cascade")
    private Event event;

    @DatabaseField(columnName = "text", canBeNull = false)
    private String text;

    @DatabaseField(columnName = "completed", canBeNull = false, dataType = DataType.BOOLEAN)
    private boolean completed;


    public Task() {
    }

    public Task(Event event, String text, boolean completed) {
        this.event = event;
        this.text = text;
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
