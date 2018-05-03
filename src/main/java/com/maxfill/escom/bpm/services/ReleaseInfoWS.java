package com.maxfill.escom.bpm.services;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

/* web socket ws://localhost:8090/escom-bpm-info-1.0-SNAPSHOT/release_info */
@Stateful //аннотация добавлена чтобы была возможность инжекции EJB
@ServerEndpoint("/release_info")
public class ReleaseInfoWS {
    @EJB
    private Config config;

    @OnMessage
    public String onMessage(String license) {
        System.out.println("license=" + license);
        return config.getActualRelease().toJSON();
    }
}