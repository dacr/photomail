/*
 * Form.java
 *
 * Created on 23 février 2005, 16:04
 */

package com.photomail.form;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Administrateur
 */
public class Form<DataBean> extends JPanel implements FocusListener, PropertyChangeListener {
    private DataBean dataBean;
    private boolean changeOccuredStatus=false;
    
    public boolean getChangeOccuredStatus() {
        return changeOccuredStatus;
    }
    public void resetChangeOccuredStatus() {
        changeOccuredStatus=false;
    }
    
    public Form(DataBean databean) {
        super();
        setDataBean(databean);
    }
    public Form(DataBean databean, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        setDataBean(databean);
    }
    public Form(DataBean databean, LayoutManager layout) {
        super(layout);
        setDataBean(databean);
    }
    public Form(DataBean databean, LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        setDataBean(databean);
    }
    public DataBean getDataBean() {
        return dataBean;
    }
    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }
    private List inputComponents = new ArrayList();
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        if (JScrollPane.class.isAssignableFrom(comp.getClass())) {
            // Pour prendre en compte un composant inclut dans un ScrollPane
            comp = ((JScrollPane)comp).getViewport().getView();
        }
        Class inputClasses[] = {
                JTextComponent.class,
                JSlider.class,
                JComboBox.class,
                JSpinner.class,
                JList.class,
                JToggleButton.class,
                JTable.class
        };
        for(Class inputClass : inputClasses) {
            if (inputClass.isAssignableFrom(comp.getClass())) {
                inputComponents.add(comp);
                if (JFormattedTextField.class.isAssignableFrom(comp.getClass())) {
                    // TODO : Pour le moment utilisé que pour JFormattedTextField => ETENDRE L'UTILISATSION
                    // Comme ce composant a une gestion spéciale du focus... on écoute les changements via un autre canal
                    JFormattedTextField ftf = (JFormattedTextField)comp;
                    ftf.addPropertyChangeListener("value", this);
                } else {
                    // Comportement standard, quand le focus est perdu, on sauvegarde la valeur saisie
                    comp.addFocusListener(this);
                }
                initComponentWithBean(comp);
            }
        }
    }
    protected void initComponentWithBean(Component component) {
        try {
            if (JFormattedTextField.class.isAssignableFrom(component.getClass())) {
                JFormattedTextField textComponent = (JFormattedTextField)component;
                textComponent.setValue(getAttributeValue(getDataBean(), component.getName()));
                return;
            }
            if (JTextComponent.class.isAssignableFrom(component.getClass())) {
                JTextComponent textComponent = (JTextComponent)component;
                textComponent.setText(getAttributeValueAsString(getDataBean(), component.getName()));
                return;
            }
            if (JSlider.class.isAssignableFrom(component.getClass())) {
                JSlider sliderComponent = (JSlider)component;
                sliderComponent.setValue(getAttributeValueAsInt(getDataBean(), component.getName()));
                return;
            }
            if (JSpinner.class.isAssignableFrom(component.getClass())) {
                JSpinner spinnerComponent = (JSpinner)component;
                spinnerComponent.setValue(new Integer(getAttributeValueAsInt(getDataBean(), component.getName())));
                return;
            }
            if (JCheckBox.class.isAssignableFrom(component.getClass())) {
                JCheckBox checkbox = (JCheckBox)component;
                checkbox.setSelected(getAttributeValueAsBoolean(getDataBean(), component.getName()));
                return;
            }
            if (JComboBox.class.isAssignableFrom(component.getClass())) {
                JComboBox comboComponent = (JComboBox)component;
                comboComponent.setModel(new DefaultComboBoxModel(getAttributeValues(getDataBean(), component.getName())));
                comboComponent.getModel().setSelectedItem(getAttributeValue(getDataBean(), component.getName()));
                return;
            }
        } catch(Exception e) {
            
        }
    }
    
    /**
     * force bean update from components current values
     */
    public void updateBean() {
        for(Component comp : getComponents()) {
            if (JScrollPane.class.isAssignableFrom(comp.getClass())) {
                // Pour prendre en compte un composant inclut dans un ScrollPane
                comp = ((JScrollPane)comp).getViewport().getView();
            }
            updateBeanFromComponent(comp);
        }
    }

    protected void updateBeanFromComponent(Component component) {
        try {
            Object value=null;

            if (JFormattedTextField.class.isAssignableFrom(component.getClass())) {
                value = ((JFormattedTextField)component).getValue();
            } else
            if (JTextComponent.class.isAssignableFrom(component.getClass())) {
                value = ((JTextComponent)component).getText();
            } else
            if (JSlider.class.isAssignableFrom(component.getClass())) {
                value = ((JSlider)component).getValue();
            } else
            if (JSpinner.class.isAssignableFrom(component.getClass())) {
                value = ((JSpinner)component).getValue();
            } else
            if (JCheckBox.class.isAssignableFrom(component.getClass())) {
                value = ((JCheckBox)component).isSelected();
            } else
            if (JComboBox.class.isAssignableFrom(component.getClass())) {
                value = ((JComboBox)component).getModel().getSelectedItem();
            } else {
                // TODO : FixMe, raise an exception
            }
            updateData(getDataBean(), component.getName(), value);
        } catch(Exception e) {
            // TODO : FixMe, raise an exception
        }
    }

    public void focusGained(FocusEvent focusEvent) {
    }

    public void focusLost(FocusEvent focusEvent) {
        Component comp = (Component)focusEvent.getSource();
        updateBeanFromComponent(comp);
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        // TODO : Pour le moment utilisé que pour JFormattedTextField => ETENDRE L'UTILISATSION
        Component comp = (Component)propertyChangeEvent.getSource();
        updateBeanFromComponent(comp);
    }
    
    
    // **************************************************************************************************
    public Object[] getAttributeValues(Object bean, String attributeName) throws Exception {
        // ------------- Processing if value is an enumeration (returns then possible values)
        PropertyDescriptor pd = new PropertyDescriptor(attributeName, bean.getClass());
        Class attributeClass  = primitive2Class(pd.getPropertyType());
        if (attributeClass.isEnum()) {
            Method valuesGetter = attributeClass.getMethod("values");
            return (Object[])valuesGetter.invoke(null);
        }
        // ------------- Processing if value is a list (returns all values)
        Object value = getAttributeValue(bean, attributeName);
        if (List.class.isAssignableFrom(value.getClass())) {
            List list = (List)value;
            return list.toArray();
        }
        // ------------- By default return an array with one value
        Object[] r = {value};
        return r;
    }

    public Object getAttributeValue(Object bean, String attributeName) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(attributeName, bean.getClass());
        Method read = pd.getReadMethod();
        Object value = read.invoke(bean);
        return value;
    }
    
    public String getAttributeValueAsString(Object bean, String attributeName) throws Exception {
        return getAttributeValue(bean, attributeName).toString();
    }

    public int getAttributeValueAsInt(Object bean, String attributeName) throws Exception {
        Object value = getAttributeValue(bean, attributeName);
        if (value instanceof Integer)          return ((Integer)value).intValue();
        if (value.getClass() == Integer.TYPE)  return ((Integer)value).intValue();
        if (value instanceof Float)            return (int)((Float)value).floatValue();
        if (value.getClass() == Float.TYPE)    return (int)((Float)value).floatValue();
        if (value instanceof Double)           return (int)((Double)value).doubleValue();
        if (value.getClass() == Double.TYPE)   return (int)((Double)value).doubleValue();
        if (value instanceof String)           return Integer.valueOf((String)value);
        return 0;
    }

    public boolean getAttributeValueAsBoolean(Object bean, String attributeName) throws Exception {
        Object value = getAttributeValue(bean, attributeName);
        if (value instanceof Boolean)          return ((Boolean)value).booleanValue();
        if (value.getClass() == Boolean.TYPE)  return ((Boolean)value).booleanValue();
        if (value instanceof String)           return Boolean.valueOf((String)value);
        return false;
    }

    public Class primitive2Class(Class cl) {
        if (!cl.isPrimitive())      return cl;
        if (cl == Byte.TYPE)        return Byte.class;
        if (cl == Short.TYPE)       return Short.class;
        if (cl == Integer.TYPE)     return Integer.class;
        if (cl == Long.TYPE)        return Long.class;
        if (cl == Float.TYPE)       return Float.class;
        if (cl == Double.TYPE)      return Float.class;
        if (cl == Boolean.TYPE)     return Boolean.class;
        if (cl == Character.TYPE)   return Character.class;
       return cl;
    }
    public Class class2primitive(Class cl) {
        if (cl.isPrimitive())       return cl;
        if (cl == Byte.class)        return Byte.TYPE;
        if (cl == Short.class)       return Short.TYPE;
        if (cl == Integer.class)     return Integer.TYPE;
        if (cl == Long.class)        return Long.TYPE;
        if (cl == Float.class)       return Float.TYPE;
        if (cl == Double.class)      return Float.TYPE;
        if (cl == Boolean.class)     return Boolean.TYPE;
        if (cl == Character.class)   return Character.TYPE;
       return cl;
    }

    public void updateData(Object bean, String attributeName, Object givenValue) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(attributeName, bean.getClass());
        Class attributeClass  = primitive2Class(pd.getPropertyType());
        Class givenValueClass = givenValue.getClass();
        Method write = pd.getWriteMethod();
        Method decoder = null;
        try {
            // ----------------------------------------
            // 1. On cherche une methode ValueOf avec le bon type
            decoder = attributeClass.getMethod("valueOf", givenValueClass);
        } catch(NoSuchMethodException e1) {
            try {
                // ----------------------------------------
                // 2. Si on a pas trouver on retourne sur le type primitif (pour contrer l'autoboxing)
                decoder = attributeClass.getMethod("valueOf", class2primitive(givenValueClass));
            }  catch(NoSuchMethodException e2) {
                try {
                    // ----------------------------------------
                    // 3. On utilise valueOf avec String et on convertit la valeur en String
                    decoder = attributeClass.getMethod("valueOf", String.class);
                    givenValue = givenValue.toString();
                } catch(NoSuchMethodException e3) {
                    // ----------------------------------------
                    // 4. On effectue aucune conversion, on essaye d'affecter la valeur telle quelle
                }
            }
        }
        Object value;
        if (decoder != null) {
            value = decoder.invoke(null, givenValue);
        } else {
            value = givenValue;
        }
        write.invoke(bean, value);
        changeOccuredStatus=true;
    }

    public void updateData(Object bean, Map<String, Object> input) throws Exception {
        for(Map.Entry<String,Object> entry:input.entrySet()) {
            updateData(bean, entry.getKey(), entry.getValue());
        }
    }

}


