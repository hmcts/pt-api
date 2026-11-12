package uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaseRequestVettingInformationConfigurerTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private PageBuilder pageBuilder;

    @Test
    void shouldAddPagesToPageBuilder() {
        CaseRequestVettingInformationConfigurer configurer = new CaseRequestVettingInformationConfigurer();

        configurer.configurePages(pageBuilder);

        verify(pageBuilder).add(isA(RequestVettingInfoWhatInfoNeededPage.class));
        verify(pageBuilder).add(isA(RequestVettingInfoPreviewVettingInfoPage.class));
    }
}
