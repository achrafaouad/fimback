package com.example.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource("data.properties")

public class StationNameService {

    @Autowired
    private Environment env;


    public void init() {

    }


    public void setupHash(){

    }
    public String get(Integer name){
        System.out.println("" + name);
        System.out.println(env.getProperty("" + name));
        return env.getProperty("" + name);
    }
}
