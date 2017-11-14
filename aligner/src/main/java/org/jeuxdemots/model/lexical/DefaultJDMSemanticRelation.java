package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.api.lexical.JDMSemanticRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelation;

public class DefaultJDMSemanticRelation extends DefaultJDMRelation implements JDMSemanticRelation {
    public DefaultJDMSemanticRelation(final JDMRelation relation, final JDMLexicalAspect lexicalAspect) {
        super(relation.getId(), relation.getSourceId(),relation.getTargetId(), relation.getType(), relation.getWeight());
        sourceSense = lexicalAspect.nodeToLexicalSense(lexicalAspect.getNode(relation.getSourceId().intValue()));
        targetSense = lexicalAspect.nodeToLexicalSense(lexicalAspect.getNode(relation.getTargetId().intValue()));
    }

    private final JDMLexicalSense sourceSense;

    private final JDMLexicalSense targetSense;

    @Override
    public JDMLexicalSense getSourceSense() {
        return sourceSense;
    }

    @Override
    public JDMLexicalSense getTargetSense() {
        return targetSense;
    }
}
