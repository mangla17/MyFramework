package com.sabaFrameworkCode;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConstantsMap {
    public static Map<String, String> getMap() {
        Map<String, String> constantsMap  = new HashMap<String, String>();
        String[]            constantFiles = { "com.saba.guitest.people.PeopleConstants",
                							"com.saba.guitest.common.HomePageConstants", 
                							"com.saba.guitest.collaboration.GroupConstants",
                							"com.saba.guitest.resource.ResourcesConstants"};

        for (String constFile : constantFiles) {
            try {
                Object instance = Class.forName(constFile).newInstance();

                for (Field field : Class.forName(constFile).getDeclaredFields()) {
                    constantsMap.put(""+ field.get(instance), constFile +"."+ field.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return constantsMap;
    }
}
