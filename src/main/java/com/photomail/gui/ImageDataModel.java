/*
 * AvailableImageListDataModel.java
 *
 * Created on 5 avril 2005, 21:46
 */

package com.photomail.gui;

import com.photomail.image.ImageInfo;
import com.photomail.image.ImageInfoEvent;
import com.photomail.image.ImageInfoListener;
import com.photomail.image.ImageListener;
import com.photomail.setup.Resources;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;

import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author dcr
 */
public class ImageDataModel implements ImageListener, ImageInfoListener, ListModel, ComboBoxModel, TableModel {
    private List<ImageInfo> data;
    private List<ListDataListener> listeners;
    private List<TableModelListener> tableListeners;
    private Object selectedItem=null;
    
    public ImageDataModel() {
        data      = Collections.synchronizedList(new ArrayList<ImageInfo>());
        listeners = Collections.synchronizedList(new ArrayList<ListDataListener>());
        tableListeners = Collections.synchronizedList(new ArrayList<TableModelListener>());
    }
    
    // Bad hack to avoid duplicate photos between chosen and available photo
    // when a new search occured
    private ImageDataModel linkedDataModel;
    public ImageDataModel(ImageDataModel linkedDataModel) {
        this();
        this.linkedDataModel = linkedDataModel;
    }

    private void fireIntervalAdded(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,rowFirst, rowLast);
        synchronized(listeners) {
            for(ListDataListener listener: listeners) {
                listener.intervalAdded(event);
            }
        }
    }
    private void fireIntervalRemoved(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,rowFirst, rowLast);
        synchronized(listeners) {
            for(ListDataListener listener: listeners) {
                listener.intervalRemoved(event);
            }        
        }
    }
    private void fireContentsChanged(int rowFirst, int rowLast) {
        ListDataEvent event = new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,rowFirst, rowLast);
        synchronized(listeners) {
            for(ListDataListener listener: listeners) {
                listener.contentsChanged(event);
            }
        }
    }
    
    private void fireTableDataChanged() {
        TableModelEvent event = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        synchronized(tableListeners) {
            for(TableModelListener listener: tableListeners) listener.tableChanged(event);
        }
    }
    private void fireTableRowsDeleted(int firstRow, int lastRow) {
        TableModelEvent event = new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        synchronized(tableListeners) {
            for(TableModelListener listener: tableListeners) listener.tableChanged(event);
        }
    }
    private void fireTableRowsInserted(int firstRow, int lastRow) {
        TableModelEvent event = new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        synchronized(tableListeners) {
            for(TableModelListener listener: tableListeners) listener.tableChanged(event);
        }
    }
    private void fireTableRowsUpdated(int firstRow, int lastRow) {        
        TableModelEvent event = new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
        synchronized(tableListeners) {
            for(TableModelListener listener: tableListeners) listener.tableChanged(event);
        }
    }
    
    public List<ImageInfo> getData() {
        return Collections.unmodifiableList(data);
    }
    
    public ImageInfo get(int index) {
        return data.get(index);
    }
    
    public void add(ImageInfo imageInfo) {
        if (imageInfo == null) return; // ImageFinder has finished
//        synchronized(data) {
//            if (data.contains(imageInfo)) return;
//        }
        final int addedIndex = data.size();
        data.add(imageInfo);
        imageInfo.addImageInfoListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireIntervalAdded(addedIndex,  addedIndex);
                fireTableRowsInserted(addedIndex, addedIndex);
            }
        });
        if (selectedItem == null && data.size()>0) { // For ComboBoxModel
            setSelectedItem(data.get(0));
        }
    }
    
    public void remove(ImageInfo imageInfo) {
        final int removedIndex = data.indexOf(imageInfo);
        data.remove(imageInfo);
        imageInfo.removeImageInfoListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireIntervalRemoved(removedIndex, removedIndex);
                fireTableRowsDeleted(removedIndex, removedIndex);
            }
        });
        if (selectedItem == imageInfo) { // For ComboBoxModel
            if (data.size() > 0 ) {
                setSelectedItem(data.get(0));
            } else {
                setSelectedItem(null);
            }
        }
    }
    public void reset() {
        final int size = data.size();
        synchronized(data) {
            data.clear();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireIntervalRemoved(0, size-1);
                fireTableDataChanged();
            }
        });
        setSelectedItem(null);
    }
    
    /**
     * Receive message from ImageFinder
     */
    public void imageFound(ImageInfo event) {
        if (linkedDataModel != null) {
            // Bad hack to avoid duplicate photos between chosen and available photo
            // when a new search occured
            if (linkedDataModel.getData().contains(event)) return;
        }        
        add(event);
    }

    /**
     * Receive message from ThumbnailGenerator
     */
    public void changeOccured(ImageInfoEvent event) {
        if (event.isThumbnail()) {
            final int changedIndex = data.indexOf(event.getImageInfo());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireContentsChanged(changedIndex, changedIndex);
                    fireTableRowsUpdated(changedIndex, changedIndex);
                }
            });
        }
    }

    /* ---------------------------- ListModel ---------------------------- */

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

    
    /* ---------------------------- ComboBoxModel ---------------------------- */
    
    public Object getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Object item) {
        selectedItem = item;
        fireContentsChanged(data.indexOf(item), data.indexOf(item));
        
    }
    
    /* ---------------------------- TableModel ---------------------------- */
    public Class<Object> getColumnClass(int index) {
        return Object.class;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int index) {
        switch(index) {
            case 0:  return Resources.getString("image.table.column.photo");
            case 1:  return Resources.getString("image.table.column.date");
            default: return "N/A";
        }
    }

    public int getRowCount() {
        return getSize();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return getElementAt(rowIndex);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addTableModelListener(TableModelListener listener) {
        tableListeners.add(listener);
    }
    public void removeTableModelListener(TableModelListener listener) {
        tableListeners.remove(listener);
    }    
}


