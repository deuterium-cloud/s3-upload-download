package cloud.deuterium.aws.files.service;

import cloud.deuterium.aws.files.dto.UploadResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */
public interface UploadService {
    Mono<UploadResponse> upload(FilePart filePart);
}
