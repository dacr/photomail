/*
 * PanelMailRecipients.java
 *
 * Created on 11 avril 2005, 17:11
 */

package com.photomail.gui;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.photomail.setup.Resources;
import com.photomail.setup.Setup;
import com.photomail.setup.SetupMailingListEntry;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author dcr
 */
public class PanelMailRecipients extends JPanel {
    
    /** Creates a new instance of PanelMailRecipients */
    public PanelMailRecipients(Setup setup, StringListModel<List<String>> toModel) {

        FormLayout formlayout = new FormLayout(
                "4dlu, right:pref, 4dlu, left:pref:grow, 4dlu",
                "min, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref");

        setLayout(formlayout);
        setBorder(Borders.DIALOG_BORDER);

        CellConstraints cc = new CellConstraints();
        int r=1;

        JTitleLabel title = new JTitleLabel(Resources.getString("wizard.recipients.title"));
        add(title, cc.xyw(1,r,5));
        r+=2;
        
        Separator separator1 = new Separator(Resources.getString("wizard.recipients.known.title"));
        add(separator1, cc.xyw(1,r,4));
        r+=2;
        
        EmailsSelectorTableModel tableModel = new EmailsSelectorTableModel(setup, toModel);
        JTable knownEmailsTable = new EmailSelectorTable(tableModel);
        JScrollPane saKnownEmailsTable = new JScrollPane(knownEmailsTable);
        add(saKnownEmailsTable, cc.xy(4, r, "fill, fill"));
        r+=2;


        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        layout.setHgap(4);
        JPanel listButtons = new JPanel(layout);
        JButton addButton    = new JButton(new PanelMailCreateAction(knownEmailsTable));
        addButton.setFocusable(false);
        JButton removeButton = new JButton(new PanelMailDeleteAction(knownEmailsTable));
        removeButton.setFocusable(false);
        listButtons.add(addButton);
        listButtons.add(removeButton);
        add(listButtons, cc.xy(4, r, "left, fill"));
        r+=2;
    }
}


class PanelMailDeleteAction extends JButtonAction {
    private JTable table;
    public PanelMailDeleteAction(JTable table) {
        super(Resources.getString("wizard.recipients.action.delete.label"));;
        putValue(JButtonAction.SMALL_ICON, Resources.createUserDelete());
        this.table=table;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        int[] selectedIndexes = table.getSelectedRows();
        EmailsSelectorTableModel dataModel = (EmailsSelectorTableModel)table.getModel();
        dataModel.remove(selectedIndexes);
    }
}






class PanelMailCreateAction extends JButtonAction {
    private JTable table;
    public PanelMailCreateAction(JTable table) {
        super(Resources.getString("wizard.recipients.action.create.label"));
        putValue(JButtonAction.SMALL_ICON, Resources.createUserAdd());
        this.table=table;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        EmailsSelectorTableModel dataModel = (EmailsSelectorTableModel)table.getModel();
        int pos = dataModel.add(new EmailSelector("",false));
        table.getSelectionModel().setSelectionInterval(pos, pos);
        table.editCellAt(pos,1);
    }
}






class EmailSelector {
    private String  email;
    private boolean selected;

    public EmailSelector(String email, boolean selected) {
        this.email = email;
        this.selected = selected;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}








class EmailSelectorTable extends JTable {
    public EmailSelectorTable(TableModel tableModel) {
        super(tableModel);
    }
}







class EmailsSelectorTableModel extends AbstractTableModel {
    private StringListModel<List<String>> toModel;
    private SetupMailingListEntry collectedAddressesMailingList;
    
    private List<String>          collectedAddresses; // Référence au contenu de collectedAddressesMailingList
    private List<EmailSelector>   data; // ATTENTION data et collectedAddresses sont "identiques"
    

    public EmailsSelectorTableModel(Setup setup, StringListModel<List<String>> toModel) {
        this.data = new ArrayList<EmailSelector>();
        this.collectedAddresses = new ArrayList<String>();
        this.toModel = toModel;
        fillData(setup);
    }
    
    private void fillData(Setup setup) {
        // --------- Reconstruction de la liste des addresses collectées à partir des listes disponibles
        for(SetupMailingListEntry list: setup.getMailingList()) {
            if (list.getName()==null) {
                collectedAddressesMailingList = list;
            }
            for(String email: list.getEmailList()) {
                if (!collectedAddresses.contains(email)) {
                    collectedAddresses.add(email);
                }
            }
        }

        // -------- Impacts des adresses déjà sélectionnées
        Collection<String> toData = toModel.getData();
        for(String email: toData) {
            if (!collectedAddresses.contains(email)) {
                collectedAddresses.add(email);
            }
        }

        // -------- Mise à jour de la liste des addresses collectées (contenu dans le setup sous nom null)
        if (collectedAddressesMailingList==null) {
            collectedAddressesMailingList=new SetupMailingListEntry();
            collectedAddressesMailingList.setName(null);
            setup.getMailingList().add(collectedAddressesMailingList);
        }
        collectedAddressesMailingList.setEmailList(collectedAddresses);
        
        // -------- Initialisation des données
        for(String email : collectedAddresses) {
            boolean chosen=false;
            if (toData.contains(email)) chosen = true;
            data.add(new EmailSelector(email, chosen));
        }
    }
    

    public int add(String email, boolean selected) {
        EmailSelector newentry = new EmailSelector(email, selected);
        return add(newentry);
    }
    public int add(EmailSelector newentry) {
        int pos = data.size();
        data.add(newentry);
        if (newentry.getEmail().length() > 0) {
            collectedAddresses.add(newentry.getEmail());
        }
        fireTableRowsInserted(pos,pos);
        return pos;
    }
    public void remove(int index) {
        int[] indexes = {index};
        remove(indexes);
    }
    public void remove(int[] selectedIndexes) {
        List<EmailSelector> toRemove = new ArrayList<EmailSelector>();
        List<String> toRemoveEmail = new ArrayList<String>();
        for(int index:selectedIndexes) {
            toRemove.add(data.get(index));
            toRemoveEmail.add(data.get(index).getEmail());
        }
        data.removeAll(toRemove);
        collectedAddresses.removeAll(toRemoveEmail);
        fireTableDataChanged();
    }
    
    
    
    
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0: return Resources.getString("wizard.recipients.table.column.selected");
            case 1: return Resources.getString("wizard.recipients.table.column.email");
            default:
                return "N/A";
        }
    }
    
    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return data.size();
    }
    
    public boolean isCellEditable(int rowIndex, int column) {
        return true;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0: return data.get(rowIndex).isSelected();
            case 1: return data.get(rowIndex).getEmail();
            default:
                return "N/A";
        }
    }
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        EmailSelector emailSelector = data.get(rowIndex);
        String email = emailSelector.getEmail();
        switch(columnIndex) {
            case 0:
                if (((Boolean)value).booleanValue()) {
                    emailSelector.setSelected(true);
                    if (!toModel.contains(email)) {
                        toModel.add(email);
                    }
                } else {
                    emailSelector.setSelected(false);
                    if (toModel.contains(email)) {
                        toModel.remove(email);    
                    }
                }
                break;
            case 1:
                String newEmail = ((String)value).toLowerCase();
                if (newEmail.length()==0) {
                    remove(rowIndex);
                    return;
                }
                emailSelector.setEmail(newEmail);
                if (emailSelector.isSelected()) {
                    toModel.remove(email);
                    toModel.add(newEmail);
                }
                if (email.length() == 0) {
                    collectedAddresses.add(newEmail);
                }
                break;
            default:
        }
    }
}

