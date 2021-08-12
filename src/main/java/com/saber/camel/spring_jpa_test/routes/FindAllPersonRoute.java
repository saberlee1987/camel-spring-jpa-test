package com.saber.camel.spring_jpa_test.routes;

import com.saber.camel.spring_jpa_test.dto.PersonResponseDto;
import com.saber.camel.spring_jpa_test.entities.Person;
import org.apache.camel.Exchange;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FindAllPersonRoute extends AbstractRestRouteBuilder {
    @Override
    public void configure() throws Exception {

        super.configure();

        rest("/person")
                .get("findAll")
                .id(Routes.FIND_ALL_PERSON)
                .description("Find All Person")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .responseMessage().code(200).responseModel(PersonResponseDto.class).endResponseMessage()
                .enableCORS(true)
                .bindingMode(RestBindingMode.json)
                .route()
                .routeId(Routes.FIND_ALL_PERSON)
                .threads(50,80,Routes.FIND_ALL_PERSON).maxQueueSize(3000).allowCoreThreadTimeOut(true)
                .to("jpa://" + Person.class.getName() + "?resultClass=" + Person.class + "&namedQuery=Person.findAll")
                .process(exchange -> {
                    List<Person> personList = (List<Person>) exchange.getMessage().getBody();
                    PersonResponseDto responseDto = new PersonResponseDto();
                    responseDto.setPersons(personList);
                    exchange.getMessage().setBody(responseDto);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
