package com.codeit.duckhu.domain.review.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
public class ReviewRepositoryTest {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Test
  @DisplayName("리뷰 저장 확인")
  void saveReview_shouldSavedReview() {
    // Given
    User user = User.builder()
        .email("test@example.com")
        .nickname("테스터")
        .password("password")
        .isDeleted(false)
        .build();
    User savedUser = userRepository.save(user);

    Book book = Book.builder()
        .title("테스트 도서")
        .author("테스트 작가")
        .publisher("테스트 출판사")
        .isbn("9788956609959")
        .publishedDate(LocalDate.now())
        .isDeleted(false)
        .build();
    Book savedBook = bookRepository.save(book);

    Review savedReview = Review.builder()
        .rating(5)
        .content("재밌어요")
        .likeCount(0)
        .commentCount(0)
        .user(savedUser)
        .book(savedBook)
        .build();

    // When
    Review foundReview = reviewRepository.save(savedReview);

    // Then
    assertThat(foundReview).isNotNull();
    assertThat(foundReview.getRating()).isEqualTo(5);
    assertThat(foundReview.getContent()).isEqualTo("재밌어요");
    assertThat(foundReview.getLikeCount()).isEqualTo(0);
    assertThat(foundReview.getCommentCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("사용자 ID와 도서 ID로 리뷰 찾기")
  void findByUserIdAndBookId_success() {
    // Given
    // 사용자 생성 및 저장
    User user = User.builder()
        .email("test@example.com")
        .nickname("테스터")
        .password("password")
        .isDeleted(false)
        .build();
    User savedUser = userRepository.save(user);

    // 도서 생성 및 저장
    Book book = Book.builder()
        .title("테스트 도서")
        .author("테스트 작가")
        .publisher("테스트 출판사")
        .isbn("9788956609959")
        .publishedDate(LocalDate.now())
        .isDeleted(false)
        .description("테스트 설명")
        .thumbnailUrl("http://example.com/image.jpg")
        .build();
    Book savedBook = bookRepository.save(book);

    // 리뷰 생성 및 저장
    Review review = Review.builder()
        .content("정말 좋은 책이에요")
        .rating(5)
        .likeCount(0)
        .commentCount(0)
        .user(savedUser)
        .book(savedBook)
        .build();
    reviewRepository.save(review);

    // When
    Optional<Review> foundReview = reviewRepository.findByUserIdAndBookId(
        savedUser.getId(), savedBook.getId());

    // Then
    assertThat(foundReview.isPresent()).isTrue();
    assertThat(foundReview.get().getContent()).isEqualTo("정말 좋은 책이에요");
    assertThat(foundReview.get().getRating()).isEqualTo(5);
    assertThat(foundReview.get().getUser().getId()).isEqualTo(savedUser.getId());
    assertThat(foundReview.get().getBook().getId()).isEqualTo(savedBook.getId());
  }

  @Test
  @DisplayName("사용자 ID와 도서 ID로 리뷰 찾기 - 리뷰 없음")
  void findByUserIdAndBookId_reviewNotFound() {
    // Given
    // 사용자 생성 및 저장
    User user = User.builder()
        .email("test2@example.com")
        .nickname("테스터2")
        .password("password")
        .isDeleted(false)
        .build();
    User savedUser = userRepository.save(user);

    // 도서 생성 및 저장
    Book book = Book.builder()
        .title("테스트 도서2")
        .author("테스트 작가2")
        .publisher("테스트 출판사2")
        .isbn("9788956609958")
        .publishedDate(LocalDate.now())
        .isDeleted(false)
        .description("테스트 설명2")
        .thumbnailUrl("http://example.com/image2.jpg")
        .build();
    Book savedBook = bookRepository.save(book);

    // 리뷰는 저장하지 않음

    // When
    Optional<Review> foundReview = reviewRepository.findByUserIdAndBookId(
        savedUser.getId(), savedBook.getId());

    // Then
    assertThat(foundReview.isPresent()).isFalse();
  }
}
