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
public class Comment { 
    private int id;
    private String content;
    private User commentator;
    private Calendar timeComment;

    public Comment(int id, String content, User commentator, Calendar timestamp) {
        this.id = id;
        this.content = content;
        this.commentator = commentator;
        this.timeComment = timestamp;
    }
    
    public Comment(User commentator, String content) {
        this.content = content;
        this.commentator = commentator;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCommentator() {
        return commentator;
    }

    public void setCommentator(User commentator) {
        this.commentator = commentator;
    }

    public Calendar getTimeComment() {
        return timeComment;
    }

    public void setTimeComment(Calendar timeComment) {
        this.timeComment = timeComment;
    }

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", content=" + content + ", commentator=" + commentator + ", timeComment=" + timeComment + '}';
    }
    
    
    
}
