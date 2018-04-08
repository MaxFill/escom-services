package com.maxfill.escom.bpm.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ActualRelease implements Serializable{
    private static final long serialVersionUID = 8047333907746200457L;

    private String number;
    private String date;
    private String page;
    private String version;

    public ActualRelease(String number, String date, String page, String version) {
        this.number = number;
        this.date = date;
        this.page = page;
        this.version = version;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getPage() {
        return page;
    }
    public void setPage(String page) {
        this.page = page;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }   
    
    public String toJSON(){
        ObjectMapper mapper = new ObjectMapper();
        String retVal = "";
        try {
            retVal = mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(ActualRelease.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retVal;
    }
    
}
