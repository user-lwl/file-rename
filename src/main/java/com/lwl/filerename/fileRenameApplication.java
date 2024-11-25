package com.lwl.filerename;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class fileRenameApplication {

    public static void main(String[] args) {
        SpringApplication.run(fileRenameApplication.class, args);
    }

}
