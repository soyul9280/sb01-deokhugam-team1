package com.codeit.duckhu.domain.book.controller;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("도서를 등록하면 201 상태코드와 BookDto가 반환된다.")
  void createBook_shouldReturn201AndBookDto() throws Exception {
    // given
    BookCreateRequest request = new BookCreateRequest(
        "클린 코드", "로버트 마틴", "소개", "출판사",
        LocalDate.now(), "1234567890"
    );

    MockMultipartFile bookDataPart = new MockMultipartFile(
        "bookData", null,
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile thumbnailPart = new MockMultipartFile(
        "thumbnailImage", "image.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "fake-image-content".getBytes()
    );

    BookDto response = new BookDto(
        UUID.randomUUID(),
        "클린 코드",
        "로버트 마틴",
        "소개",
        "출판사",
        LocalDate.of(2020, 1, 1),
        "1234567890",
        null,        // thumbnailUrl
        0,           // reviewCount
        0.0,         // rating
        Instant.now(), // createdAt
        Instant.now()  // updatedAt
    );

    given(bookService.registerBook(request, Optional.of(thumbnailPart)))
        .willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/books")
            .file(bookDataPart)
            .file(thumbnailPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("클린 코드"))
        .andExpect(jsonPath("$.author").value("로버트 마틴"));
  }

  @Test
  @DisplayName("도서 목록 조회 시 결과가 반환된다.")
  void getBooks_shouldReturnBookList() throws Exception {
    // given
    CursorPageResponseBookDto fakeResponse = new CursorPageResponseBookDto(List.of(), null, null, 0,
        0, false);

    given(bookService.searchBooks(
        null, "title", "DESC", null, null, 50
    )).willReturn(fakeResponse);

    // when & then
    mockMvc.perform(get("/api/books"))
        .andExpect(status().isOk());
  }
}
