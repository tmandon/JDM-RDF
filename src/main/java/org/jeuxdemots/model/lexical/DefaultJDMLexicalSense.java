package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.graph.JDMNode;
import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.lexical.JDMLexicalSense;
import org.jeuxdemots.model.graph.DefaultJDMNode;

public class DefaultJDMLexicalSense extends DefaultJDMNode implements JDMLexicalSense {

    private final String gloss;

    DefaultJDMLexicalSense(final JDMNode node, final JeuxDeMots jeuxDeMots) {
        super(node.getId(), node.getName(), node.getNodeType(), node.getWeight());
        final String glossIdString = extractGlossNodeId(node);
        if (glossIdString.isEmpty()) {
            gloss = "";
        } else {
            final int glossNodeId = Integer.valueOf(extractGlossNodeId(node));
            gloss = getGlossString(jeuxDeMots, glossNodeId);
        }

    }

    private String extractGlossNodeId(final JDMNode jdmNode){
        final String name = jdmNode.getName();
        return ((name != null) && name.contains(">")) ? name.split(">")[1] : "";
    }

    @SuppressWarnings("LawOfDemeter")
    private String getGlossString(final JeuxDeMots jeuxDeMots, final int nodeId){
        final JDMNode node = jeuxDeMots.getNode(nodeId);
        return node.getName();
    }

    @Override
    public String getGloss() {
        return gloss;
    }
}
