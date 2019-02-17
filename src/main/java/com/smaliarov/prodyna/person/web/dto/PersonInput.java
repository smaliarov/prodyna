package com.smaliarov.prodyna.person.web.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

@ApiModel(description = "Person input for creating a new record")
public class PersonInput {
    @Size(min = 1, max = 36)
    @ApiModelProperty(notes = "Unique identifier of a person. Has a length of 1 to 36 characters.", example = "0e9edd32-b067-405e-bf78-980c879bcd0c", required = true)
    private String id;
    @Size(min = 3, max = 20)
    @ApiModelProperty(notes = "Name. Has a length of 3 to 20 characters.", example = "Sergii", required = true)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
