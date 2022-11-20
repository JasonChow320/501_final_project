package com.cs501.project.Model;

import java.util.ArrayList;

public class Profile {
    ArrayList<User> users;

    public Profile(){
        this.users = new ArrayList<User>();
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    public boolean setUsers(ArrayList<User> users){
        if(users == null){
            return false;
        }

        this.users = users;
        return true;
    }
}
