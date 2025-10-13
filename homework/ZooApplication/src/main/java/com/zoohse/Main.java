package com.zoohse;


import com.zoohse.config.ZooConfig;
import com.zoohse.console.ZooApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main( String[] args ) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ZooConfig.class);
        ZooApplication app = context.getBean(ZooApplication.class);

        app.run();
        app.close();
    }
}
