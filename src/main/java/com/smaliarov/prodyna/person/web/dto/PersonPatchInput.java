package com.smaliarov.prodyna.person.web.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ApiModel(description = "Person input for updating an existing record")
public class PersonPatchInput {
    @Size(min = 3, max = 20)
    @ApiModelProperty(notes = "Name. Has a length of 3 to 20 characters.", example = "Sergii", required = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}