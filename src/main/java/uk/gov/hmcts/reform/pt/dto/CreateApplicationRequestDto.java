package uk.gov.hmcts.reform.pt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;

@Getter
@Builder
public class CreateApplicationRequestDto {
    @NotBlank
    private String applicantFirstName;
    @NotBlank
    private String applicantLastName;
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(
        regexp = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$",
        message = "Invalid UK postcode format"
    )
    private String postcode;
    @NotNull
    private ApplicationType applicationType;
}
