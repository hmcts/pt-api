package uk.gov.hmcts.reform.pt.ccd;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.CaseView;
import uk.gov.hmcts.ccd.sdk.CaseViewRequest;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

/**
 * Invoked by CCD to load PT cases under the decentralised model.
 */
@Component
@AllArgsConstructor
public class PTCaseView implements CaseView<PTCase, State> {
    @Override
    public PTCase getCase(CaseViewRequest<State> request) {
        return PTCase.builder().build();
    }
}
