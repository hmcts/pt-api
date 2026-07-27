package uk.gov.hmcts.reform.pt.ccd.event;

import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.ccd.sdk.ConfigBuilderImpl;
import uk.gov.hmcts.ccd.sdk.ResolvedCCDConfig;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.Start;
import uk.gov.hmcts.ccd.sdk.api.callback.Submit;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseEventTest {
    protected static final long TEST_CASE_REFERENCE = 1234L;

    protected Event<PTCase, UserRole, State> event;
    private final ConfigBuilderImpl<PTCase, State, UserRole> configBuilder = createConfigBuilder();

    protected PTCase callStartHandler(PTCase caseData) {
        EventPayload<PTCase, State> eventPayload = new EventPayload<>(TEST_CASE_REFERENCE, caseData, null);
        Start<PTCase, State> startHandler = event.getStartHandler();
        return startHandler.start(eventPayload);
    }

    protected SubmitResponse<State> callSubmitHandler(PTCase caseData) {
        EventPayload<PTCase, State> eventPayload = new EventPayload<>(TEST_CASE_REFERENCE, caseData, null);
        Submit<PTCase, State> submitHandler = event.getSubmitHandler();
        return submitHandler.submit(eventPayload);
    }

    protected void configureEvent(CCDConfig<PTCase, State, UserRole> eventConfig) {
        eventConfig.configureDecentralised(configBuilder);
        ResolvedCCDConfig<PTCase, State, UserRole> resolvedCCDConfig = configBuilder.build();
        Collection<Event<PTCase, UserRole, State>> events = resolvedCCDConfig.getEvents().values();
        assertThat(events)
            .withFailMessage("There should be exactly 1 event configured")
            .hasSize(1);
        event = events.iterator().next();
    }

    private ConfigBuilderImpl<PTCase, State, UserRole> createConfigBuilder() {
        ResolvedCCDConfig<PTCase, State, UserRole> initialCCDConfig
            = new ResolvedCCDConfig<>(PTCase.class, State.class, UserRole.class, Map.of(), ImmutableSet.of());
        return new ConfigBuilderImpl<>(initialCCDConfig);
    }
}
