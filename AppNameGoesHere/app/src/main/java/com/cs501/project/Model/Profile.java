package com.cs501.project.Model;

import java.util.ArrayList;

public class Profile {
    private ArrayList<User> users;
    private String accountEmail;
    private String userId;

    public Profile(){
        this(new String(), new String());
    }

    public Profile(String accountEmail, String userId){
        this.accountEmail = accountEmail;
        this.userId = userId;
        this.users = new ArrayList<User>();
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

    public void setUsers(ArrayList<User> users){
        if(users == null){
            return;
        }

        this.users = users;
    }

    public void setAccountEmail(String email){
        if(email == null){
            return;
        }

        this.accountEmail = email;
        return;
    }

    public void setUserId(String id){
        if(id == null){
            return;
        }

        this.userId = id;
        return;
    }

    public void addUser(User user){
        if(user == null){
            return;
        }

        this.users.add(user);
        return;
    }

    public String toString(){
        return this.accountEmail + ", " + this.userId;
    }
}
