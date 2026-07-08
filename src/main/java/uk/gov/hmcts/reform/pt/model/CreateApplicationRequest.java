package uk.gov.hmcts.reform.pt.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@NotBlank
public class CreateApplicationRequest {
    @NotBlank
    private UUID userId;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(
        regexp = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$",
        message = "Invalid UK postcode format"
    )
    private String postcode;
    @NotBlank
    private String applicationType;
}
