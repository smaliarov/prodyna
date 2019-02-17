package com.smaliarov.prodyna.person.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Person output")
public class PersonOutput {
    @ApiModelProperty(notes = "Unique identifier of a person.", example = "0e9edd32-b067-405e-bf78-980c879bcd0c", required = true)
    private String id;
    @ApiModelProperty(notes = "Name.", example = "Sergii", required = true)
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
