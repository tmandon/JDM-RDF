package org.jeuxdemots.model.lexical;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultJDMLexicalAspect implements JDMLexicalAspect {

    private final JDMRelationType raffSemType;
    private final JeuxDeMots jeuxDeMots;
    private final Map<MutableInt, JDMLexicalSense> senseIndex;

    public DefaultJDMLexicalAspect(final JeuxDeMots jeuxDeMots) {
        this.jeuxDeMots = jeuxDeMots;
        senseIndex = new HashMap<>();
        raffSemType = jeuxDeMots.findType("r_raff_sem");
    }

    @Override
    public void forEachLexicalEntry(final Consumer<JDMLexicalEntry> consumer) {
        jeuxDeMots.forEachNodeOfType(node -> {
                    final JDMLexicalEntry lexicalEntry = nodeToLexicalEntry(node);
                    if (lexicalEntry != null) {
                        consumer.accept(lexicalEntry);
                    }
                },
                NodeType.TERM);
    }

    /**
     * Converts a JDMNode into a corresponding lexical entry, if the JDMNode corresponds to a lexical entry,
     * otherwise returns null.
     *
     * @return The corresponding lexical entry or null
     */
    @Override
    public JDMLexicalEntry nodeToLexicalEntry(final JDMNode node) {
        JDMLexicalEntry lexicalEntry = null;
        final Map<JDMRelationType, List<JDMRelation>> incomingRefinementRelations = relationMap(jeuxDeMots.getIncomingRelations(raffSemType, node));
        if (incomingRefinementRelations.isEmpty()) {
            final Map<JDMRelationType, List<JDMRelation>> outgoingRelationMap = relationMap(jeuxDeMots.getOutgoingRelations(node));
            final String[] posParts = getPOS(outgoingRelationMap).split(":");
            //If posParts[1] isn't empty, we have a derived form
            if ((posParts.length == 1) || posParts[1].isEmpty()) {
                final List<JDMNode> senseNodes = getSenseNodes(outgoingRelationMap);
                final List<JDMLexicalSense> lexicalSenses = lexicalSensesFromNodes(senseNodes);
                lexicalEntry = new DefaultJDMLexicalEntry(jeuxDeMots, node, "", lexicalSensesFromNodes(senseNodes),
                        incomingRefinementRelations, outgoingRelationMap);
                for (final JDMLexicalSense jdmLexicalSense : lexicalSenses) {
                    senseIndex.put(jdmLexicalSense.getId(), jdmLexicalSense);
                }
            } else {

            }
        }
        return lexicalEntry;
    }

    private String getPOS(final Map<JDMRelationType, List<JDMRelation>> outgoingRelations) {
        final JDMRelationType relationType = jeuxDeMots.findType("r_pos");
        List<JDMRelation> POS = outgoingRelations.get(relationType);
        if (POS == null) {
            POS = Collections.emptyList();
        }
        return (POS.isEmpty())
                ? ""
                : POS
                .stream()
                .map(jdmRelation -> jeuxDeMots.getNode(jdmRelation
                        .getTargetId()
                        .intValue()))
                .collect(Collectors.toList())
                .get(0)
                .getName();
    }

    @Override
    public JDMLexicalSense nodeToLexicalSense(final JDMNode node) {
        return senseIndex.get(node.getId());
    }

    @Override
    public Collection<JDMLexicalRelation> getLexicalRelations(final JDMLexicalEntry lexicalEntry) {
        final Collection<JDMRelation> entryRelation = jeuxDeMots.getOutgoingRelations(lexicalEntry);
        return entryRelation
                .stream()
                .filter(Objects::nonNull)
                .map(relation ->
                        new DefaultJDMLexicalRelation(relation, this)
                )
                .filter(jdmRelation -> (jdmRelation.getTargetEntry() != null) && (jdmRelation.getSourceEntry() != null))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<JDMSemanticRelation> getSemanticRelations(final JDMLexicalSense lexicalSense) {
        final Collection<JDMRelation> senseRelations = jeuxDeMots.getOutgoingRelations(lexicalSense);
        return senseRelations
                .stream()
                .map(relation ->
                        new DefaultJDMSemanticRelation(relation, this)
                )
                .collect(Collectors.toList());
    }

    private List<JDMLexicalSense> lexicalSensesFromNodes(final Collection<JDMNode> senseNodes) {
        return senseNodes
                .stream()
                .map((JDMNode node) -> new DefaultJDMLexicalSense(node, jeuxDeMots))
                .collect(Collectors.toList());
    }


    private Map<JDMRelationType, List<JDMRelation>> relationMap(final Collection<JDMRelation> relations) {
        return (relations.isEmpty())
                ? Collections.emptyMap()
                : relations
                .stream()
                .filter(Objects::nonNull)
                .filter(jdmRelation -> jdmRelation.getType() != null)
                .collect(Collectors.groupingBy(JDMRelation::getType));
    }

    private List<JDMNode> getSenseNodes(final Map<JDMRelationType, List<JDMRelation>> relationTypeMap) {
        final List<JDMRelation> senses = relationTypeMap.get(raffSemType);
//        relationTypeMap.remove(relationType);

        return ((senses != null) && !senses.isEmpty()) ? senses
                .stream()
                .map(jdmRelation -> jeuxDeMots.getNode(jdmRelation
                        .getTargetId()
                        .intValue()))
                .filter(node -> node.getName() != null)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        jeuxDeMots.forEachNode(consumer);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        jeuxDeMots.forEachNodeOfType(consumer, nodeType);
    }

    @Override
    public JDMNode getNode(final String name) {
        return jeuxDeMots.getNode(name);
    }

    @Override
    public JDMNode getNode(final int id) {
        return jeuxDeMots.getNode(id);
    }

    @Override
    public JDMRelationType findType(final int id) {
        return jeuxDeMots.findType(id);
    }

    @Override
    public JDMRelationType findType(final String name) {
        return jeuxDeMots.findType(name);
    }

    @Override
    public JDMRelation getRelation(final int id) {
        return jeuxDeMots.getRelation(id);
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        return jeuxDeMots.getIncomingRelations(type, target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        return jeuxDeMots.getOutgoingRelations(type, source);
    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return jeuxDeMots.getIncomingRelations(target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return jeuxDeMots.getOutgoingRelations(source);
    }

    @Override
    public JDMNode getRelationSource(final JDMRelation relation) {
        return jeuxDeMots.getRelationSource(relation);
    }

    @Override
    public JDMNode getRelationTarget(final JDMRelation relation) {
        return jeuxDeMots.getRelationTarget(relation);
    }
}
