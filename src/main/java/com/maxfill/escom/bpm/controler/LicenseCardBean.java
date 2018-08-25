package com.maxfill.escom.bpm.controler;

import com.maxfill.escom.bpm.model.License;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author maksim
 */
@Named
@ViewScoped
public class LicenseCardBean implements Serializable{    
    private static final long serialVersionUID = 5681162781178419915L;
    
    private License license;

    @PostConstruct
    private void init(){
        System.out.println("bean created");
    }
    
    @PreDestroy
    private void destroy(){
        System.out.println("bean destroy");
    }
    
    public void onBeforeLoad(){
        System.out.println("onBeforeLoad!");
    }
    
    public void save(){
        System.out.println("save!"); 
        //PrimeFaces.current().dialog().closeDynamic(null);
    }
    
    public String onClose(){
        System.out.println("close");
        //PrimeFaces.current().dialog().closeDynamic(null);
        return "list";
    }
    
    public License getLicense() {
        return license;
    }
    public void setLicense(License license) {
        this.license = license;
    }
    
    
}
