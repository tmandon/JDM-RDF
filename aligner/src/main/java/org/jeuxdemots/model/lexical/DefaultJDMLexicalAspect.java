package org.jeuxdemots.model.lexical;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.*;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultJDMLexicalAspect implements JDMLexicalAspect {

    private final JeuxDeMots jeuxDeMots;
    private final Map<MutableInt, JDMLexicalSense> senseIndex;

    public DefaultJDMLexicalAspect(final JeuxDeMots jeuxDeMots) {
        this.jeuxDeMots = jeuxDeMots;
        senseIndex = new HashMap<>();
    }

    @Override
    public Iterable<JDMLexicalEntry> lexicalEntries() {
        return () -> new JDMLexicalEntryIterator(jeuxDeMots, this);
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
        final Map<JDMRelationType, List<JDMRelation>> relationTypeMap = outgoingRelationMap(node);
        final List<JDMNode> senseNodes = getSenseNodes(relationTypeMap);
        if (!senseNodes.isEmpty()) {
            final String pos = getPOS(relationTypeMap);
            final List<JDMLexicalSense> lexicalSenses = lexicalSensesFromNodes(senseNodes);
            lexicalEntry = new DefaultJDMLexicalEntry(node, pos, lexicalSensesFromNodes(senseNodes));
            for (final JDMLexicalSense jdmLexicalSense : lexicalSenses) {
                senseIndex.put(jdmLexicalSense.getId(), jdmLexicalSense);
            }
        }
        return lexicalEntry;
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


    private Map<JDMRelationType, List<JDMRelation>> outgoingRelationMap(final JDMNode node) {
        final Collection<JDMRelation> relations = jeuxDeMots.getOutgoingRelations(node);
        return (relations.isEmpty())
                ? Collections.emptyMap()
                : relations
                .stream()
                .filter(Objects::nonNull)
                .filter(jdmRelation -> jdmRelation.getType() != null)
                .collect(Collectors.groupingBy(JDMRelation::getType));
    }

    private List<JDMNode> getSenseNodes(final Map<JDMRelationType, List<JDMRelation>> relationTypeMap) {
        final JDMRelationType relationType = jeuxDeMots.findType("r_raff_sem");
        final List<JDMRelation> senses = relationTypeMap.get(relationType);
//        relationTypeMap.remove(relationType);

        return ((senses != null) && !senses.isEmpty()) ? senses
                .stream()
                .map(jdmRelation -> jeuxDeMots.getNode(jdmRelation
                        .getTargetId()
                        .intValue()))
                .filter(node -> node.getName() != null)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    private String getPOS(final Map<JDMRelationType, List<JDMRelation>> relationTypeMap) {
        final JDMRelationType relationType = jeuxDeMots.findType("r_pos");
        List<JDMRelation> POS = relationTypeMap.get(relationType);
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
    public Iterable<JDMNode> nodeIterable() {
        return jeuxDeMots.nodeIterable();
    }

    @Override
    public Iterable<JDMNode> nodeIterable(final NodeType nodeType) {
        return jeuxDeMots.nodeIterable(nodeType);
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


    private static class JDMLexicalEntryIterator implements Iterator<JDMLexicalEntry> {


        final Iterator<JDMNode> termIterator;
        private JDMLexicalEntry currentEntry;
        JDMLexicalAspect lexicalAspect;
        boolean nextChecked;

        JDMLexicalEntryIterator(final JeuxDeMots jeuxDeMots, final JDMLexicalAspect lexicalAspect) {
            this.lexicalAspect = lexicalAspect;
            termIterator = jeuxDeMots
                    .nodeIterable(NodeType.TERM)
                    .iterator();
        }


        @Override
        public boolean hasNext() {
            findNextEntry();
            nextChecked = true;
            return currentEntry != null;
        }

        private void findNextEntry() {
            JDMLexicalEntry currentEntry = null;
            while (termIterator.hasNext() && (currentEntry == null)) {
                final JDMNode termNode = termIterator.next();
                currentEntry = lexicalAspect.nodeToLexicalEntry(termNode);
            }
            this.currentEntry = currentEntry;
        }

        @Override
        public JDMLexicalEntry next() {
            if (!nextChecked) {
                findNextEntry();
            }
            return currentEntry;
        }
    }
}
