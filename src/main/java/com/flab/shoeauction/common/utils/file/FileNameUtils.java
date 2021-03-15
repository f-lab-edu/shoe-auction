package com.flab.shoeauction.common.utils.file;

import com.flab.shoeauction.exception.file.IllegalMimeTypeException;
import java.util.UUID;

public class FileNameUtils {

    public static void checkImageMimeType(String mimeType) {
        if (!(mimeType.equals("image/jpg") || mimeType.equals("image/jpeg")
            || mimeType.equals("image/png") || mimeType.equals("image/gif"))) {
            throw new IllegalMimeTypeException();
        }
    }

    public static String fileNameConvert(String fileName) {
        StringBuilder builder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String extension = getExtension(fileName);

        builder.append(uuid).append(".").append(extension);

        return builder.toString();
    }

    private static String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");

        return fileName.substring(pos + 1);
    }

    public static String getFileName(String path) {
        int idx = path.lastIndexOf("/");

        return path.substring(idx + 1);
    }

    public static String toThumbnail(String src) {
        return src.replaceFirst("origin", "thumbnail");
    }

    public static String toResized(String src) {
        return src.replaceFirst("origin", "resized");
    }
}
