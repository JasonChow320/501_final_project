package com.cs501.project.Model;

import java.util.ArrayList;
import java.util.Arrays;

public class Account {

    private ArrayList<Profile> accounts;

    public Account(){
        this.accounts = new ArrayList<Profile>();
    }

    public ArrayList<Profile> getAccounts() {
        return this.accounts;
    }

    public boolean setAccounts(ArrayList<Profile> accounts){
        if(accounts == null){
            return false;
        }

        this.accounts = accounts;
        return true;
    }

    @Override
    public String toString(){
        return Arrays.toString(accounts.toArray());
    }
}
