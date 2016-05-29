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
public class AlbumComment extends Comment{
    Album album;

    public AlbumComment(int id, String content, User commentator, Calendar timestamp, Album album) {
        super(id, content, commentator, timestamp);
        this.album = album;
    }
}
