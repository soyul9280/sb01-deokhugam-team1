package com.codeit.duckhu.domain.book.ocr;

import com.codeit.duckhu.domain.book.exception.OCRException;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("visionOcrExtractor")
@Primary
@Slf4j
public class VisionOcrExtractor implements OcrExtractor {

  private static final String DEFAULT_RESULT = "0";
  private static final Pattern ISBN_PATTERN =
      Pattern.compile("ISBN[\\s:-]*((?:97[89][- ]?)?\\d{1,5}[- ]?\\d{1,7}[- ]?\\d{1,7}[- ]?\\d)", Pattern.CASE_INSENSITIVE);

  private final Supplier<ImageAnnotatorClient> clientSupplier;

  public VisionOcrExtractor() {
    this(() -> {
      try {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new ClassPathResource("vision-key.json").getInputStream());
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();
        return ImageAnnotatorClient.create(settings);
      } catch (IOException e) {
        throw new OCRException(ErrorCode.GOOGLE_VISION_CLIENT_INIT_FAIL);
      }
    });
  }

  // 주입 가능한 생성자
  public VisionOcrExtractor(Supplier<ImageAnnotatorClient> clientSupplier) {
    this.clientSupplier = clientSupplier;
  }

  @Override
  public String extractOCR(MultipartFile image) {
    log.info("OCR 처리 시작 - 파일명: {}, 크기: {} bytes", image.getOriginalFilename(), image.getSize());
    try (ImageAnnotatorClient client = clientSupplier.get()) {
      ByteString imgBytes = ByteString.readFrom(image.getInputStream());
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();
      List<AnnotateImageResponse> responses = client.batchAnnotateImages(List.of(request)).getResponsesList();

      if (responses.isEmpty() || responses.get(0).hasError()) {
        String errorMsg = responses.isEmpty() ? "응답 없음" : responses.get(0).getError().getMessage();
        log.warn("Vision API 오류: {}", errorMsg);
        return DEFAULT_RESULT;
      }

      String extractedText = responses.get(0).getFullTextAnnotation().getText();
      log.debug("Vision API 텍스트 추출 완료");
      return extractIsbnFromText(extractedText);
    } catch (IOException e) {
      log.error("이미지 스트림 읽기 실패 - 오류: {}", e.toString());
      throw new OCRException(ErrorCode.IMAGE_STREAM_READ_FAIL);
    } catch (Exception e) {
      log.error("Google Vision OCR 처리 - 오류:  {}", e.toString());
      throw new OCRException(ErrorCode.OCR_PROCESSING_FAIL);
    }
  }

  // default 접근자로 수정하여 테스트 가능하게 함
  String extractIsbnFromText(String text) {
    String cleanedText = text.replaceAll("[\\n\\r]", " ").replaceAll("\\s+", " ");
    log.debug("OCR 텍스트 정제 완료");
    Matcher matcher = ISBN_PATTERN.matcher(cleanedText);

    if (matcher.find()) {
      log.info("ISBN 추출 성공");
      return matcher.group(1).replaceAll("[- ]", "");
    }

    log.warn("ISBN 추출 실패 - OCR 결과: {}", cleanedText);
    throw new OCRException(ErrorCode.UNABLE_EXTRACT_ISBN);
  }
}
