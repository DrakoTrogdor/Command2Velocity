package com.drakotrogdor.command2velocity;

import java.io.File;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigUtil {
    public static void load() {
        File configFile = new File("config.yml");
        if (configFile.length() == 0) {
            System.out.println("File 'config.yml' is empty ...");
        } else {
            System.out.println("File 'config.yml' is NOT empty ...");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try {
                Config config = mapper.readValue(new File("config.yml"), Config.class);
                System.out.println(ReflectionToStringBuilder.toString(config,ToStringStyle.MULTI_LINE_STYLE));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void save() {

    }
}
