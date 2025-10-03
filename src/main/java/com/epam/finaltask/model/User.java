package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String password;

    private Role role;

	@OneToMany(mappedBy = "user")
    private List<Voucher> vouchers;

    private String phoneNumber;

    private BigDecimal balance;

    private boolean active;
    
}