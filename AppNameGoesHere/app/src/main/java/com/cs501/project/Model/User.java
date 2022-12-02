package com.cs501.project.Model;

public class User {
    private String userId, username;
    private Wardrobe wardrobe;
    private boolean passwordProtected;
    private User_settings user_settings;

    public User(){
        this("default_id", "default_user");
    }

    public User(String userId, String username){

        this(userId, username, null, false);
    }

    public User(String userId, String username, Wardrobe wardrobe, boolean password_protected){

        if(userId == null){
            this.userId = new String();
        } else {
            this.userId = userId;
        }

        if(username == null) {
            this.username = new String();
        } else {
            this.username = username;
        }

        if(wardrobe == null) {
            this.wardrobe = new Wardrobe();
        } else {
            this.wardrobe = wardrobe;
        }

        this.passwordProtected = password_protected;
        this.user_settings = new User_settings();
    }

    /* Public methods */
    public String getUserId(){
        return this.userId;
    }

    public String getUsername(){
        return this.username;
    }

    public Wardrobe getWardrobe(){
        return this.wardrobe;
    }

    public boolean getPasswordProtected(){
        return this.passwordProtected;
    }

    public User_settings getUserSettings(){
        return this.user_settings;
    }
    public void setUserSettings(User_settings settings){
        if(settings != null){
            this.user_settings = settings;
        }
    }

    public boolean setUserId(String userId){

        if(userId == null){
            return false;
        }

        this.userId = userId;
        return true;
    }

    public boolean setUsername(String username){

        if(username == null){
            return false;
        }

        this.username = username;
        return true;
    }

    public boolean setWardrobe(Wardrobe wardrobe){

        if(wardrobe == null){
            return false;
        }

        this.wardrobe = wardrobe;
        return true;
    }

    public boolean setPasswordProtected(boolean password_protected){
        this.passwordProtected = password_protected;

        return true;
    }

    @Override
    public String toString(){
        String to_str = new String();

        to_str += this.userId + "\n";
        to_str += this.username + "\n";

        return to_str;
    }
}
