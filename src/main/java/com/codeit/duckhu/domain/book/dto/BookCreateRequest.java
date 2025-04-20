package com.codeit.duckhu.domain.book.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 *
 * @param title 도서 제목 *
 * @param author 저자 *
 * @param description 설명
 * @param publisher 출판사 *
 * @param publishedDate 출판일 *
 * @param isbn ISBN
 */
public record BookCreateRequest(
    @NotBlank(message = "제목은 필수 입력값입니다.")
    String title,
    @NotBlank(message = "저자는 필수 입력값입니다.")
    String author,
    String description,
    @NotBlank(message = "출판사는 필수 입력값입니다")
    String publisher,
    @NotBlank(message = "출판일은 필수 입력값입니다.")
    LocalDate publishedDate,
    String isbn
) {

}
