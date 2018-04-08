package com.maxfill.escom.bpm.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxfill.escom.bpm.model.License;
import com.maxfill.escom.bpm.model.LicenseFacade;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateful
@ServerEndpoint("/activate")
public class ActivateLicenseWS{
    private static final Logger LOGGER = Logger.getLogger(ActivateLicenseWS.class.getName());

    @EJB
    private LicenseFacade licenseFacade;

    @OnMessage
    public String onMessage(String json) {
        Set<String> errors = new HashSet <>();
        Map<String,String> licenseInfo = parseJson(json, errors);
        License license = checkLicense(licenseInfo, errors);
        if (errors.isEmpty()){
            return activateLicence(license);
        } else {
            return errorsToJson(errors);
        }
    }

    /**
     * Преобразование данных запроса из Json в Map
     * @param json
     * @return
     */
    private Map<String,String> parseJson(String json, Set<String> errors){
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> map = null;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            errors.add("ERROR: bad request!");
        }
        return map;
    }

    /**
     * Проверка лицензии
     * @param licenseInfo
     */
    private License checkLicense(Map<String,String> licenseInfo, Set<String> errors){
        if (licenseInfo == null) return null;
        String licenseNumber = licenseInfo.getOrDefault("number", null);
        String licenseKey = licenseInfo.getOrDefault("key", null);
        if (StringUtils.isBlank(licenseNumber)){
            errors.add("ERROR: license number not found in request!");
        }
        if (StringUtils.isBlank(licenseKey)){
            errors.add("ERROR: license key not found in request!");
        }
        if (!errors.isEmpty()) {
            return null;
        }
        License license = licenseFacade.findByNumber(licenseNumber);
        if (license == null){
            errors.add("ERROR: license number is not correct!");
            return null;
        }
        if (license.getDateActivate() != null){
            errors.add("ERROR: license already activated!");
            return null;
        }
        license.setKey(licenseKey);
        return license;
    }

    /**
     * Активация лицензии
     * @param license
     */
    private String activateLicence(License license){
        license.setDateActivate(new Date());
        licenseFacade.edit(license);
        String activateStr = license.toXML();
        String criptStr = doCrypto(license.getKey(), activateStr);
        return criptStr;
    }

    /**
     * Преобразование списка ошибок в Json
     * @param errors
     * @return
     */
    private String errorsToJson(Set<String> errors){
        StringBuilder sb = new StringBuilder("{\"error\":\"");
        errors.stream().forEach(err->sb.append(err).append(" "));
        sb.append("\"}");
        return sb.toString();
    }

    /**
     * Шифрование строки в AES
     * @param licenseKey
     * @param sourceStr
     * @return
     */
    private String doCrypto(String licenseKey, String sourceStr) {
        String result = null;
        try {
            Key secretKey = new SecretKeySpec(licenseKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceStr.getBytes());
            byte[] inputBytes = new byte[(int) sourceStr.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(outputBytes);
            result = outputStream.toString("UTF-8");
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
