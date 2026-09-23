package uk.gov.hmcts.reform.pt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationSummaryMapper {

    @Mapping(target = "caseReference", source = "caseReference")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "submittedOn", source = "submittedDate")
    ApplicationDto toDto(ApplicationSummary summary);

    List<ApplicationDto> toDtos(List<ApplicationSummary> summaries);
}
