package com.fyp.classes;

public class Customer implements user {
    private String Name;
    private String Email;
    private String Location;
    private int rating;
    private int phoneno;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public void login_user(String em, String pw) {

    }

    @Override
    public void change_password(String em) {

    }

    @Override
    public void generate_complaint(String em) {

    }

    public Customer() {

    }
}
