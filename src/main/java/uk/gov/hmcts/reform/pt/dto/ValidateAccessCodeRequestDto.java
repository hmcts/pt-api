package uk.gov.hmcts.reform.pt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateAccessCodeRequestDto {

    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9]{12}")
    private String accessCode;
}
