package org.jeuxdemots.model.api.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JeuxDeMots;

import java.util.Collection;
import java.util.function.Consumer;

public interface JDMLexicalAspect extends JeuxDeMots{
    void forEachLexicalEntry(Consumer<JDMLexicalEntry> consumer);
    JDMLexicalEntry nodeToLexicalEntry(final JDMNode node);

    JDMLexicalSense nodeToLexicalSense(JDMNode node);

    Collection<JDMLexicalRelation> getLexicalRelations(JDMLexicalEntry lexicalEntry);
    Collection<JDMSemanticRelation> getSemanticRelations(JDMLexicalSense lexicalSense);

}
