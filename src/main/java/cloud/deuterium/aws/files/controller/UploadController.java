package cloud.deuterium.aws.files.controller;

import cloud.deuterium.aws.files.dto.UploadResponse;
import cloud.deuterium.aws.files.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */

@RestController
@RequestMapping("/v1/upload")
public class UploadController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public Mono<UploadResponse> uploadAws(@RequestPart("file") FilePart filePart) {
        log.info("POST request -> upload file with name: {}", filePart.filename());
        return uploadService.upload(filePart);
    }
}
