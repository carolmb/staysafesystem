package com.example.ana.staysafesystem.processor;

/**
 * Created by ana on 19/11/17.
 */

public class Processor {

    private Processor instance;

    private Processor() {}

    public Processor getInstance() {
        if(instance != null) {
            return instance;
        } else {
            instance = new Processor();
            return instance;
        }
    }
}
