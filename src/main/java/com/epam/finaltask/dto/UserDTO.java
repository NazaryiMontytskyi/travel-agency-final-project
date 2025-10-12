package com.epam.finaltask.dto;

import java.util.List;

import com.epam.finaltask.model.Voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private String id;

	@NotNull
	@NotBlank
	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	private String role;

	private List<Voucher> vouchers;

	@NotBlank
	private String phoneNumber;

	private Double balance;

	private boolean active;
	
}
