package com.smaliarov.prodyna.person.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonChangeLogListener implements PersonChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(PersonChangeLogListener.class);

    @Override
    public void onChange(String personId, Action action) {
        LOG.info("Called {} for personId {}", action, personId);
    }
}
