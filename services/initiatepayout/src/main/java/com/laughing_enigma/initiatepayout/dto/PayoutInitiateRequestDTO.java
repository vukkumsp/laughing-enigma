package com.laughing_enigma.initiatepayout.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutInitiateRequestDTO {

    @NotNull
    @Positive
    private Number amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String purpose;

    @Size(max = 256)
    private String transaction_description;

    @NotNull
    private BeneficiaryDetails beneficiary_details;

    public static class BeneficiaryDetails {
        @NotBlank
        private String name;
        @NotBlank
        private String type;
        @NotNull
        private DestinationDetails destination_details;
        @NotNull
        @Valid
        private Address address;
    }

    public static class DestinationDetails {
        @NotBlank
        private String type;
        @NotNull
        private Bank bank;
    }

    public static class Bank {
        @NotBlank
        private String account_number;
        @NotBlank
        private String bank_name;
        @NotBlank
        private String country;
        @NotBlank
        private String currency;
        @NotNull
        private BankCodes bank_codes;
    }

    public static class BankCodes {
        @NotBlank
        private String ifsc_code;
    }

    public static class Address {
        @NotBlank
        private String line1;
        @NotBlank
        private String city;
        @NotBlank
        private String state;
        @NotBlank
        private String country;
        @NotBlank
        private String postal_code;
    }
}
