package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
class SampleFunctionalTest {

    @Steps
    ApiSteps apiSteps;

    @Test
    void testHealth() {
        apiSteps.getHealth();
    }
}
