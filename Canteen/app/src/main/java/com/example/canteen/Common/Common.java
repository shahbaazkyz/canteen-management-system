package com.example.canteen.Common;

import com.example.canteen.Model.Request;
import com.example.canteen.Model.User;

public class Common {
    public static User currentUser;
    public static Request currentRequest;
    public static final String DELETE="Delete";
    public static final String UPDATE="Update";

   public static String convertCodeToStatus(String status)
    {
        if(status!=null && status.equals("0"))
            return "Order Placed.....";
        else if(status!=null && status.equals("1"))
            return "Being Cooked";
        else
            return "Order is Ready!!!";
    }
}
