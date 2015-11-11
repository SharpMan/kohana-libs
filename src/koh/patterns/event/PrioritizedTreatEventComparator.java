package koh.patterns.event;

import java.util.Comparator;

class PrioritizedTreatEventComparator  implements Comparator<PrioritizedTreatEvent> {

    @Override
    public int compare(PrioritizedTreatEvent prioritizedLeft, PrioritizedTreatEvent prioritizedRight) {
        return prioritizedLeft.priority.ordinal() > prioritizedRight.priority.ordinal() ?
                1 : -1;
    }

}