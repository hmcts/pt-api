package uk.gov.hmcts.reform.pt.feesandpay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.repository.ApplicationFeeRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.service.CasePartyService;
import uk.gov.hmcts.reform.pt.service.MarketRentCaseService;

import java.time.Clock;
import java.time.LocalDateTime;

import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.feesandpay.model.FeeType.APPLICATION_FEE;
import static uk.gov.hmcts.reform.pt.feesandpay.model.PaymentStatus.NOT_PAID;

@Service
@RequiredArgsConstructor
public class ApplicationFeeService {

    private final CasePartyService casePartyService;
    private final FeeService feeService;
    private final PaymentService paymentService;
    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseService marketRentCaseService;
    private final ApplicationFeeRepository applicationFeeRepository;
    private final Clock clock;

    @Transactional
    public ApplicationFeeEntity requestApplicationFee(long caseReference) {
        PTCaseEntity ptCase = ptCaseRepository.findByCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        MarketRentCaseEntity marketRentCase = marketRentCaseService.getOrCreateMarketRentCase(ptCase);

        ApplicationFeeEntity existing = marketRentCase.getApplicationFee();
        if (existing != null) {
            return existing;
        }

        String responsibleParty = casePartyService.getPartyForCaseByRole(ptCase, APPLICANT)
            .map(applicant -> applicant.getFirstName() + " " + applicant.getLastName())
            .orElseThrow(() -> new IllegalStateException("No applicant on case " + caseReference));

        FeeDetails fee = feeService.getFee(APPLICATION_FEE);
        String requestReference = paymentService.createServiceRequest(caseReference, fee, responsibleParty);

        ApplicationFeeEntity applicationFee = ApplicationFeeEntity.builder()
            .requestReference(requestReference)
            .requestDate(LocalDateTime.now(clock))
            .amount(fee.getFeeAmount())
            .status(NOT_PAID.getValue())
            .marketRentCase(marketRentCase)
            .build();
        marketRentCase.setApplicationFee(applicationFee);

        return applicationFeeRepository.save(applicationFee);
    }
}
