package org.jeuxdemots.model.api.graph;


import java.util.Collection;

public interface JeuxDeMots {

    Iterable<JDMNode> nodeIterable();
    Iterable<JDMNode> nodeIterable(NodeType nodeType);

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
