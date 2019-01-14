package org.jeuxdemots.reader;

import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.graph.JeuxDeMotsFactory;
import org.jeuxdemots.model.api.graph.NodeContainer;
import org.jeuxdemots.model.api.graph.RelationContainer;
import org.jeuxdemots.reader.parser.AbstractJDMParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class JDMLoaderFromDump extends AbstractJDMParser implements JDMLoader {


    private final InputStream jdmInputStream;
    private NodeContainer nodes;
    private RelationContainer relations;
    private final JeuxDeMotsFactory jeuxDeMotsFactory;


    public JDMLoaderFromDump(final InputStream jdmInputStream, final JeuxDeMotsFactory jeuxDeMotsFactory) {
        super(true,true);
        this.jdmInputStream = jdmInputStream;
        this.jeuxDeMotsFactory = jeuxDeMotsFactory;
    }

    @Override
    public JeuxDeMots load() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jdmInputStream, "ISO-8859-1"))) {
            parseJDM(bufferedReader);
        }

        return jeuxDeMotsFactory.createJeuxDeMots(nodes,relations);
    }


    @Override
    public void processRelationType(final Map<String, String> fields) {
        relations.addRelationType(
                jeuxDeMotsFactory.createRelationType(
                        Integer.valueOf(fields.get("rtid")),
                        fields.get("name"),
                        fields.get("nom_etendu"),
                        fields.get("info")));
    }

    @Override
    public void processNode(final Map<String, String> fields) {
        final String eid = fields.get("eid");
        final String t = fields.get("t");
        final String w = fields.get("w");
        if((eid != null) && (t != null) && (w != null)) {
            nodes.add(
                    jeuxDeMotsFactory.createNode(
                            Integer.valueOf(fields.get("eid")),
                            fields.get("n"),
                            Integer.valueOf(fields.get("t")),
                            Double.valueOf(fields.get("w"))
                    )
            );
        }
    }

    @Override
    public void processRelation(final Map<String, String> fields) {
        relations.add(
                jeuxDeMotsFactory.createRelation(
                        Integer.valueOf(fields.get("rid")),
                        Integer.valueOf(fields.get("n1")),
                        Integer.valueOf(fields.get("n2")),
                        relations.findType(Integer.valueOf(fields.get("t"))),
                        Double.valueOf(fields.get("w"))
                )
        );
    }

    @Override
    public void initializeContainers(final int numberOfNodes, final int numberOfRelations) throws IOException {
        if ((numberOfNodes == 0) || (numberOfRelations == 0)) {
            throw new IOException("Incorrect dump file, max node id or max relation id missing");
        }
        relations = jeuxDeMotsFactory.createRelationContainer(numberOfRelations, numberOfNodes);
        nodes = jeuxDeMotsFactory.createNodeContainer(numberOfNodes);
    }
}
