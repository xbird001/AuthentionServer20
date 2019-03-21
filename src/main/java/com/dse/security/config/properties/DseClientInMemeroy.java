package com.dse.security.config.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DseClientInMemeroy {

    private Logger logger = LoggerFactory.getLogger(DseUserInMemeroy.class);

    private List<String> clients = new ArrayList<>();


    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }
}
