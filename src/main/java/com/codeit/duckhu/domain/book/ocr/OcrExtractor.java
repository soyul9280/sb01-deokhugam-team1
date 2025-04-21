package com.codeit.duckhu.domain.book.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OcrExtractor {
  String extractOCR(MultipartFile image);
}
