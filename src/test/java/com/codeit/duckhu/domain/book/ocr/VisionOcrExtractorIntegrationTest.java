package com.codeit.duckhu.domain.book.ocr;

import java.io.FileInputStream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class VisionOcrExtractorIntegrationTest {
  @Autowired
  private VisionOcrExtractor ocrExtractor;

  @Test
  @DisplayName("실제_이미지에서_ISBN을_정상적으로_추출한다")
  void extract_isbn_success() throws Exception {
    // given
    FileInputStream fis = new FileInputStream("src/test/resources/image_with_isbn.jpg");
    MockMultipartFile file = new MockMultipartFile("file", "image_with_isbn.jpg", "image/jpeg", fis);

    // when
    String isbn = ocrExtractor.extractOCR(file);

    // then
    assertThat(isbn).matches("^97[89]\\d{10}$"); // 예: 9781234567890
  }
}
