package com.example.component_advisor;

import java.util.ArrayList;
import java.util.List;

public class ComponentExpert{
    List<String> getComponents(String type){
        List<String> components = new ArrayList<String>();
        if (type.equals("Процессоры")){
            components.add("Intel");
            components.add("AMD");
        }else{
            if(type.equals("Мониторы")){
                components.add("LG");
                components.add("Samsung");
                components.add("Sony");
            }else{
                components.add("HyperX");
                components.add("Kingston");
                components.add("Crucial");
            }
        }
        return components;
    }
}
