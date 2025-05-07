package com.codeit.duckhu.global.S3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3LogUploader {

    private final S3Client s3Client;

    @Value("${duckhu.storage.s3.bucket}")
    private String bucket;

    // 로컬 로그 디렉토리
    @Value("${logging.file.path:logs}")
    private String logDir;

    //매일 01시에 전날 로그 파일을 찾아 s3에 업로드합니다.
    @Scheduled(cron = "0 0 1 * * *")
    public void uploadYesterdayLog(){
        String date = LocalDate.now().minusDays(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 실제 파일명 패턴: deokhugam-YYYY-MM-DD.log
        Path filePath = Paths.get(logDir, "deokhugam." + date + ".log");

        if (Files.notExists(filePath)) {
            log.warn("Log file not found for upload: {}", filePath);
            return;
        }

        // S3에 올릴 키: logs/deokhugam.YYYY-MM-DD.log
        String s3Key = "logs/deokhugam." + date + ".log";

        // AWS SDK의 RuntimeException (SdkException) 등이 발생하면 스케줄러에 의해 로그됩니다.
        PutObjectRequest req = PutObjectRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .build();

        s3Client.putObject(req, RequestBody.fromFile(filePath.toFile()));
        log.info("Uploaded log to S3: s3://{}/{}", bucket, s3Key);
    }
}
