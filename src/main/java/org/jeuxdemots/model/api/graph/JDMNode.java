package org.jeuxdemots.model.api.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;

public interface JDMNode {
    MutableInt getId();

    String getName();

    MutableDouble getWeight();

    void incrementWeight(double value);

    void decrementWeight(double value);

    NodeType getNodeType();
}
