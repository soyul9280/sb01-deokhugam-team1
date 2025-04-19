package com.codeit.duckhu.domain.book.ocr;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface OcrExtractor {
  String extractOCR(MultipartFile image);
}
