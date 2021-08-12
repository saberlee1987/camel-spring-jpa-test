package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.PersonDto;
import com.saber.camel.spring_jpa_test.dto.ServerErrorResponse;
import com.saber.camel.spring_jpa_test.entities.Person;
import com.saber.camel.spring_jpa_test.exceptions.InvalidDataException;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class UpdatePersonRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        rest("/person")
                .put("/update/{id}")
                .id(Routes.UPDATE_PERSON)
                .description("update Person")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage().code(200).responseModel(Person.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServerErrorResponse.class).endResponseMessage()
                .responseMessage().code(400).responseModel(ServerErrorResponse.class).endResponseMessage()
                .enableCORS(true)
                .param().type(RestParamType.path).name("id").defaultValue("1").example("1").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .type(PersonDto.class)
                .route()
                .routeId(Routes.UPDATE_PERSON)
                .setHeader(Headers.REQUEST_BODY,simple("${in.body}"))
                .threads(50,80,Routes.UPDATE_PERSON).maxQueueSize(3000).allowCoreThreadTimeOut(true)
                .log("Request for update person with body ${in.body} with id ${in.header.id}")
                .process(exchange -> {
                    String id = exchange.getMessage().getHeader("id",String.class);

                    if (id==null || id.trim().equals("") || !id.trim().matches("\\d+")){
                        throw new InvalidDataException("please enter correct id");
                    }
                })
                .to("direct:find-person-by-id")
                .process(exchange -> {
                    Person person = exchange.getMessage().getBody(Person.class);
                    PersonDto personDto = exchange.getMessage().getHeader(Headers.REQUEST_BODY,PersonDto.class);
                    person.setFirstName(personDto.getFirstName());
                    person.setLastName(personDto.getLastName());
                    person.setAge(personDto.getAge());
                    exchange.getMessage().setBody(person);
                })
                .to("jpa:"+Person.class.getName()+"?useExecuteUpdate=true")
                .removeHeader(Headers.REQUEST_BODY)
                .log("Response for update new Person ===> ${in.body}");
    }
}
