package com.example.parquet.controller;

import com.example.parquet.service.ParquetService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private ParquetService parquetService;

    @PostMapping("/upload")
    public String uploadJsonFile(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Convert JSON to Avro
            List<GenericRecord> records = parquetService.convertJsonToAvro(inputStream);

            // Load schema
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("transaction-schema.avsc");
            Schema schema = new Schema.Parser().parse(schemaStream);

            // Write to Parquet
            File outputFile = parquetService.writeParquetFile(records, schema, "output.parquet");

            return "✅ Parquet file created at: " + outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}
