package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;

import java.util.List;

public interface JDMLexicalEntry extends JDMNode {
    String getPosTag();
    List<JDMLexicalSense> getSenses();
}
