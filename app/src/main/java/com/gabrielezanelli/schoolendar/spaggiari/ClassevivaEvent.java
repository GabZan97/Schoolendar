package com.gabrielezanelli.schoolendar.spaggiari;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClassevivaEvent {

    // These dumb names are for Gson that needs the same Json attributes' names given by Spaggiari
    private String id;
    private String autore_desc;
    private String data_inserimento;
    private String end;
    private String nota_2;
    private String start;
    private String tipo;
    private String title;


    // Get Methods [START]
    public String getId() {
        return id;
    }

    public String getAuthor() {
        return autore_desc;
    }

    public String getInsertDate() {
        return data_inserimento;
    }

    public String getEnd() {
        return end;
    }

    public String getDescription() {
        return nota_2;
    }

    public long getDate() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start));
            cal.set(Calendar.HOUR_OF_DAY,0);
            cal.set(Calendar.MINUTE,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getType() {
        return tipo;
    }

    public String getTitle() {
        return title;
    }
    // Get Methods [END]
}
