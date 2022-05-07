package com.hanghae.finalp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class FinalPApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalPApplication.class, args);
        log.info("abc");
        log.warn("warn");
        log.debug("");
        System.out.println("args = " + args);
    }


    // AWS우분투 시간대 한국으로 설정
    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
