package com.example.demo.component.AgentParts;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseManager {
    private final JdbcTemplate jdbcTemplate;
    private final String dbUrl;
    private volatile Word2Vec word2Vec; // volatile 추가
    private final Object word2VecLock = new Object(); // 락 객체 추가
    private Map<String, INDArray> tableEmbeddings;
    private Map<String, List<String>> tableColumns;
    private Map<String, INDArray> columnEmbeddings;
    private static final int EMBEDDING_SIZE = 100;
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_SAMPLE_SIZE = 5;

    public DatabaseManager(JdbcTemplate jdbcTemplate, @Value("${spring.datasource.url}") String dbUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbUrl = dbUrl;
        this.tableEmbeddings = new HashMap<>();
        this.columnEmbeddings = new HashMap<>();
        this.tableColumns = new HashMap<>();
    }

    private Word2Vec getWord2Vec() {
        if (word2Vec == null) {
            synchronized (word2VecLock) {
                if (word2Vec == null) {
                    initializeWord2Vec();
                }
            }
        }
        return word2Vec;
    }

    private void initializeWord2Vec() {
        try {
            // Create temporary vocabulary file
            File tempFile = File.createTempFile("vocab", ".txt");
            tempFile.deleteOnExit();

            // Basic database terminology
            List<String> vocabularyWords = new ArrayList<>(Arrays.asList(
                    "table", "column", "key", "index", "foreign", "primary",
                    "database", "schema", "relation", "entity", "attribute",
                    "varchar", "integer", "datetime", "boolean", "numeric"));

            // Add schema-specific vocabulary
            try {
                var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                        .orElseThrow(() -> new IllegalStateException("DataSource error"));
                try (var connection = dataSource.getConnection()) {
                    DatabaseMetaData metaData = connection.getMetaData();

                    // Add table names to vocabulary
                    try (ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })) {
                        while (tables.next()) {
                            String tableName = tables.getString("TABLE_NAME");
                            // Split table name by common separators and add each part
                            vocabularyWords.addAll(Arrays.asList(tableName.split("[_\\s]")));

                            // Add columns for each table
                            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                                while (columns.next()) {
                                    String columnName = columns.getString("COLUMN_NAME");
                                    String typeName = columns.getString("TYPE_NAME");
                                    // Split column name and type by common separators and add each part
                                    vocabularyWords.addAll(Arrays.asList(columnName.split("[_\\s]")));
                                    vocabularyWords.addAll(Arrays.asList(typeName.split("[_\\s]")));
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to extract schema vocabulary", e);
            }

            // Remove duplicates and clean vocabulary
            vocabularyWords = vocabularyWords.stream()
                    .map(String::toLowerCase)
                    .distinct()
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());

            // Write vocabulary to temp file
            FileUtils.writeLines(tempFile, vocabularyWords);

            // Initialize tokenizer
            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            // Build and train Word2Vec model
            Word2Vec initializedModel = new Word2Vec.Builder()
                    .minWordFrequency(1)
                    .iterations(5)
                    .layerSize(EMBEDDING_SIZE)
                    .seed(42)
                    .windowSize(5)
                    .iterate(new BasicLineIterator(tempFile))
                    .tokenizerFactory(tokenizerFactory)
                    .build();

            initializedModel.fit();
            this.word2Vec = initializedModel; // 완전히 초기화된 후에 할당
        } catch (Exception e) {
            throw new RuntimeException("Word2Vec initialization failed", e);
        }
    }

    public String loadDatabaseSchema() {
        String dbName = extractDatabaseName(dbUrl);
        StringBuilder schema = new StringBuilder("DB Schema (" + dbName + "):\n");
        try {
            var dataSource = Optional.ofNullable(jdbcTemplate.getDataSource())
                    .orElseThrow(() -> new IllegalStateException("DataSource error"));
            try (var connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                processSchema(metaData, schema);
                getWord2Vec(); // initializeWord2Vec() 대신 getWord2Vec() 사용
                generateEmbeddings();
            }
        } catch (Exception e) {
            return "Schema loading failed: " + e.getMessage();
        }
        return schema.toString();
    }

    private void generateEmbeddings() {
        tableColumns.forEach((tableName, columns) -> {
            // Generate table embedding
            StringBuilder tableDesc = new StringBuilder(tableName);
            columns.forEach(col -> tableDesc.append(" ").append(col));
            tableEmbeddings.put(tableName, createEmbedding(tableDesc.toString()));

            // Generate column embeddings
            columns.forEach(columnName -> {
                String columnDesc = String.format("%s %s", tableName, columnName);
                columnEmbeddings.put(tableName + "." + columnName, createEmbedding(columnDesc));
            });
        });
    }

    private void processSchema(DatabaseMetaData metaData, StringBuilder schema) throws SQLException {
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                processTable(metaData, tableName, schema);
            }
        }
    }

    private void processTable(DatabaseMetaData metaData, String tableName, StringBuilder schema) throws SQLException {
        schema.append("\nTable: ").append(tableName);
        tableColumns.put(tableName, new ArrayList<>());

        processColumns(metaData, tableName, schema);
        processPrimaryKeys(metaData, tableName, schema);
    }

    private void processColumns(DatabaseMetaData metaData, String tableName, StringBuilder schema) throws SQLException {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
            processColumnsInBatches(columns, tableName, schema);
        }
    }

    private void processColumnsInBatches(ResultSet columns, String tableName, StringBuilder schema)
            throws SQLException {
        schema.append("\n  Columns:");
        List<ColumnInfo> columnBatch = new ArrayList<>();

        while (columns.next()) {
            columnBatch.add(new ColumnInfo(
                    columns.getString("COLUMN_NAME"),
                    columns.getString("TYPE_NAME")));

            if (columnBatch.size() >= BATCH_SIZE) {
                processColumnBatch(columnBatch, tableName, schema);
                columnBatch.clear();
            }
        }

        if (!columnBatch.isEmpty()) {
            processColumnBatch(columnBatch, tableName, schema);
        }
    }

    private void processColumnBatch(List<ColumnInfo> columns, String tableName, StringBuilder schema) {
        for (ColumnInfo column : columns) {
            schema.append("\n    - ")
                    .append(column.name())
                    .append(" (")
                    .append(column.type())
                    .append(")");

            tableColumns.get(tableName).add(column.name());
            generateColumnEmbedding(tableName, column);
        }
    }

    private record ColumnInfo(String name, String type) {
    }

    private void generateColumnEmbedding(String tableName, ColumnInfo column) {
        String columnDesc = String.format("%s %s %s", tableName, column.name(), column.type());
        INDArray embedding = createEmbedding(columnDesc);
        columnEmbeddings.put(tableName + "." + column.name(), embedding);
    }

    private void processPrimaryKeys(DatabaseMetaData metaData, String tableName, StringBuilder schema)
            throws SQLException {
        try (ResultSet pks = metaData.getPrimaryKeys(null, null, tableName)) {
            if (pks.next()) {
                schema.append("\n  Primary Key: ").append(pks.getString("COLUMN_NAME"));
            }
        }
    }

    private INDArray createEmbedding(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        INDArray embedding = Nd4j.zeros(EMBEDDING_SIZE);
        int count = 0;

        Word2Vec w2v = getWord2Vec(); // getWord2Vec() 사용
        for (String word : words) {
            // Handle compound words
            String[] subWords = word.split("[_\\s]");
            for (String subWord : subWords) {
                if (w2v.hasWord(subWord)) {
                    embedding.addi(Nd4j.create(w2v.getWordVector(subWord)));
                    count++;
                }
            }
        }

        return count > 0 ? embedding.divi(count) : embedding;
    }

    public List<String> findRelatedTables(String query) {
        INDArray queryEmbedding = createEmbedding(query);
        return findSimilarItems(queryEmbedding, tableEmbeddings);
    }

    private List<String> findSimilarItems(INDArray queryEmbedding, Map<String, INDArray> embeddings) {
        return embeddings.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        cosineSimilarity(queryEmbedding, entry.getValue())))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(INDArray vec1, INDArray vec2) {
        double dotProduct = vec1.mul(vec2).sumNumber().doubleValue();
        double norm1 = vec1.norm2Number().doubleValue();
        double norm2 = vec2.norm2Number().doubleValue();
        return dotProduct / (norm1 * norm2);
    }

    private String extractDatabaseName(String url) {
        String[] parts = url.split("/");
        String dbWithParams = parts[parts.length - 1];
        return dbWithParams.split("\\?")[0];
    }

    public long getTableCount(String tableName) {
        try {
            String query = String.format("SELECT COUNT(*) FROM %s", tableName);
            return jdbcTemplate.queryForObject(query, Long.class);
        } catch (Exception e) {
            return 0L;
        }
    }

    public QueryResult executeSelectQuery(String query) {
        try {
            String limitedQuery = ensureQueryLimit(query);

            List<Map<String, Object>> results = jdbcTemplate.query(
                    limitedQuery,
                    (rs, rowNum) -> {
                        Map<String, Object> row = new HashMap<>();
                        ResultSetMetaData metaData = rs.getMetaData();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        return row;
                    });
            return new QueryResult(results, null);
        } catch (Exception e) {
            return new QueryResult(null, "Query execution failed: " + e.getMessage());
        }
    }

    private String ensureQueryLimit(String query) {
        String upperQuery = query.toUpperCase().trim();
        if (!upperQuery.contains("LIMIT")) {
            return query + " LIMIT " + MAX_SAMPLE_SIZE;
        }
        return query;
    }

    public static record QueryResult(List<Map<String, Object>> data, String error) {
        public boolean isSuccess() {
            return error == null;
        }

        public List<Map<String, Object>> getData() {
            return data != null ? data : new ArrayList<>();
        }
    }
}