package cloud.deuterium.aws.files.service;

import cloud.deuterium.aws.files.dto.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */

@Service
public class UploadServiceImpl implements UploadService{

    @Value("${aws.bucketName}")
    private String bucketName;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final S3AsyncClient s3AsyncClient;

    public UploadServiceImpl(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @Override
    public Mono<UploadResponse> upload(FilePart filePart) {

        String fileName = filePart.filename();

        Mono<byte[]> mono = DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });

        return mono.map(bytes -> s3AsyncClient.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key("videos/" + fileName)
                        .build(), AsyncRequestBody.fromBytes(bytes)))
                .log()
                .then(Mono.just(new UploadResponse(true, "File successfully uploaded")));
    }
}
