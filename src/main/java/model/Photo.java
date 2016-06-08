/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Calendar;
import java.util.List;

/**
 *
 * @author BBEIRIGO
 */
public class Photo implements Comparable<Photo>{
    private int id;
    private String path;
    private Calendar uploadTime;
    public Photo(int id, String path, Calendar uploadTimestamp) {
        this.id = id;
        this.path = path;
        this.uploadTime = uploadTimestamp;
    }
    
    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
    }
    
    public Photo(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Photo{" + "id=" + id + ", path=" + path + ", timestamp=" + uploadTime + "}";
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Calendar getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Calendar uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public int compareTo(Photo o) {
        return this.uploadTime.compareTo(o.uploadTime);
    }

    
}
