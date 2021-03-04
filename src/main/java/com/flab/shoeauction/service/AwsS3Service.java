package com.flab.shoeauction.service;

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
public class AwsS3Service {

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

    public String upload(MultipartFile file, String dir) {
        String fileName = file.getOriginalFilename();
        String convertedFileName = FileNameUtils.fileNameConvert(fileName);
        String fileNameWithPath = FileNameUtils.addDirToSave(convertedFileName, dir);

        try {
            String mimeType = new Tika().detect(file.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();

            FileNameUtils.checkImageMimeType(mimeType);
            metadata.setContentType(mimeType);
            s3Client.putObject(
                new PutObjectRequest(awsProperties.getBucket(), fileNameWithPath,
                    file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            throw new ImageRoadFailedException();
        }

        return s3Client.getUrl(awsProperties.getBucket(), fileNameWithPath).toString();
    }
}