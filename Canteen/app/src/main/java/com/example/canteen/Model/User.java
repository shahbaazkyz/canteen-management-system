package com.example.canteen.Model;


public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String isStaff;

    public User() {
    }

    public User(String phone, String name, String password) {
        Phone = phone;
        Name = name;
        Password = password;
        isStaff = "false";
    }

    public String getIsStaff() {
        return isStaff;
    }

    public User setIsStaff(String isStaff) {
        this.isStaff = isStaff;
        return this;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
