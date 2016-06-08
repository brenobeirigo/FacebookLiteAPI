package model;

import java.util.Calendar;

/**
 *
 * @author BBEIRIGO
 */
public class Album {
    private int id;
    private String name;
    private Calendar date;
    
    public Album(int id, String name, Calendar date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
    
    public Album(String name) {
        this.name = name;
    }

    public Album(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

}
