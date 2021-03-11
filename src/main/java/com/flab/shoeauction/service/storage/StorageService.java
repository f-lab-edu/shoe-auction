package com.flab.shoeauction.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile file, String destLocation);

    void delete(String destLocation, String destKey);
}
