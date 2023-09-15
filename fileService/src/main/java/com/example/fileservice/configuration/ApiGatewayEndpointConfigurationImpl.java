package com.example.fileservice.configuration;

import jakarta.annotation.PostConstruct;
import org.example.ApiGatewayEndpointConfiguration;
import org.example.entity.Endpoint;
import org.example.entity.HttpMethod;
import org.example.entity.Response;
import org.example.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiGatewayEndpointConfigurationImpl implements ApiGatewayEndpointConfiguration {

        @Value("${api-gateway.url}")
        private String GATEWAY_URL;

        @PostConstruct
        public void startOperation(){
            initMap();
            register();
        }

    @Override
    public void initMap() {
        endpointList.add(new Endpoint("/api/v1/image", HttpMethod.GET, Role.GUEST));
        endpointList.add(new Endpoint("/api/v1/image", HttpMethod.POST, Role.GUEST));
        endpointList.add(new Endpoint("/api/v1/image", HttpMethod.PATCH, Role.GUEST));
        endpointList.add(new Endpoint("/api/v1/image", HttpMethod.DELETE, Role.ADMIN));
    }

    @Override
    public void register() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Response> response = restTemplate.postForEntity(GATEWAY_URL,
                                                                       endpointList,
                                                                       Response.class);
        if (response.getStatusCode().isError()) throw new RuntimeException();
    }
}
