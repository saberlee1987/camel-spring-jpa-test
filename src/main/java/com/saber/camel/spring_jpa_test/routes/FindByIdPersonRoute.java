package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.PersonResponseDto;
import com.saber.camel.spring_jpa_test.dto.ServerErrorResponse;
import com.saber.camel.spring_jpa_test.entities.Person;
import com.saber.camel.spring_jpa_test.exceptions.ResourceNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FindByIdPersonRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();

        rest("/person")
                .get("findById/{id}")
                .id(Routes.FIND_BY_ID_PERSON)
                .description("Find All Person")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage().responseModel(PersonResponseDto.class).endResponseMessage()
                .responseMessage().code(406).responseModel(ServerErrorResponse.class).endResponseMessage()
                .enableCORS(true)
                .param().type(RestParamType.path).name("id").defaultValue("1").example("1").required(true).endParam()
                .bindingMode(RestBindingMode.json)
                .route()
                .routeId(Routes.FIND_BY_ID_PERSON)
                .threads(50, 80, Routes.FIND_BY_ID_PERSON).maxQueueSize(3000).allowCoreThreadTimeOut(true)
                .to("direct:find-person-by-id");


        from("direct:find-person-by-id")
                .log("Request for FIND_BY_ID_PERSON with id ==> ${in.header.id}")
                .toD("jpa://" + Person.class.getName() + "?query=select p from " + Person.class.getName() + " p where p.id = ${header.id}")
                .log("Response for FIND_BY_ID_PERSON ===> ${in.body}")
                .process(exchange -> {
                    List<Person> personList = (List<Person>) exchange.getMessage().getBody();
                    String id = exchange.getMessage().getHeader("id", String.class);
                    Person person;
                    if (personList.isEmpty()) {
                        throw new ResourceNotFoundException(Person.class.getName() + " with id : " + id + " not found");
                    }
                    person = personList.get(0);
                    exchange.getMessage().setBody(person);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
