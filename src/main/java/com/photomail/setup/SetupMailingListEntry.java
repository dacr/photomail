/*
 * SetupMailingListEntry.java
 *
 * Created on 22 septembre 2004, 22:57
 */

package com.photomail.setup;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author  dcr
 */
public class SetupMailingListEntry implements Serializable {
    
    private String name;
    private List<String> emailList;
    
    /** Creates a new instance of SetupMailingListEntry */
    public SetupMailingListEntry(String name, String... emails) {
        this.name = name;
        this.emailList = new ArrayList<String>();
        for(String email : emails) this.emailList.add(email);
    }
    
    public SetupMailingListEntry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList=emailList;
    }
    
}
