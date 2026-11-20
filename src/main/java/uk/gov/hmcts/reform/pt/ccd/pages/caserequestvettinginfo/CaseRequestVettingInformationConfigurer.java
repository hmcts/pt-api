package uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.common.PageConfigurer;

@Component
@AllArgsConstructor
public class CaseRequestVettingInformationConfigurer implements PageConfigurer {
    @Override
    public void configurePages(PageBuilder pageBuilder) {
        pageBuilder
            .add(new RequestVettingInfoWhatInfoNeededPage())
            .add(new RequestVettingInfoPreviewVettingInfoPage());
    }
}

