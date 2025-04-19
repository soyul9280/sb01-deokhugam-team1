package com.codeit.duckhu.domain.book.ocr;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component("visionOcrExtractor")
public class VisionOcrExtractor implements OcrExtractor {

  @Override
  public String extractOCR(MultipartFile image) {
    try {
      ByteString imgBytes = ByteString.readFrom(image.getInputStream());
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
          .addFeatures(feat)
          .setImage(img)
          .build();

      // ✅ 인증 키를 classpath에서 명시적으로 불러옴
      GoogleCredentials credentials = GoogleCredentials
          .fromStream(new ClassPathResource("vision-key.json").getInputStream());

      ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
          .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
          .build();

      try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
        List<AnnotateImageResponse> responses = client.batchAnnotateImages(List.of(request)).getResponsesList();
        if (responses.isEmpty() || responses.get(0).hasError()) {
          log.warn("Vision API 오류: {}", responses.isEmpty() ? "응답 없음" : responses.get(0).getError().getMessage());
          return "0";
        }

        String text = responses.get(0).getFullTextAnnotation().getText();
        return extractIsbnFromText(text);
      }

    } catch (Exception e) {
      log.error("Google Vision OCR 처리 실패", e);
      return "0";
    }
  }

  private String extractIsbnFromText(String text) {
    String cleaned = text.replaceAll("[\\n\\r]", " ").replaceAll("\\s+", " ");
    Pattern pattern = Pattern.compile(
        "ISBN[\\s:-]*((?:97[89][- ]?)?\\d{1,5}[- ]?\\d{1,7}[- ]?\\d{1,7}[- ]?\\d)",
        Pattern.CASE_INSENSITIVE
    );
    Matcher matcher = pattern.matcher(cleaned);
    return matcher.find() ? matcher.group(1).replaceAll("[- ]", "") : "0";
  }
}