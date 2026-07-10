package uk.gov.hmcts.reform.pt.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateApplicationRequestDtoTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"SW1A 1AA", "SW1A1AA", "M1 1AE", "B33 8TH", "CR2 6XH", "DN55 1PT"})
    void shouldHaveNoViolationsForValidPostcode(String postcode) {
        CreateApplicationRequestDto request = applicationRequestWithPostcode(postcode);

        Set<ConstraintViolation<CreateApplicationRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "12345", "sw1a 1aa", "SW1A 1AAA", ""})
    void shouldHaveViolationForInvalidPostcode(String postcode) {
        CreateApplicationRequestDto request = applicationRequestWithPostcode(postcode);

        Set<ConstraintViolation<CreateApplicationRequestDto>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("postcode");
    }

    private static CreateApplicationRequestDto applicationRequestWithPostcode(String postcode) {
        return CreateApplicationRequestDto.builder()
            .applicantFirstName("John")
            .applicantLastName("Smith")
            .email("john.smith@example.com")
            .postcode(postcode)
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .build();
    }
}