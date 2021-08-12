package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.PersonDto;
import com.saber.camel.spring_jpa_test.entities.Person;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SavePersonRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {
        super.configure();

        rest("/person")
                .post("/save")
                .id(Routes.SAVE_PERSON)
                .description("add new Person")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage().responseModel(Person.class).endResponseMessage()
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .type(PersonDto.class)
                .route()
                .routeId(Routes.SAVE_PERSON)
                .threads(50,80,Routes.SAVE_PERSON).maxQueueSize(3000).allowCoreThreadTimeOut(true)
                .log("Request for add person with body ${in.body}")
                .process(exchange -> {
                    PersonDto personDto = exchange.getMessage().getBody(PersonDto.class);

                    Person person = new Person();
                    person.setFirstName(personDto.getFirstName());
                    person.setLastName(personDto.getLastName());
                    person.setAge(personDto.getAge());
                    exchange.getMessage().setBody(person);

                })
                .to("jpa:"+Person.class.getName()+"?usePersist=true")
                .log("Response for add new Person ===> ${in.body}");
    }
}
