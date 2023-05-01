package com.newproject.pace.Model;

public class contacts {
    public String name;
    public String number;
    public String id;

    public contacts(String name, String number, String id) {
        this.number = number;
        this.name = name;
        this.id = id;
    }

    public contacts() {
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
