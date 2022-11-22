package com.cs501.project.Model;

import java.util.ArrayList;

public class Profile {
    private ArrayList<User> users;
    private String accountEmail;
    private String userId;

    public Profile(){
        this.users = new ArrayList<User>();
        this.accountEmail = new String();
        this.userId = new String();
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public String getAccountEmail(){
        return this.accountEmail;
    }

    public String getUserId(){
        return this.userId;
    }

    public boolean setUsers(ArrayList<User> users){
        if(users == null){
            return false;
        }

        this.users = users;
        return true;
    }

    public boolean setAccountEmail(String email){
        if(email == null){
            return false;
        }

        this.accountEmail = email;
        return true;
    }

    public boolean setUserId(String id){
        if(id == null){
            return false;
        }

        this.userId = id;
        return true;
    }

    public String toString(){
        return this.accountEmail + ", " + this.userId;
    }
}
