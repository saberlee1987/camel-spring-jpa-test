package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.DeletePersonResponse;
import com.saber.camel.spring_jpa_test.dto.ServerErrorResponse;
import com.saber.camel.spring_jpa_test.entities.Person;
import com.saber.camel.spring_jpa_test.exceptions.InvalidDataException;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DeletePersonRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        rest("/person")
                .delete("/delete/{id}")
                .id(Routes.DELETE_PERSON)
                .description("delete Person")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage().code(200).responseModel(Person.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServerErrorResponse.class).endResponseMessage()
                .responseMessage().code(400).responseModel(ServerErrorResponse.class).endResponseMessage()
                .enableCORS(true)
                .param().type(RestParamType.path).name("id").defaultValue("1").example("1").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .route()
                .routeId(Routes.DELETE_PERSON)
                .threads(50, 80, Routes.DELETE_PERSON).maxQueueSize(3000).allowCoreThreadTimeOut(true)
                .log("Request for delete person  with id ${in.header.id}")
                .process(exchange -> {
                    String id = exchange.getMessage().getHeader("id", String.class);

                    if (id == null || id.trim().equals("") || !id.trim().matches("\\d+")) {
                        throw new InvalidDataException("please enter correct id");
                    }
                })
                .to("direct:find-person-by-id")
                .toD("jpa:" + Person.class.getName() + "?nativeQuery=delete from persons where id =${in.header.id}&useExecuteUpdate=true")
                .log("Response for delete  Person ===> ${in.body}")
                .process(exchange -> {
                    Integer id = exchange.getMessage().getHeader("id", Integer.class);
                    DeletePersonResponse response = new DeletePersonResponse();
                    response.setCode(0);
                    response.setMessage(String.format("person with id %d is deleted", id));
                    exchange.getMessage().setBody(response);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
