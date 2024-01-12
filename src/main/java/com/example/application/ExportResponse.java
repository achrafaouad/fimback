package com.example.application;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExportResponse {
    private StringBuilder file1Content;
    private StringBuilder file2Content;
    private String fileName;
    private byte[] file1Bytes;
    private byte[] file2Bytes;

    // Constructors, getters, and setters
}
