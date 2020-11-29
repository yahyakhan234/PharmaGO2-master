package com.fyp.classes;

public class order {

    String email;
    String Name;
    String time;
    String meds[];
    String uid;
    String order_type;
    String order_id;
    int number_meds;


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }





    public int getNumber_meds() {
        return number_meds;
    }

    public void setNumber_meds(int number_meds) {
        this.number_meds = number_meds;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public order(String email) {
        this.email = email;
    }

    public order() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] getMeds() {
        return meds;
    }

    public void setMeds(String[] meds) {
        this.meds = meds;
    }
}
