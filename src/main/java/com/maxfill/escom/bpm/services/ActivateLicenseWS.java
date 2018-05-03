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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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
        List<License> licenses = licenseFacade.findByNumber(licenseNumber);
        if (licenses.isEmpty()){
            errors.add("ERROR: license number is not correct!");
            return null;
        }
        License license = licenses.get(0);
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
        return doCrypto(license.getKey(), license.getNumber(), activateStr);
    }

    /**
     * Преобразование списка ошибок в строку
     * @param errors
     * @return
     */
    private String errorsToJson(Set<String> errors){
        StringBuilder sb = new StringBuilder();
        errors.stream().forEach(err->sb.append(err).append(" "));
        return sb.toString();
    }

    /**
     * Шифрование строки в AES
     * @param keyInfo
     * @param sourceStr
     * @return
     */
    private String doCrypto(String keyInfo, String initVector, String sourceStr) {
        String result = null;
        try {
            String licenseKey = keyInfo.substring(0, 16);
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(licenseKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] encrypted = cipher.doFinal(sourceStr.getBytes());
            result = Base64.getEncoder().encodeToString(encrypted);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
