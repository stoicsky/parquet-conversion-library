package com.example.parquet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParquetService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a JSON InputStream into a list of Avro GenericRecords using the Avro schema.
     */
    public List<GenericRecord> convertJsonToAvro(InputStream jsonInputStream) throws IOException {
        // Load schema from resources
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("transaction-schema.avsc");
        if (schemaStream == null) {
            throw new IOException("❌ Schema file 'transaction-schema.avsc' not found in resources!");
        }

        Schema schema = new Schema.Parser().parse(schemaStream);
        JsonNode rootNode = objectMapper.readTree(jsonInputStream);

        List<GenericRecord> records = new ArrayList<>();
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                GenericRecord record = new GenericData.Record(schema);
                record.put("transactionId", node.path("transactionId").asText());
                record.put("amount", node.path("amount").asDouble());
                record.put("currency", node.path("currency").asText());
                record.put("timestamp", node.path("timestamp").asText());
                record.put("status", node.path("status").asText());
                records.add(record);
            }
        } else {
            throw new IOException("❌ Expected a JSON array of records!");
        }

        return records;
    }

    /**
     * Writes a list of Avro GenericRecords to a Parquet file.
     */
    public File writeParquetFile(List<GenericRecord> records, Schema schema, String fileName) throws IOException {
        // Force user context to avoid getSubject issue
        System.setProperty("HADOOP_USER_NAME", "testuser");  // or any dummy name

        // Set Hadoop configuration to not use security manager logic
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "simple");

        File outputDir = new File("output");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("❌ Failed to create output directory!");
        }

        File outputFile = new File(outputDir, fileName);
        Path path = new Path(outputFile.getAbsolutePath());

        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(path)
                .withSchema(schema)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withConf(conf)
                .build()) {

            for (GenericRecord record : records) {
                writer.write(record);
            }
        }

        return outputFile;
    }

}

