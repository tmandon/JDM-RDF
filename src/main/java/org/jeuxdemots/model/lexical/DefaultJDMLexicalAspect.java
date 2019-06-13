package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.api.lexical.JDMLexicalAspect;
import org.jeuxdemots.model.api.lexical.JDMLexicalEntry;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DefaultJDMLexicalAspect implements JDMLexicalAspect {

    static final String R_RAFF_SEM = "r_raff_sem";
    private final JDMRelationType raffSemType;
    private final JeuxDeMots jeuxDeMots;
//    private final Map<MutableInt, JDMLexicalSense> senseIndex;
//    private final Map<MutableInt, JDMLexicalEntry> entryIndex;

//    private final Logger logger = LoggerFactory.getLogger(DefaultJDMLexicalAspect.class);

    public DefaultJDMLexicalAspect(final JeuxDeMots jeuxDeMots) {
        this.jeuxDeMots = jeuxDeMots;
//        senseIndex = new HashMap<>();
//        entryIndex = new HashMap<>();
        raffSemType = jeuxDeMots.findType(R_RAFF_SEM);
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

    @Override
    public Optional<JDMLexicalEntry> getLexicalEntry(final int id) {
        return Optional.ofNullable(nodeToLexicalEntry(jeuxDeMots.getNode(id)));
    }

    @Override
    public Optional<JDMLexicalSense> getLexicalSense(final int id) {
        return Optional.ofNullable(nodeToLexicalSense(jeuxDeMots.getNode(id)));
    }

    /**
     * Converts a JDMNode into a corresponding lexical entry, if the JDMNode corresponds to a lexical entry,
     * otherwise returns null.
     *
     * @return The corresponding lexical entry or null
     */
    private JDMLexicalEntry nodeToLexicalEntry(final JDMNode node) {
//        final MutableInt id = node.getId();
//        JDMLexicalEntry lexicalEntry = entryIndex.get(id);
        JDMLexicalEntry lexicalEntry = null;
//        if (lexicalEntry == null) {
        final Map<JDMRelationType, List<JDMRelation>> incomingRefinementRelations = JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getIncomingRelations(raffSemType, node));
        if (incomingRefinementRelations.isEmpty()) {
            final Map<JDMRelationType, List<JDMRelation>> outgoingRelationMap = JDMLexicalAspect.relationListToRelationMap(jeuxDeMots.getOutgoingRelations(node));

            lexicalEntry = new DefaultJDMLexicalEntry(this, node, outgoingRelationMap);
        }
//        }
        return lexicalEntry;
    }


    private JDMLexicalSense nodeToLexicalSense(final JDMNode node) {
//        final MutableInt id = node.getId();
        JDMLexicalSense lexicalSense = null;
        if (node.getName().contains(">")) {
            lexicalSense = new DefaultJDMLexicalSense(node, this);
        }
        return lexicalSense;
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
