package com.example.canteenserver.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String SRN;
    private String total;
    private String status;
    private List<Order> foods; //list of food order

    public Request() {
    }

    public Request(String phone, String name, String SRN, String total, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.SRN = SRN;
        this.total = total;
        this.foods = foods;
        this.status = "0"; // Default is 0 ; 0:Placed , 1:Processing , 2:Ready
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSRN() {
        return SRN;
    }

    public void setSRN(String SRN) {
        this.SRN = SRN;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

}
