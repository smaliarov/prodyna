package com.smaliarov.prodyna.person.listener;


import com.smaliarov.prodyna.person.model.Person;

public interface PersonChangeListener {
    void onChange(String personId, Action action);

    public enum Action {
        CREATE, UPDATE, DELETE;
    }
}
