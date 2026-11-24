package uk.gov.hmcts.reform.pt.entity.converter;

import jakarta.persistence.Converter;
import uk.gov.hmcts.reform.pt.ccd.domain.VettingInformationType;

@Converter
public class VettingInformationTypeSetConverter extends EnumSetConverter<VettingInformationType> {
    protected VettingInformationTypeSetConverter() {
        super(VettingInformationType.class);
    }
}
