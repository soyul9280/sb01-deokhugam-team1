package com.codeit.duckhu.domain.book.mapper;

import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

  @Mapping(target = "reviewCount", source = "reviewCount")
  @Mapping(target = "rating", source = "rating")
  BookDto toDto(Book book, Integer reviewCount, Double rating);

  // 기본 변환 (선택적으로 유지)
  @Mapping(target = "reviewCount", ignore = true)
  @Mapping(target = "rating", ignore = true)
  BookDto toDto(Book book);
}
