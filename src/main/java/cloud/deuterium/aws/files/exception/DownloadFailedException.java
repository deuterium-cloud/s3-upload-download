package cloud.deuterium.aws.files.exception;

import org.springframework.http.HttpStatus;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.io.Serial;
import java.util.Optional;

/**
 * Created by Milan Stojkovic 08-Dec-2021
 */
public class DownloadFailedException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    private int statusCode;
    private Optional<String> statusText;

    public DownloadFailedException(SdkResponse response) {

        SdkHttpResponse httpResponse = response.sdkHttpResponse();
        if (httpResponse != null) {
            this.statusCode = httpResponse.statusCode();
            this.statusText = httpResponse.statusText();
        } else {
            this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            this.statusText = Optional.of("UNKNOWN");
        }

    }
}
