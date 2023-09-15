package org.example;

import org.example.entity.Endpoint;

import java.util.ArrayList;
import java.util.List;

public interface ApiGatewayEndpointConfiguration {
    List<Endpoint> endpointList = new ArrayList<>();
    void initMap();
    void register();
}
