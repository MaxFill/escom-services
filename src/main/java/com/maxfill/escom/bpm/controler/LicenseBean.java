package com.maxfill.escom.bpm.controler;

import com.google.gson.Gson;
import com.maxfill.escom.bpm.model.License;
import com.maxfill.escom.bpm.model.LicenseFacade;
import com.maxfill.escom.bpm.util.JsfUtil;
import com.maxfill.escom.bpm.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named
@SessionScoped
public class LicenseBean implements Serializable {
    private static final long serialVersionUID = 4642270371945216061L;

    @EJB
    private LicenseFacade ejbFacade;
    private List<License> items = null;
    private License selected;
            
    public LicenseBean() {
    }      

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LicenseFacade getFacade() {
        return ejbFacade;
    }

    public License prepareCreate() {
        selected = new License();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        saveModulesToJSON();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        saveModulesToJSON();        
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LicenseUpdated"));
    }

    private void saveModulesToJSON(){
        Gson gson = new Gson();        
        String modulesJSON = gson.toJson(selected.getModules());
        selected.setModulesJSON(modulesJSON);
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LicenseDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<License> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public License getLicense(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<License> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<License> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /* GETS & SETS */
        
    public License getSelected() {
        return selected;
    }
    public void setSelected(License selected) {
        this.selected = selected;
    }
    
    @FacesConverter(forClass = License.class)
    public static class licenseBeanConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LicenseBean controller = (LicenseBean) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "licenseBean");
            return controller.getLicense(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof License) {
                License o = (License) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), License.class.getName()});
                return null;
            }
        }

    }

}
