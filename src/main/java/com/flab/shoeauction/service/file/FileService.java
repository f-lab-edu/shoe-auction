package com.flab.shoeauction.service.file;

import com.flab.shoeauction.exception.file.IllegalMimeTypeException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileService {

    public void checkImageMimeType(String mimeType) {
        if (!(mimeType.equals("image/jpg") || mimeType.equals("image/jpeg")
            || mimeType.equals("image/png") || mimeType.equals("image/gif"))) {
            throw new IllegalMimeTypeException();
        }
    }

    public String addDirToSave(String fileName, String dir) {
        StringBuilder builder = new StringBuilder();

        builder.append(dir).append("/").append(fileName);

        return builder.toString();
    }

    public String fileNameConvert(String fileName) {
        StringBuilder builder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String extension = getExtension(fileName);

        builder.append(uuid).append(".").append(extension);

        return builder.toString();
    }

    private String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");

        return fileName.substring(pos + 1);
    }
}
