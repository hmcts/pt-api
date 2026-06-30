package uk.gov.hmcts.reform.pt.ccd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.ConfigBuilderImpl;
import uk.gov.hmcts.ccd.sdk.api.Search;
import uk.gov.hmcts.ccd.sdk.api.SearchCases;
import uk.gov.hmcts.ccd.sdk.api.Tab.TabBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseTypeTest {

    @InjectMocks
    private CaseType caseType;

    @Mock
    private ConfigBuilderImpl<PTCase, State, UserRole> builder;

    @Mock
    private Search.SearchBuilder<PTCase, UserRole> searchBuilder;

    @Mock
    private SearchCases.SearchCasesBuilder<PTCase> searchCasesBuilder;

    @Mock
    private TabBuilder<PTCase, UserRole> tabBuilder;

    @AfterEach
    void tearDown() {
        System.clearProperty("CASE_TYPE_SUFFIX");
    }

    @Test
    void shouldGetCaseType() {
        String result = CaseType.getCaseType();

        assertThat(result).isEqualTo("PT");
    }

    @Test
    void shouldGetCaseTypeName() {
        String result = CaseType.getCaseTypeName();

        assertThat(result).isEqualTo("Possession");
    }

    @Test
    void shouldConfigureCaseType() {
        when(builder.searchInputFields()).thenReturn(searchBuilder);
        when(builder.searchCasesFields()).thenReturn(searchCasesBuilder);
        when(builder.searchResultFields()).thenReturn(searchBuilder);
        when(builder.workBasketInputFields()).thenReturn(searchBuilder);
        when(builder.workBasketResultFields()).thenReturn(searchBuilder);
        when(builder.tab(anyString(), anyString())).thenReturn(tabBuilder);

        caseType.configure(builder);

        verify(builder).caseType(
            eq("PT"),
            eq("Possession"),
            eq("Possession Case Type")
        );

        verify(builder).jurisdiction(
            eq("PT"),
            eq("Civil Possession"),
            eq("Civil Possession Jurisdiction")
        );

        verify(builder).searchInputFields();
        verify(builder).searchCasesFields();
        verify(builder).searchResultFields();
        verify(builder).workBasketInputFields();
        verify(builder).workBasketResultFields();

        verify(builder).tab("Example", "Example Tab");
    }
}
