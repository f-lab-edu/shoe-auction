package com.flab.shoeauction.service.file;

import com.flab.shoeauction.exception.file.IllegalMimeTypeException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class FileService {

    public void checkImageMimeType(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String mimeType = new Tika().detect(inputStream);

            if (!(mimeType.equals("image/jpg") || mimeType.equals("image/jpeg")
                || mimeType.equals("image/png") || mimeType.equals("image/gif"))) {
                throw new IllegalMimeTypeException();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
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
