package com.saber.camel.spring_jpa_test.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RouteDefinition extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        restConfiguration()
                .contextPath("/services/camel")
                .apiContextPath("/v2/api-docs")
                .apiContextRouteId("/swagger")
                .apiProperty("api.title","true")
                .apiProperty("api.version","ver1")
                .apiProperty("cors","true")
                .enableCORS(true)
                .component("servlet")
                .dataFormatProperty("prettyPrint","true")
                .bindingMode(RestBindingMode.json);

    }
}
