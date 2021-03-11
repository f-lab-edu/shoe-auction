package com.flab.shoeauction.service.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.flab.shoeauction.common.properties.AwsProperties;
import com.flab.shoeauction.common.utils.file.FileNameUtils;
import com.flab.shoeauction.exception.file.ImageRoadFailedException;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AwsS3Service implements StorageService {

    private final AwsProperties awsProperties;

    private AmazonS3 s3Client;

    /*
     * bean이 초기화 될 때 한 번 AmazonS3Client 객체를 빌드한다.
     */
    @PostConstruct
    private void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(),
            awsProperties.getSecretKey());

        s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(awsProperties.getRegionStatic())
            .build();
    }

    public String uploadBrandImage(MultipartFile file) {
        return upload(file, awsProperties.getBrnadBucket());
    }

    public String uploadProductImage(MultipartFile file) {
        return upload(file, awsProperties.getProductBucket());
    }

    public String upload(MultipartFile file, String bucket) {
        String fileName = file.getOriginalFilename();
        String convertedFileName = FileNameUtils.fileNameConvert(fileName);

        try {
            String mimeType = new Tika().detect(file.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();

            FileNameUtils.checkImageMimeType(mimeType);
            metadata.setContentType(mimeType);
            s3Client.putObject(
                new PutObjectRequest(bucket, convertedFileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            throw new ImageRoadFailedException();
        }

        return s3Client.getUrl(bucket, convertedFileName).toString();
    }

    public void deleteBrandImage(String key) {
        delete(awsProperties.getBrnadBucket(), key);
    }

    public void deleteProductImage(String key) {
        delete(awsProperties.getProductBucket(), key);
    }

    public void delete(String bucket, String key) {
        s3Client.deleteObject(bucket, key);
    }
}