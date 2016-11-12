package org.helioviewer.jhv.data.datatype.event;

import java.util.List;

/**
 * Defines a relationship between events.
 *
 * @author Bram Bourgoignie (Bram.Bourgoignie@oma.be)
 *
 */
public class SWEKRelatedEvents {

    private final SWEKEventType event;

    private final SWEKEventType relatedWith;

    private final List<SWEKRelatedOn> relatedOnList;

    public SWEKRelatedEvents(SWEKEventType _event, SWEKEventType _relatedWith, List<SWEKRelatedOn> _relatedOnList) {
        event = _event;
        relatedWith = _relatedWith;
        relatedOnList = _relatedOnList;
    }

    public SWEKEventType getEvent() {
        return event;
    }

    public SWEKEventType getRelatedWith() {
        return relatedWith;
    }

    public List<SWEKRelatedOn> getRelatedOnList() {
        return relatedOnList;
    }

}
