package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.ServerErrorResponse;
import com.saber.camel.spring_jpa_test.exceptions.InvalidDataException;
import com.saber.camel.spring_jpa_test.exceptions.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class AbstractRestRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {


        onException(ResourceNotFoundException.class)
                .maximumRedeliveries(0)
                .handled(true)
                .process(exchange -> {
                    ResourceNotFoundException resourceNotFoundException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, ResourceNotFoundException.class);
                    ServerErrorResponse errorResponse = new ServerErrorResponse();
                    errorResponse.setCode(-1);
                    errorResponse.setMessage(resourceNotFoundException.getMessage());
                    errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"message\":\"%s\"}", 404, resourceNotFoundException.getMessage()));
                    exchange.getMessage().setBody(errorResponse);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(406));

        onException(InvalidDataException.class)
                .maximumRedeliveries(0)
                .handled(true)
                .process(exchange -> {
                    InvalidDataException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, InvalidDataException.class);
                    ServerErrorResponse errorResponse = new ServerErrorResponse();
                    errorResponse.setCode(-1);
                    errorResponse.setMessage(exception.getMessage());
                    errorResponse.setOriginalMessage(String.format("{\"code\":%d,\"message\":\"%s\"}", 400, exception.getMessage()));
                    exchange.getMessage().setBody(errorResponse);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));
    }
}
