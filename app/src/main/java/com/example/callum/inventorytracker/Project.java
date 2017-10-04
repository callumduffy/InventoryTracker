package com.example.callum.inventorytracker;
import java.util.Date;
import java.util.ArrayList;
/**
 * Created by Rory on 19/11/2016.
 */
public class Project {
    private String projectID;
    private String projectName;
    private String individual;
    private String expiryDate;


    Project(String ID,String name, String indiv, String expiry) {
        projectID=ID;
        projectName=name;
        individual=indiv;
        expiryDate=expiry;
    }

    public void add(EquipmentItem item){//TODO // FIXME: 30/11/2016
        
    }
    public boolean contains(EquipmentItem item){
        return true;//TODO
    }
    public String getProjectID(){
        return projectID;
    }
    public String getProjectName(){
        return projectName;
    }
    public String getExpiryDate(){
        return expiryDate;
    }
    public String getIndividual(){
        return individual;
    }
}

