package com.example.gateway.controller;


import com.example.gateway.filter.RouteValidator;
import lombok.RequiredArgsConstructor;
import org.example.entity.Endpoint;
import org.example.entity.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/gateway")
@RequiredArgsConstructor
public class RegistrationController {

    private final RouteValidator routeValidator;

    @PostMapping
    public ResponseEntity<Response> register (@RequestBody List<Endpoint> endpoints){
        routeValidator.addEndpoints(endpoints);
        return ResponseEntity.ok(new Response("Successfully registered new endpoints"));
    }

}
