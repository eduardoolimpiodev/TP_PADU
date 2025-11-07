package com.userprocessor.util;

import com.userprocessor.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidationUtil {

    private static final List<String> CSV_MIME_TYPES = Arrays.asList(
        "text/csv",
        "application/csv",
        "text/plain"
    );

    private static final List<String> JSON_MIME_TYPES = Arrays.asList(
        "application/json",
        "text/json",
        "text/plain"
    );

    private static final List<String> XML_MIME_TYPES = Arrays.asList(
        "application/xml",
        "text/xml",
        "text/plain"
    );

    public static boolean isValidMimeType(MultipartFile file, FileType fileType) {
        String mimeType = file.getContentType();
        if (mimeType == null) {
            return false;
        }

        switch (fileType) {
            case CSV:
                return CSV_MIME_TYPES.contains(mimeType.toLowerCase());
            case JSON:
                return JSON_MIME_TYPES.contains(mimeType.toLowerCase());
            case XML:
                return XML_MIME_TYPES.contains(mimeType.toLowerCase());
            default:
                return false;
        }
    }

    public static FileType detectFileTypeFromName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }

        String extension = getFileExtension(filename).toLowerCase();
        
        switch (extension) {
            case "csv":
                return FileType.CSV;
            case "json":
                return FileType.JSON;
            case "xml":
                return FileType.XML;
            default:
                return null;
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    public static boolean isValidFileSize(MultipartFile file, long maxSizeInBytes) {
        return file.getSize() <= maxSizeInBytes;
    }

    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeInBytes / 1024.0);
        } else {
            return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }
}
