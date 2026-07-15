package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.annotations.Steps;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.steps.BaseApi;

@ExtendWith(SerenityJUnit5Extension.class)
@Tag("Functional")
class SampleFunctionalTest extends BaseApi {

    @Steps
    ApiSteps apiSteps;

    @Test
    void testHealth() {
        apiSteps.getHealth();
    }
}
