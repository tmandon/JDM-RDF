package org.jeuxdemots.model.graph;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.*;

public class InMemoryJeuxDeMotsFactory implements JeuxDeMotsFactory {
    @Override
    public JDMNode createNode(final int id, final String name, final int nodeType, final double weight) {
        return new DefaultJDMNode(new MutableInt(id), name, nodeType, new MutableDouble(weight));
    }

    @Override
    public JDMRelation createRelation(final int id, final int sourceId, final int targetId, final JDMRelationType type, final double weight) {
        return new DefaultJDMRelation(new MutableInt(id),new MutableInt(sourceId),new MutableInt(targetId),type,new MutableDouble(weight));
    }

    @Override
    public JDMRelationType createRelationType(final int id, final String name, final String extendedName, final String info) {
        return new DefaultJDMRelationType(new MutableInt(id),name,extendedName,info);
    }

    @Override
    public NodeContainer createNodeContainer(final int numberOfNodes) {
        return new InMemoryNodeContainer(numberOfNodes);
    }

    @Override
    public RelationContainer createRelationContainer(final int numberOfRelations, final int numberOfNodes) {
        return new InMemoryRelationContainer(numberOfRelations,numberOfNodes);
    }

    @Override
    public JeuxDeMots createJeuxDeMots(final NodeContainer jdmNodes, final RelationContainer jdmRelations) {
        return new InMemoryJeuxDeMots(jdmNodes,jdmRelations);
    }
}
