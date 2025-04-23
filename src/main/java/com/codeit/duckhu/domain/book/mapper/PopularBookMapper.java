package com.codeit.duckhu.domain.book.mapper;

import com.codeit.duckhu.domain.book.dto.PopularBookDto;
import com.codeit.duckhu.domain.book.entity.PopularBook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PopularBookMapper {

  @Mapping(source = "book.id", target = "bookId")
  @Mapping(source = "book.title", target = "title")
  @Mapping(source = "book.author", target = "author")
  @Mapping(source = "book.thumbnailUrl", target = "thumbnailUrl")
  PopularBookDto toDto(PopularBook popularBook);
}
