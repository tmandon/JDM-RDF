package org.jeuxdemots.reader;

import org.jeuxdemots.model.api.graph.JeuxDeMots;
import org.jeuxdemots.model.api.graph.JeuxDeMotsFactory;
import org.jeuxdemots.model.api.graph.NodeContainer;
import org.jeuxdemots.model.api.graph.RelationContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDMLoaderFromDump implements JDMLoader {

    private static final Pattern RELATION_MAX_ID_PATTERN = Pattern.compile("// MAX RELATION ID = ([0-9]+)");
    private static final Pattern NODE_MAX_ID_PATTERN = Pattern.compile("// MAX NODE ID = ([0-9]+)");
    private static final Pattern SEPARATOR_CHAR_PATTERN = Pattern.compile("SEPARATOR = (.*)");

    private static final Pattern RELATION_TYPES_PATTERN = Pattern.compile("// ---- RELATION TYPES", Pattern.LITERAL);
    private static final Pattern NODES_PATTERN = Pattern.compile("// -- NODES", Pattern.LITERAL);
    private static final Pattern RELATIONS_PATTERN = Pattern.compile("// -- RELATIONS", Pattern.LITERAL);

    private static final Pattern KEY_VALUE = Pattern.compile("([^=]*)=([^=]*)");
    private static final Pattern KEY_VALUE_QUOTED_PATTERN = Pattern.compile("([^=]*)=\"([^\"]*)\"$");


    private final InputStream jdmInputStream;
    private NodeContainer nodes;
    private RelationContainer relations;
    private final JeuxDeMotsFactory jeuxDeMotsFactory;

    private enum ParsingStatus {
        METADATA, NODES, RELATION_TYPES, RELATIONS;
    }

    public JDMLoaderFromDump(final InputStream jdmInputStream, final JeuxDeMotsFactory jeuxDeMotsFactory) {
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

    @SuppressWarnings({"HardcodedFileSeparator", "IfStatementWithTooManyBranches"})
    private void parseJDM(final BufferedReader bufferedReader) throws IOException {
        ParsingStatus status = ParsingStatus.METADATA;
        int numberOfRelations = 0;
        int numberOfNodes = 0;
        String separatorChar = "\\|";
        String line = "";
        boolean initialized = false;
        while (line != null) {
            line = bufferedReader.readLine();
            if(line!=null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    if (line.startsWith("//")) {
                        status = ParsingStatus.METADATA;
                    }
                    if (status == ParsingStatus.METADATA) {
                        final Matcher nodeMaxIdMatcher = NODE_MAX_ID_PATTERN.matcher(line);
                        final Matcher relatioMaxIdMatcher = RELATION_MAX_ID_PATTERN.matcher(line);
                        final Matcher separatorCharMatcher = SEPARATOR_CHAR_PATTERN.matcher(line);

                        if (nodeMaxIdMatcher.matches()) {
                            numberOfNodes = Integer.valueOf(nodeMaxIdMatcher.group(1));
                        } else if (relatioMaxIdMatcher.matches()) {
                            numberOfRelations = Integer.valueOf(relatioMaxIdMatcher.group(1));
                        } else if (separatorCharMatcher.matches()) {
                            separatorChar = separatorCharMatcher.group(1);
                            if (separatorChar.equals("|")) {
                                separatorChar = "\\|";
                            }
                        } else if (matches(RELATION_TYPES_PATTERN, line)) {
                            status = ParsingStatus.RELATION_TYPES;
                        } else if (matches(NODES_PATTERN, line)) {
                            status = ParsingStatus.NODES;
                        } else if (matches(RELATIONS_PATTERN, line)) {
                            status = ParsingStatus.RELATIONS;
                        }
                    } else {

                        if (!initialized) {
                            initializeContainers(numberOfNodes, numberOfRelations);
                            initialized = true;
                        }

                        final Map<String, String> fields = parseDataLine(line, separatorChar);

                        if (status == ParsingStatus.RELATION_TYPES) {
                            processRelationType(fields);
                        } else if (status == ParsingStatus.NODES) {
                            processNode(fields);
                        } else if (status == ParsingStatus.RELATIONS) {
                            processRelation(fields);
                        }
                    }
                }
            }
        }
    }

    private void processRelationType(final Map<String, String> fields) {
        relations.addRelationType(
                jeuxDeMotsFactory.createRelationType(
                        Integer.valueOf(fields.get("rtid")),
                        fields.get("name"),
                        fields.get("nom_etendu"),
                        fields.get("info")));
    }

    private void processNode(final Map<String, String> fields) {
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

    private void processRelation(final Map<String, String> fields) {
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

    private void initializeContainers(final int numberOfNodes, final int numberOfRelations) throws IOException {
        if ((numberOfNodes == 0) || (numberOfRelations == 0)) {
            throw new IOException("Incorrect dump file, max node id or max relation id missing");
        }
        relations = jeuxDeMotsFactory.createRelationContainer(numberOfRelations, numberOfNodes);
        nodes = jeuxDeMotsFactory.createNodeContainer(numberOfNodes);
    }

    private Map<String, String> parseDataLine(final String line, final String separatorChar) {
        final Map<String, String> lineData = new HashMap<>();
        final String[] fields = line.split(separatorChar);
        for (final String field : fields) {
            Matcher keyValueMatcher = KEY_VALUE_QUOTED_PATTERN.matcher(field);
            if(!keyValueMatcher.matches()){
                keyValueMatcher = KEY_VALUE.matcher(field);
            }
            if (keyValueMatcher.matches()) {
                lineData.put(keyValueMatcher.group(1), keyValueMatcher.group(2));
            }
        }
        return lineData;
    }

    private boolean matches(final Pattern pattern, final CharSequence line) {
        return pattern.matcher(line).matches();
    }
}
