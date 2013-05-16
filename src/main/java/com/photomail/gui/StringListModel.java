/*
 * TmpListModel.java
 *
 * Created on 8 avril 2005, 16:14
 */

package com.photomail.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author dcr
 */
public class StringListModel<T extends List<String>> implements ListModel {
    private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
    private T data;

    private boolean changeOccuredStatus=false;
    
    public boolean getChangeOccuredStatus() {
        return changeOccuredStatus;
    }
    public void resetChangeOccuredStatus() {
        changeOccuredStatus=false;
    }

    
    public StringListModel(T data) {
        this.data = data;
    }

    public void remove(String value) {
        int pos = data.indexOf(value);
        data.remove(value);
        fireIntervalRemoved(pos, pos);
        changeOccuredStatus=true;
    }
    
    public void add(String value) {
        int pos=data.size();
        data.add(value);
        fireIntervalAdded(pos, pos);
        changeOccuredStatus=true;
    }
    
    public boolean contains(String value) {
        return data.contains(value);
    }
    
    public Collection<String> getData() {
        return Collections.unmodifiableCollection(data);
    }
            
    public Object getElementAt(int index) {
        return data.get(index);
    }

    public int getSize() {
        return data.size();
    }

    public void addListDataListener(ListDataListener listener) {
        listeners.add(listener);
    }

    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }
    private void fireIntervalAdded(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,rowFirst, rowLast);
        for(ListDataListener listener: listeners) {
            listener.intervalAdded(event);
        }
    }
    private void fireIntervalRemoved(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,rowFirst, rowLast);
        for(ListDataListener listener: listeners) {
            listener.intervalRemoved(event);
        }        
    }
    private void fireContentsChanged(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,rowFirst, rowLast);
        for(ListDataListener listener: listeners) {
            listener.contentsChanged(event);
        }
    }

}
