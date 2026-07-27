package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

@Service
@RequiredArgsConstructor
public class TenancyDetailsService {

    private final TenancyDetailsRepository tenancyDetailsRepository;

    @Transactional
    public TenancyDetailsEntity getTenancyDetailsOrCreateIfNotExists(TenancyType tenancyType) {
        return tenancyDetailsRepository.findFirstByTenancyType(tenancyType)
            .orElseGet(() -> createTenancyDetails(tenancyType));
    }

    @Transactional
    public TenancyDetailsEntity createTenancyDetails(TenancyType tenancyType) {
        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tenancyType(tenancyType)
            .build();
        return tenancyDetailsRepository.save(tenancyDetails);
    }
}
