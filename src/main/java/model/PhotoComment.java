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
public class PhotoComment extends Comment{
    private Photo photo;
    
    public PhotoComment(int id){
        super(id);
    }
    
    public PhotoComment(int id, User commentator, String content, Calendar timestamp, Photo photo) {
        super(id, content, commentator, timestamp);
        this.photo = photo;
    }   
}
