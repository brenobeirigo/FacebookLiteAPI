/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Calendar;

/**
 *
 * @author BBEIRIGO
 */
public class User {
    private int id;
    private String name;
    private String email;
    private Calendar dateOfBirth;
    private Calendar dateOfEnrollment;
    private Photo profilePhoto;
    private Photo coverPhoto;

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", email=" + email + ", dateOfBirth=" + dateOfBirth + ", profilePhoto=" + profilePhoto + ", coverPhoto=" + coverPhoto + '}';
    }
    
    public User(int id) {
    this.id = id;
    }
    
    public User(int id, String name, Photo profilePhoto, Photo coverPhoto) {
        this.id = id;
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.coverPhoto = coverPhoto;
    }
    
    public User(int id, String name, String email) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public User(String name, String email, Calendar dateOfBirth) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }
        
    
    public User(int id, String name, Photo profilePhoto) {
        this.id = id;
        this.name = name;
        this.profilePhoto = profilePhoto;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Calendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Calendar getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment(Calendar dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public Photo getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Photo profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Photo getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(Photo coverPhoto) {
        this.coverPhoto = coverPhoto;
    }
}