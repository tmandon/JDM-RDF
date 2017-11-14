package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.graph.DefaultJDMNode;

import java.util.Collections;
import java.util.List;

public class DefaultJDMLexicalEntry extends DefaultJDMNode implements JDMLexicalEntry {

    private final String posTag;
    private final List<JDMLexicalSense> senses;


    DefaultJDMLexicalEntry(final JDMNode node, final String posTag, final List<JDMLexicalSense> senses) {
        super(node.getId(), node.getName(), node.getNodeType(), node.getWeight());
        this.posTag = posTag;
        this.senses = Collections.unmodifiableList(senses);
    }

    @Override
    public String getPosTag() {
        return posTag;
    }


    @Override
    public List<JDMLexicalSense> getSenses() {
        return Collections.unmodifiableList(senses);
    }
}
