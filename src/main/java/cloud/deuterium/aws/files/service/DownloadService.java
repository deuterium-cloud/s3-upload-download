package cloud.deuterium.aws.files.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */
public interface DownloadService {
    Mono<ResponseEntity<Flux<ByteBuffer>>> download(String fileName);
}
