package cloud.deuterium.aws.files.service;

import cloud.deuterium.aws.files.exception.DownloadFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */

@Service
public class DownloadServiceImpl implements DownloadService {

    @Value("${aws.bucketName}")
    private String bucketName;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final S3AsyncClient s3AsyncClient;

    public DownloadServiceImpl(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @Override
    public Mono<ResponseEntity<Flux<ByteBuffer>>> download(String fileName) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("videos/" + fileName)
                .build();

        return Mono.fromFuture(s3AsyncClient.getObject(request, new FluxResponseProvider()))
                .map(response -> {
                    checkResult(response.sdkResponse);
                    String filename = getMetadataItem(response.sdkResponse, "filename", fileName);
                    log.info("File downloading: filename={}, length={}", filename, response.sdkResponse.contentLength());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, response.sdkResponse.contentType())
                            .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.sdkResponse.contentLength()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                            .body(response.flux);
                });
    }

    private static void checkResult(GetObjectResponse response) {
        SdkHttpResponse sdkResponse = response.sdkHttpResponse();
        if (sdkResponse != null && sdkResponse.isSuccessful()) {
            return;
        }
        throw new DownloadFailedException(response);
    }

    private String getMetadataItem(GetObjectResponse sdkResponse, String key, String defaultValue) {
        for (Map.Entry<String, String> entry : sdkResponse.metadata().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return defaultValue;
    }

    static class FluxResponseProvider implements AsyncResponseTransformer<GetObjectResponse, FluxResponse> {

        private FluxResponse response;

        @Override
        public CompletableFuture<FluxResponse> prepare() {
            response = new FluxResponse();
            return response.cf;
        }

        @Override
        public void onResponse(GetObjectResponse sdkResponse) {
            this.response.sdkResponse = sdkResponse;
        }

        @Override
        public void onStream(SdkPublisher<ByteBuffer> publisher) {
            response.flux = Flux.from(publisher);
            response.cf.complete(response);
        }

        @Override
        public void exceptionOccurred(Throwable error) {
            response.cf.completeExceptionally(error);
        }

    }

    static class FluxResponse {
        final CompletableFuture<FluxResponse> cf = new CompletableFuture<>();
        GetObjectResponse sdkResponse;
        Flux<ByteBuffer> flux;
    }


}
