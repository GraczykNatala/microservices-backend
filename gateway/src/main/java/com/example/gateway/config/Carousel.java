package com.example.gateway.config;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;



@Service
@Slf4j
public class Carousel {

    private final EurekaClient eurekaClient;
    List<InstanceInfo> instances = new ArrayList<>();
    int currentIndex = 0;

    public Carousel(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
        try {
            initAuthCarousel();
        } catch(NullPointerException e) {
            log.warn("--Can't find active instances of Auth Service");
        }
        events();
    }
    private void initAuthCarousel() throws NullPointerException {
        log.info("--START initAuthCarousel");
        instances = eurekaClient.getApplication("AUTH-SERVICE").getInstances();
        log.info("--STOP initAuthCarousel");
    }

    private void events(){
        eurekaClient.registerEventListener(eurekaEvent -> {
            log.info("--START initAuthCarousel - register event");
            initAuthCarousel();
            log.info("--STOP initAuthCarousel - register event");
        });
        eurekaClient.unregisterEventListener(eurekaEvent -> {
            try{
                log.info("--START initAuthCarousel - unregister event");
                initAuthCarousel();
            }catch (NullPointerException e){
                log.warn("--Can't find active instances of Auth Service");
            }
            log.info("--STOP initAuthCarousel - unregister event");
        });
    }

    public String getUriAuth(){
        StringBuilder stringBuilder = new StringBuilder();
        InstanceInfo instance = instances.get(currentIndex);
        stringBuilder.append(instance.getIPAddr()).append(":").append(instance.getPort());
        if (instances.size()-1 == currentIndex){
            currentIndex = 0;
        }else {
            currentIndex++;
        }
        return stringBuilder.toString();
    }



}
