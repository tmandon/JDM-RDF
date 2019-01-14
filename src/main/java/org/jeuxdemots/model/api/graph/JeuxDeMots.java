package org.jeuxdemots.model.api.graph;


import java.util.Collection;
import java.util.function.Consumer;

public interface JeuxDeMots {

    void forEachNode(final Consumer<JDMNode> consumer);
    void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType);

    JDMNode getNode(String name);
    JDMNode getNode(int id);

    JDMRelationType findType(int id);
    JDMRelationType findType(String name);
    JDMRelation getRelation(int id);
    Collection<JDMRelation> getIncomingRelations(JDMRelationType type, JDMNode target);
    Collection<JDMRelation> getOutgoingRelations(JDMRelationType type, JDMNode source);

    Collection<JDMRelation> getIncomingRelations(JDMNode target);
    Collection<JDMRelation> getOutgoingRelations(JDMNode source);

    JDMNode getRelationSource(JDMRelation relation);
    JDMNode getRelationTarget(JDMRelation relation);

}
