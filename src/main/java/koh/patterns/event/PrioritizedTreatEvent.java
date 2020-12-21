package koh.patterns.event;

import koh.patterns.event.api.EventTreatmentPriority;
import koh.patterns.event.api.TreatEvent;

class PrioritizedTreatEvent {
    final EventTreatmentPriority priority;
    final TreatEvent<Event> treatment;

    public PrioritizedTreatEvent(EventTreatmentPriority priority, TreatEvent<Event> treatment) {
        this.priority = priority;
        this.treatment = treatment;
    }
}