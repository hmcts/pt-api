package uk.gov.hmcts.reform.pt.ccd.common;

import uk.gov.hmcts.ccd.sdk.api.Event.EventBuilder;
import uk.gov.hmcts.ccd.sdk.api.FieldCollection.FieldCollectionBuilder;
import uk.gov.hmcts.ccd.sdk.api.callback.MidEvent;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

public class PageBuilder {

    private final EventBuilder<PTCase, UserRole, State> eventBuilder;

    public PageBuilder(EventBuilder<PTCase, UserRole, State> eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    public FieldCollectionBuilder<PTCase, State, EventBuilder<PTCase, UserRole, State>> page(String id) {
        return eventBuilder.fields().page(id);
    }

    public FieldCollectionBuilder<PTCase, State, EventBuilder<PTCase, UserRole, State>> page(
        String id, MidEvent<PTCase, State> midEventCallback) {
        return eventBuilder.fields().page(id, midEventCallback);
    }

    public PageBuilder add(CcdPageConfiguration pageConfiguration) {
        pageConfiguration.buildPage(this);
        return this;
    }
}
