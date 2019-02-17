package com.smaliarov.prodyna.person.web.controller;

import com.smaliarov.prodyna.person.listener.PersonChangeListener;
import com.smaliarov.prodyna.person.model.Person;
import com.smaliarov.prodyna.person.repository.PersonRepository;

import com.smaliarov.prodyna.person.web.dto.PersonInput;
import com.smaliarov.prodyna.person.web.dto.PersonOutput;
import com.smaliarov.prodyna.person.web.dto.PersonPatchInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("persons")
@Api(value = "PersonService", description = "REST service which offers CRUD for an entity of type Person")
public class PersonController {
    private final ModelMapper modelMapper = new ModelMapper();
    private final PersonRepository personRepository;
    private final List<PersonChangeListener> personChangeListeners;

    public PersonController(PersonRepository personRepository, List<PersonChangeListener> personChangeListeners) {
        this.personRepository = personRepository;
        this.personChangeListeners = personChangeListeners;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Creates a Person")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Created successfully"),
            @ApiResponse(code = 400, message = "Validation failed") })
    PersonOutput create(@Valid @RequestBody PersonInput input) {
        Person person = convert(input);
        person = personRepository.save(person);

        notifyListeners(person, PersonChangeListener.Action.CREATE);

        return convert(person);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("Reads all Persons")
    List<PersonOutput> read() {
        return personRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Reads a Person by their id")
    PersonOutput read(@PathVariable("id") String id) {
        return convert(find(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ApiOperation("Updates Person's data")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated successfully"),
            @ApiResponse(code = 400, message = "Validation failed"), @ApiResponse(code = 404, message = "Person not found") })
    PersonOutput update(@PathVariable("id") String id, @Valid @RequestBody PersonPatchInput input) {
        Person person = find(id);
        person.setName(input.getName());
        person = personRepository.save(person);

        notifyListeners(person, PersonChangeListener.Action.UPDATE);

        return convert(person);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Deletes a Person by their id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") String id) {
        personRepository.findById(id).ifPresent(this::delete);
    }

    private void delete(Person person) {
        personRepository.delete(person);
        notifyListeners(person, PersonChangeListener.Action.DELETE);
    }

    private Person find(String id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find Person with id " + id));
    }

    private PersonOutput convert(Person person) {
        return modelMapper.map(person, PersonOutput.class);
    }

    private Person convert(PersonInput person) {
        return modelMapper.map(person, Person.class);
    }

    private void notifyListeners(Person person, PersonChangeListener.Action action) {
        personChangeListeners.forEach(personChangeListener -> personChangeListener.onChange(person.getId(), action));
    }
}
