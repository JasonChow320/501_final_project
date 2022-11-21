package com.cs501.project.Model;

import java.util.ArrayList;

public class Profile {
    private ArrayList<User> users;
    private String account_email;

    public Profile(){
        this.users = new ArrayList<User>();
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public String getEmail(){
        return this.account_email;
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

        this.account_email = email;
        return true;
    }

    public String toString(){
        return this.account_email;
    }
}
