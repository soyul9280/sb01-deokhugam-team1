package com.codeit.duckhu.domain.book.controller.api;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.BookUpdateRequest;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.codeit.duckhu.domain.book.exception.BookException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Book", description = "도서 관련 API")
@RequestMapping("/api/books")
public interface BookApi {

  @Operation(summary = "도서 목록 조회", description = "검색어와 정렬 기준으로 도서 목록을 조회합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "도서 목록 조회 성공",
        content = @Content(schema = @Schema(implementation = CursorPageResponseBookDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(implementation = BookException.class))),
  })
  @GetMapping
  ResponseEntity<CursorPageResponseBookDto> getBooks(
      @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
      @Parameter(description = "정렬 기준 (title, createdAt 등)") @RequestParam(defaultValue = "title")
          String orderBy,
      @Parameter(description = "정렬 방향 (ASC, DESC)") @RequestParam(defaultValue = "DESC")
          String direction,
      @Parameter(description = "커서 값") @RequestParam(required = false) String cursor,
      @Parameter(description = "기준 시간") @RequestParam(required = false) Instant after,
      @Parameter(description = "한 페이지에 가져올 데이터 개수") @RequestParam(defaultValue = "50") int limit);

  @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "도서 등록 성공",
        content = @Content(schema = @Schema(implementation = BookDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (입력값 검증 실패, ISBN 형식 오류 등)",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "409",
        description = "ISBN 중복",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(implementation = BookException.class)))
  })
  @PostMapping(consumes = {"multipart/form-data"})
  ResponseEntity<BookDto> createBook(
      @RequestPart("bookData") BookCreateRequest bookData,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage);

  @Operation(summary = "이미지 기반 ISBN 인식", description = "도서 이미지를 통해 ISBN을 인식합니다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "ISBN 인식 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 이미지 형식 또는 OCR 인식 실패"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PostMapping(
      value = "/isbn/ocr",
      consumes = {"multipart/form-data"})
  ResponseEntity<String> extractIsbnOcr(
      @RequestPart(value = "image", required = false) MultipartFile image);

  @Operation(summary = "ISBN으로 도서 정보 조회", description = "Naver API를 통해 ISBN으로 도서 정보를 조회합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "도서 정보 조회 성공",
        content = @Content(schema = @Schema(implementation = NaverBookDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 ISBN 형식",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "404",
        description = "도서 정보 없음",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(implementation = BookException.class)))
  })
  @GetMapping("/info")
  ResponseEntity<NaverBookDto> getBookByIsbn(@RequestParam("isbn") String isbn);

  @Operation(summary = "도서 정보 수정", description = "도서 정보를 수정합니다.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "도서 정보 수정 성공",
        content = @Content(schema = @Schema(implementation = BookDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "404",
        description = "도서 정보 없음",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "409",
        description = "ISBN 중복",
        content = @Content(schema = @Schema(implementation = BookException.class))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = @Content(schema = @Schema(implementation = BookException.class)))
  })
  @PatchMapping(
      value = "/{bookId}",
      consumes = {"multipart/form-data"})
  ResponseEntity<BookDto> updateBook(
      @PathVariable("bookId") UUID bookId,
      @RequestPart("bookData") BookUpdateRequest bookData,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage);

  @Operation(summary = "도서 논리 삭제", description = "도서를 논리적으로 삭제합니다.")
  @DeleteMapping(value = "/{bookId}")
  ResponseEntity<Void> deleteBookLogically(@PathVariable("bookId") UUID bookId);

  @Operation(summary = "도서 물리 삭제", description = "도서를 물리적으로 삭제합니다.")
  @DeleteMapping(value = "/{bookId}/hard")
  ResponseEntity<Void> deleteBookPhysically(@PathVariable("bookId") UUID bookId);
}
