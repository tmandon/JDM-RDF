package org.jeuxdemots.model.graph.sql;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jeuxdemots.model.api.graph.*;
import org.jeuxdemots.model.graph.DefaultJDMNode;
import org.jeuxdemots.model.graph.DefaultJDMRelation;
import org.jeuxdemots.model.graph.DefaultJDMRelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SQLJeuxDeMots implements JeuxDeMots {


    private static final Logger logger = LoggerFactory.getLogger(SQLJeuxDeMots.class);

    private final Connection connection;
    private final Connection streamConnection;

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public SQLJeuxDeMots(final Connection connection, final Connection streamConnection) {
        this.connection = connection;
        this.streamConnection = streamConnection;
    }


    @Override
    public void forEachNode(final Consumer<JDMNode> consumer) {
        forEachNodeDelegate(consumer, null);
    }

    @Override
    public void forEachNodeOfType(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        forEachNodeDelegate(consumer, nodeType);
    }

    private void forEachNodeDelegate(final Consumer<JDMNode> consumer, final NodeType nodeType) {
        String query = "SELECT * FROM nodes";
        if (nodeType != null) {
            query += " WHERE t=?";
        }
        try (final PreparedStatement statement = streamConnection.prepareStatement(query,
                java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)
        ) {
            statement.setFetchSize(Integer.MIN_VALUE);
            if (nodeType != null) {
                statement.setInt(1, nodeType.getCode());
            }
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    consumer.accept(
                            new DefaultJDMNode(
                                    new MutableInt(resultSet.getInt(1)),
                                    resultSet.getString(2),
                                    resultSet.getInt(3),
                                    new MutableDouble(resultSet.getDouble(4))
                            )
                    );

                }
            }

        } catch (final SQLException e) {
            logger.error("[forEachNode | forEachNodeOfType]@SQLJeuxDeMots: {}", e.toString());
        }
    }

    @Override
    public JDMNode getNode(final String name) {
        String query = "SELECT * FROM nodes";
        query += " WHERE n LIKE ?";
        JDMNode node = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            node = nodeFromPreparedStatement(statement);
        } catch (final SQLException e) {
            logger.error("getNode@SQLJeuxDeMots: {}", e.toString());
        }
        return node;
    }

    @Override
    public JDMNode getNode(final int id) {
        String query = "SELECT * FROM nodes";
        query += " WHERE eid=?";
        JDMNode node = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            node = nodeFromPreparedStatement(statement);
        } catch (final SQLException e) {
            logger.error("getNode@SQLJeuxDeMots: {}", e.toString());
        }
        return node;
    }

    private JDMNode nodeFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        JDMNode node = null;
        try (final ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                node = new DefaultJDMNode(
                        new MutableInt(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        new MutableDouble(resultSet.getDouble(4))
                );
            }
        }
        return node;
    }

    private List<JDMNode> nodesFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        final List<JDMNode> nodes = new ArrayList<>();
        try (final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                nodes.add(new DefaultJDMNode(
                        new MutableInt(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        new MutableDouble(resultSet.getDouble(4))
                ));
            }
        }
        return nodes;
    }

    private JDMRelationType relationTypeFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        JDMRelationType relationType = null;
        try (final ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                relationType = new DefaultJDMRelationType(
                        new MutableInt(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                );
            }
        }
        return relationType;
    }

    private JDMRelation relationFromPreparedStatement(final PreparedStatement statement) throws SQLException {
        final JDMRelation relation;
        try (final ResultSet resultSet = statement.executeQuery()) {
            relation = relationFromResultSet(resultSet);
        }
        return relation;
    }

    private JDMRelation relationFromResultSet(final ResultSet resultSet) throws SQLException {

        final JDMRelationType relationType = findType(resultSet.getInt(4));

        return new DefaultJDMRelation(
                new MutableInt(resultSet.getInt(1)),
                new MutableInt(resultSet.getInt(2)),
                new MutableInt(resultSet.getInt(3)),
                relationType,
                new MutableDouble(resultSet.getDouble(5)));
    }

    @Override
    public JDMRelationType findType(final int id) {
        String query = "SELECT * FROM relation_types";
        query += " WHERE rtid=?";
        JDMRelationType relationType = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            relationType = relationTypeFromPreparedStatement(statement);
        } catch (final SQLException e) {
            logger.error("getNode@SQLJeuxDeMots: {}", e.toString());
        }
        return relationType;
    }

    @Override
    public JDMRelationType findType(final String name) {
        String query = "SELECT * FROM relation_types";
        query += " WHERE name=?";
        JDMRelationType relationType = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            relationType = relationTypeFromPreparedStatement(statement);
        } catch (final SQLException e) {
            logger.error("findType@SQLJeuxDeMots: {}", e.toString());
        }
        return relationType;
    }

    @Override
    public JDMRelation getRelation(final int id) {
        String query = "SELECT * FROM relations";
        query += " WHERE rid=?";
        JDMRelation relation = null;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            relation = relationFromPreparedStatement(statement);
        } catch (final SQLException e) {
            logger.error("getRelations@SQLJeuxDeMots: {}", e.toString());
        }
        return relation;
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMRelationType type, final JDMNode target) {
        String query = "SELECT * FROM relations WHERE n2=?";
        if (type != null) {
            query += " AND t=?";
        }
        return getInOutRelationsFromQuery(query, type, target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMRelationType type, final JDMNode source) {
        String query = "SELECT * FROM relations WHERE n1=?";
        if (type != null) {
            query += " AND t=?";
        }
        return getInOutRelationsFromQuery(query, type, source);
    }

    private Collection<JDMRelation> getInOutRelationsFromQuery(final String query, final JDMRelationType type, final JDMNode node) {
        final Collection<JDMRelation> relations = new ArrayList<>();
        int counter = 0;
        try (final PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, node
                    .getId()
                    .intValue());
            if (type != null) {
                statement.setInt(2, type
                        .getId()
                        .intValue());
            }
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relations.add(relationFromResultSet(resultSet));
                    counter++;
                }
            }
        } catch (final SQLException e) {
            logger.error("getRelations@SQLJeuxDeMots: {}", e.toString());
        }
        return relations;

    }

    @Override
    public Collection<JDMRelation> getIncomingRelations(final JDMNode target) {
        return getIncomingRelations(null, target);
    }

    @Override
    public Collection<JDMRelation> getOutgoingRelations(final JDMNode source) {
        return getOutgoingRelations(null, source);
    }

    @Override
    public JDMNode getRelationSource(final JDMRelation relation) {
        return getNode(relation
                .getSourceId()
                .intValue());
    }

    @Override
    public JDMNode getRelationTarget(final JDMRelation relation) {
        return getNode(relation
                .getTargetId()
                .intValue());
    }
}
