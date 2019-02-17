package com.smaliarov.prodyna.person.integration;

import com.smaliarov.prodyna.person.web.dto.PersonInput;
import com.smaliarov.prodyna.person.web.dto.PersonOutput;
import com.smaliarov.prodyna.person.web.dto.PersonPatchInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonServiceTest extends AbstractTestNGSpringContextTests {
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private final String personId = UUID.randomUUID().toString();
    private final String name = "Sergii";
    private final String shortName = "SM";
    private final String longName = "Sergii Georgiiovich Maliarov";
    private final String updatedName = "Maliarov";

    public PersonServiceTest() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);

        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }

    @Test(groups = "init")
    public void readAllEmpty() {
        ResponseEntity<List> entity = this.restTemplate.getForEntity(getUrl(), List.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualTo(Collections.emptyList());
    }

    @Test(groups = "init")
    public void readNonExisting() {
        ResponseEntity<Object> entity = this.restTemplate.getForEntity(getUrl(personId), Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test(groups = "create", dependsOnGroups = "init")
    public void createTooShortName() {
        PersonInput input = new PersonInput();
        input.setId(personId);
        input.setName(shortName);

        ResponseEntity<Object> entity = this.restTemplate.postForEntity(getUrl(), input, Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test(groups = "create", dependsOnGroups = "init")
    public void createTooLongName() {
        PersonInput input = new PersonInput();
        input.setId(personId);
        input.setName(longName);

        ResponseEntity<Object> entity = this.restTemplate.postForEntity(getUrl(), input, Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test(groups = "create", dependsOnGroups = "init")
    public void createEmptyId() {
        PersonInput input = new PersonInput();
        input.setId("");
        input.setName(shortName);

        ResponseEntity<Object> entity = this.restTemplate.postForEntity(getUrl(), input, Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test(groups = "create", dependsOnGroups = "init")
    public void create() {
        PersonInput input = new PersonInput();
        input.setId(personId);
        input.setName(name);

        ResponseEntity<PersonOutput> entity = this.restTemplate.postForEntity(getUrl(), input, PersonOutput.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(entity.getBody()).isEqualToComparingFieldByField(createdPerson());
    }

    @Test(groups = "afterCreate", dependsOnGroups = "create")
    public void readById() {
        ResponseEntity<PersonOutput> entity = this.restTemplate.getForEntity(getUrl(personId), PersonOutput.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualToComparingFieldByField(createdPerson());
    }

    @Test(groups = "afterCreate", dependsOnGroups = "create")
    public void readAll() {
        ResponseEntity<List<PersonOutput>> entity = this.restTemplate.exchange(getUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<PersonOutput>>() {});
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<PersonOutput> result = entity.getBody();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualToComparingFieldByField(createdPerson());
    }

    @Test(groups = "create", dependsOnGroups = "init")
    public void createDuplicate() {
        create();
    }

    @Test(groups = "update", dependsOnGroups = "afterCreate")
    public void updateNonExisting() throws URISyntaxException {
        PersonPatchInput input = new PersonPatchInput();
        input.setName(updatedName);

        ResponseEntity<Object> entity = this.restTemplate.exchange(
                new RequestEntity<>(input, HttpMethod.PATCH, new URI(getUrl(UUID.randomUUID().toString()))), Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test(groups = "update", dependsOnGroups = "afterCreate")
    public void updateNameTooShort() throws URISyntaxException {
        PersonPatchInput input = new PersonPatchInput();
        input.setName(shortName);

        ResponseEntity<Object> entity = this.restTemplate.exchange(
                new RequestEntity<>(input, HttpMethod.PATCH, new URI(getUrl(personId))), Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test(groups = "update", dependsOnGroups = "afterCreate")
    public void updateNameTooLong() throws URISyntaxException {
        PersonPatchInput input = new PersonPatchInput();
        input.setName(longName);

        ResponseEntity<Object> entity = this.restTemplate.exchange(
                new RequestEntity<>(input, HttpMethod.PATCH, new URI(getUrl(personId))), Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test(groups = "update", dependsOnGroups = "afterCreate")
    public void update() throws URISyntaxException {
        PersonPatchInput input = new PersonPatchInput();
        input.setName(updatedName);

        ResponseEntity<PersonOutput> entity = this.restTemplate.exchange(
                new RequestEntity<>(input, HttpMethod.PATCH, new URI(getUrl(personId))), PersonOutput.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualToComparingFieldByField(updatedPerson());
    }

    @Test(groups = "afterUpdate", dependsOnGroups = "update")
    public void readUpdatedById() {
        ResponseEntity<PersonOutput> entity = this.restTemplate.getForEntity(getUrl(personId), PersonOutput.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualToComparingFieldByField(updatedPerson());
    }

    @Test(groups = "delete", dependsOnGroups = "afterUpdate")
    public void delete() {
        ResponseEntity<Object> entity = this.restTemplate.exchange(getUrl(personId), HttpMethod.DELETE, null, Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test(groups = "delete", dependsOnGroups = "afterUpdate")
    public void deleteNonExisting() {
        ResponseEntity<Object> entity = this.restTemplate.exchange(getUrl(UUID.randomUUID().toString()), HttpMethod.DELETE, null, Object.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test(groups = "teardown", dependsOnGroups = "delete")
    public void readAllEmptyAgain() {
        readAllEmpty();
    }

    private String getUrl() {
        return getUrl("");
    }

    private String getUrl(String suffix) {
        String url = "http://localhost:" + port + "/persons";
        if (!suffix.isEmpty()) {
            url = url + "/" + suffix;
        }
        return url;
    }

    private PersonOutput createdPerson() {
        PersonOutput createdPerson = new PersonOutput();
        createdPerson.setId(personId);
        createdPerson.setName(name);
        return createdPerson;
    }

    private PersonOutput updatedPerson() {
        PersonOutput updatedPerson = new PersonOutput();
        updatedPerson.setId(personId);
        updatedPerson.setName(updatedName);
        return updatedPerson;
    }
}
