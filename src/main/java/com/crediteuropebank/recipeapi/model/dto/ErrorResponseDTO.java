package com.crediteuropebank.recipeapi.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponseDTO {
	private int errorCode;
	private String errorMessage;
}
