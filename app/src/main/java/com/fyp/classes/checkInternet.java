package com.fyp.classes;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class checkInternet {
   private boolean isAvailable;
    public checkInternet() throws UnknownHostException {
        InetAddress ipAddr = InetAddress.getByName("google.com");
        //You can replace it with your name
        isAvailable= ipAddr.equals("");
    }
    public boolean getIsAvailable(){
        return this.isAvailable;
    }

}
