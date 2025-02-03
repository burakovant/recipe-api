package com.crediteuropebank.recipeapi.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Status Api model documentation")
public class StatusDTO {
    @ApiModelProperty(value = "Only activation and deactivation is allowed.", required = true, example = "true")
    private Boolean active;
}
