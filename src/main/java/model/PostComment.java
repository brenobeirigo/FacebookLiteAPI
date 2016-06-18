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
public class PostComment extends Comment{
    private Post post;

    public PostComment(int id) {
        super(id);
    }

    public PostComment(Post post, User commentator, String content) {
        super(commentator, content);
        this.post = post;
    }
    
    
    public PostComment(int id, String content, User commentator, Calendar timestamp, Post post) {
        super(id, content, commentator, timestamp);
        this.post = post;
    }

    @Override
    public String toString() {
        return "PostComment{" +super.toString()+ "post=" + post + '}';
    }
    
}
