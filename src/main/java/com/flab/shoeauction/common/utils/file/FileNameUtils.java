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

    public static String addDirToSave(String fileName, String dir) {
        StringBuilder builder = new StringBuilder();

        builder.append(dir).append("/").append(fileName);

        return builder.toString();
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
}
