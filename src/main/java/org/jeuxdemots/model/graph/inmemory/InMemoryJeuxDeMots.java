package org.jeuxdemots.model.graph.inmemory;

import org.jeuxdemots.model.api.graph.*;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InMemoryJeuxDeMots implements JeuxDeMots {

    private final NodeContainer jdmNodes;
    private final RelationContainer jdmRelations;

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    InMemoryJeuxDeMots(final NodeContainer jdmNodes, final RelationContainer jdmRelations) {
        this.jdmNodes = jdmNodes;
        this.jdmRelations = jdmRelations;
    }

    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        jdmNodes.forEach(consumer);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        jdmNodes.typedIterable(nodeType).forEach(consumer);
    }

    @Override
    public JDMNode getNode(final String name) {
        return null;
    }

    @Override
    public JDMNode getNode(final int id) {
        return jdmNodes.get(id-1);
    }

    @Override
    public JDMRelationType findType(final int id) {
            return jdmRelations.get(id-1).getType();
    }

    @Override
    public JDMRelationType findType(final String name) {
        return jdmRelations.findType(name);
    }

    @Override
    public JDMRelation getRelation(final int id) {
        return jdmRelations.get(id-1);
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        return jdmRelations.incomingRelations(target).stream().filter(jdmRelation -> jdmRelation.getType() == type).collect(Collectors.toList());
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        return jdmRelations.outgoingRelations(source).stream().filter(jdmRelation -> jdmRelation.getType() == type).collect(Collectors.toList());
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return jdmRelations.incomingRelations(target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return jdmRelations.outgoingRelations(source);
    }

    @Override
    public JDMNode getRelationSource(final JDMRelation relation) {
        return jdmNodes.get(relation.getSourceId().intValue()-1);
    }

    @Override
    public JDMNode getRelationTarget(final JDMRelation relation) {
        return jdmNodes.get(relation.getTargetId().intValue()-1);
    }
}
