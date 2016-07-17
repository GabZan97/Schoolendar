package com.gabrielezanelli.schoolendar.database;

import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "subjects")
@IgnoreExtraProperties

public class Subject implements Serializable {

    @DatabaseField(columnName = "id", canBeNull = false, id = true)
    private String id;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    public Subject(String id, String name){
        this.id=id;
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if(!this.id.equals(((Subject)object).id))
            return false;
        if(!this.name.equals(((Subject)object).name))
            return false;
        return true;
    }

    public static final Subject NO_SUBJECT = new Subject("NO_SUBJECT","No Subject");
}
