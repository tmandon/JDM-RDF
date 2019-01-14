package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.graph.JDMRelationType;
import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.graph.DefaultJDMNode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultJDMLexicalEntry extends DefaultJDMNode implements JDMLexicalEntry {

    private final String posTag;
    private final List<JDMLexicalSense> senses;
    private final Map<JDMRelationType, List<JDMRelation>> incomingRelations;
    private final Map<JDMRelationType, List<JDMRelation>> outgoingRelations;
    private final WeakReference<JeuxDeMots> jeuxDeMotsWeakReference;


    DefaultJDMLexicalEntry(final JeuxDeMots jeuxDeMots, final JDMNode node, final String posTag, final List<JDMLexicalSense> senses,
                           final Map<JDMRelationType, List<JDMRelation>> incomingRelations,
                           final Map<JDMRelationType, List<JDMRelation>> outgoingRelations) {
        super(node.getId(), node.getName(), node.getNodeType(), node.getWeight());
        this.senses = Collections.unmodifiableList(senses);
        this.incomingRelations = Collections.unmodifiableMap(incomingRelations);
        this.outgoingRelations = Collections.unmodifiableMap(outgoingRelations);
        jeuxDeMotsWeakReference = new WeakReference<>(jeuxDeMots);
        this.posTag = posTag;
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
