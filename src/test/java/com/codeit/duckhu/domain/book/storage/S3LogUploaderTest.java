package com.codeit.duckhu.domain.book.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.codeit.duckhu.global.S3.S3LogUploader;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3LogUploaderTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3LogUploader uploader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // ReflectionTestUtils로 private @Value 필드 주입
        ReflectionTestUtils.setField(uploader, "bucket", "test-bucket");
        ReflectionTestUtils.setField(uploader, "logDir", tempDir.toString());
    }

    @Test
    void whenFileExists_thenUploadCalled() throws Exception {
        // 전날 날짜 문자열 준비
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "deokhugam." + dateStr + ".log";

        // 더미 로그 파일 생성
        Path file = tempDir.resolve(filename);
        Files.writeString(file, "dummy log");

        // 메서드 직접 호출
        uploader.uploadYesterdayLog();

        // S3Client.putObject 호출 검증
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(
            captor.capture(),
            any(RequestBody.class)
        );

        PutObjectRequest actual = captor.getValue();
        String expectedKey = String.format("logs/deokhugam.%s.log", dateStr);
        assertEquals("test-bucket", actual.bucket());
        assertEquals(expectedKey,  actual.key());
    }

    @Test
    void whenFileMissing_thenNoUpload() {
        // 아무 파일도 만들지 않은 상태
        uploader.uploadYesterdayLog();
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}