package com.maxfill.escom.bpm.services;

import com.maxfill.escom.bpm.model.ActualRelease;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@LocalBean
public class Config{
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private ActualRelease actualRelease;

    @PostConstruct
    private void init() {
        String propertyFile = System.getProperty("escom-info.properties");
        File file = new File(propertyFile);
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(file));
            String version = (String) properties.get("VERSION");
            String number = (String) properties.get("NUMBER");
            String date = (String) properties.get("DATE");
            String page = (String) properties.get("URL");
            actualRelease = new ActualRelease(number, date, page, version);
            LOGGER.log(Level.INFO, null, "INFO: Service escom-info start ok!");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /* GETS & SETS */

    public ActualRelease getActualRelease() {
        return actualRelease;
    }

}
