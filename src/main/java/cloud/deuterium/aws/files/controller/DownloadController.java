package cloud.deuterium.aws.files.controller;

import cloud.deuterium.aws.files.service.DownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */

@RestController
@RequestMapping("/v1/download")
public class DownloadController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DownloadService transferService;

    public DownloadController(DownloadService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<ByteBuffer>>> fileDownload(@RequestParam("fileName") String fileName) {
        log.info("GET request -> Download file: {}", fileName);
        return transferService.download(fileName);
    }
}
