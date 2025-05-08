package com.codeit.duckhu.domain.book.ocr;

import com.google.rpc.Status;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.domain.book.exception.OCRException;
import com.google.cloud.vision.v1.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class VisionOcrExtractorTest {

  @Test
  @DisplayName("정규표현식으로 ISBN을 정확히 추출한다")
  void extractIsbnFromText_success() {
    // given
    String text = "Some text with ISBN 978-89-01-12345-6 and more info.";
    VisionOcrExtractor extractor = new VisionOcrExtractor(() -> null); // clientSupplier 사용 안함

    // when
    String isbn = extractor.extractIsbnFromText(text);

    // then
    assertThat(isbn).isEqualTo("9788901123456");
  }

  @Test
  @DisplayName("ISBN이 없는 경우 예외를 던진다")
  void extractIsbnFromText_fail() {
    // given
    String text = "There is no ISBN here.";
    VisionOcrExtractor extractor = new VisionOcrExtractor(() -> null);

    // expect
    assertThatThrownBy(() -> extractor.extractIsbnFromText(text))
        .isInstanceOf(OCRException.class);
  }

  @Test
  @DisplayName("Vision API 응답이 유효할 때 ISBN을 정상 추출한다")
  void extractOCR_success_withMockedVisionClient() throws Exception {
    // given
    TextAnnotation textAnnotation = TextAnnotation.newBuilder()
        .setText("This is an OCR result. ISBN 978-89-01-12345-6 end.")
        .build();

    AnnotateImageResponse response = AnnotateImageResponse.newBuilder()
        .setFullTextAnnotation(textAnnotation)
        .build();

    BatchAnnotateImagesResponse batchResponse = BatchAnnotateImagesResponse.newBuilder()
        .addResponses(response)
        .build();

    ImageAnnotatorClient mockClient = mock(ImageAnnotatorClient.class);
    when(mockClient.batchAnnotateImages((List<AnnotateImageRequest>) any())).thenReturn(batchResponse);

    VisionOcrExtractor extractor = new VisionOcrExtractor(() -> mockClient);
    MockMultipartFile image = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "fake-image".getBytes()
    );

    // when
    String isbn = extractor.extractOCR(image);

    // then
    assertThat(isbn).isEqualTo("9788901123456");
  }

  @Test
  @DisplayName("Vision API가 오류 응답을 반환하면 기본 결과를 반환한다")
  void extractOCR_visionError_returnsDefault() throws Exception {
    // given
    Status errorStatus = Status.newBuilder().setMessage("API Error").build();
    AnnotateImageResponse response = AnnotateImageResponse.newBuilder()
        .setError(errorStatus)
        .build();

    BatchAnnotateImagesResponse batchResponse = BatchAnnotateImagesResponse.newBuilder()
        .addResponses(response)
        .build();

    ImageAnnotatorClient mockClient = mock(ImageAnnotatorClient.class);
    when(mockClient.batchAnnotateImages((List<AnnotateImageRequest>) any())).thenReturn(batchResponse);

    VisionOcrExtractor extractor = new VisionOcrExtractor(() -> mockClient);
    MockMultipartFile image = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "fake-image".getBytes()
    );

    // when
    String result = extractor.extractOCR(image);

    // then
    assertThat(result).isEqualTo("0");
  }
}