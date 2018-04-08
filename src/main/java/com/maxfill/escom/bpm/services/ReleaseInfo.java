package com.maxfill.escom.bpm.services;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.commons.lang3.StringUtils;

/* SOAP сервис по http://localhost:8090/escom-bpm-info-1.0-SNAPSHOT/ReleaseInfo */
@WebService(serviceName = "ReleaseInfo")
public class ReleaseInfo {

    @EJB
    private Config config;

    @WebMethod(operationName = "getReleaseInfo")
    public String getReleaseInfo(@WebParam(name = "licenceNumber") String licenceNumber) {
        if (StringUtils.isBlank(licenceNumber)){
            throw new NullPointerException("ERROR: licenceNumber is null!");
        }
        return config.getActualRelease().toJSON();
    }
}