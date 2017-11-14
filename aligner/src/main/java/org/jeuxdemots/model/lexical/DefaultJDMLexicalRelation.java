package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMRelation;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelation;

public class DefaultJDMLexicalRelation extends DefaultJDMRelation implements JDMLexicalRelation {
    public DefaultJDMLexicalRelation(final JDMRelation relation, final JDMLexicalAspect lexicalAspect) {
        super(relation.getId(), relation.getSourceId(),relation.getTargetId(), relation.getType(), relation.getWeight());
        sourceEntry = lexicalAspect.nodeToLexicalEntry(lexicalAspect.getNode(relation.getSourceId().intValue()));
        targetEntry = lexicalAspect.nodeToLexicalEntry(lexicalAspect.getNode(relation.getTargetId().intValue()));
    }

    private final JDMLexicalEntry sourceEntry;

    private final JDMLexicalEntry targetEntry;

    @Override
    public JDMLexicalEntry getSourceEntry() {
        return sourceEntry;
    }

    @Override
    public JDMLexicalEntry getTargetEntry() {
        return targetEntry;
    }
}
