package com.codeit.duckhu.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.global.type.Direction;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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

  @BeforeEach
  void setUp() {
    // 테스트 전 데이터 초기화
    reviewRepository.deleteAll();
    userRepository.deleteAll();
    bookRepository.deleteAll();
  }

  @Test
  @DisplayName("리뷰 저장 확인")
  void saveReview_shouldSavedReview() {
    // Given
    User user =
        User.builder().email("test333@test.com").nickname("테스터").password("password12!@").build();
    User savedUser = userRepository.save(user);

    Book book =
        Book.builder()
            .title("테스트 도서")
            .author("테스트 작가")
            .publisher("테스트 출판사")
            .isbn("9788956609959")
            .publishedDate(LocalDate.now())
            .build();
    Book savedBook = bookRepository.save(book);

    Review reviewToSave =
        Review.builder()
            .rating(5)
            .content("재밌어요")
            .likeCount(0)
            .commentCount(0)
            .user(savedUser)
            .book(savedBook)
            .build();

    // When
    Review savedReview = reviewRepository.save(reviewToSave);

    // Then
    assertThat(savedReview).isNotNull();
    assertThat(savedReview.getRating()).isEqualTo(5);
    assertThat(savedReview.getContent()).isEqualTo("재밌어요");
    assertThat(savedReview.getLikeCount()).isEqualTo(0);
    assertThat(savedReview.getCommentCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("사용자 ID와 도서 ID로 리뷰 찾기")
  void findByUserIdAndBookId_success() {
    // Given
    // 사용자 생성 및 저장
    User user =
        User.builder()
            .email("test41@example.com")
            .nickname("테스터")
            .password("password12!@")
            .build();
    User savedUser = userRepository.save(user);

    // 도서 생성 및 저장
    Book book =
        Book.builder()
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
    Review review =
        Review.builder()
            .content("정말 좋은 책이에요")
            .rating(5)
            .likeCount(0)
            .commentCount(0)
            .user(savedUser)
            .book(savedBook)
            .build();
    Review savedReview = reviewRepository.save(review);

    // When
    Optional<Review> foundReview =
        reviewRepository.findByUserIdAndBookId(savedUser.getId(), savedBook.getId());

    // Then
    assertThat(foundReview).isPresent();
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
    User user =
        User.builder()
            .email("test2@example.com")
            .nickname("테스터2")
            .password("password")
            .build();
    User savedUser = userRepository.save(user);

    // 도서 생성 및 저장
    Book book =
        Book.builder()
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

    // When
    Optional<Review> foundReview =
        reviewRepository.findByUserIdAndBookId(savedUser.getId(), savedBook.getId());

    // Then
    assertThat(foundReview).isEmpty();
  }

  @Nested
  @DisplayName("커서 페이지네이션")
  class CursorPaginationTests {

    @Test
    @DisplayName("커서 기반 페이지네이션으로 리뷰 목록 조회 (시간순)")
    void findReviewsWithCursor_byCreatedAt_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-cursor1@example.com")
              .nickname("커서테스터1")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("커서 테스트 도서")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609950")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 리뷰 5개 생성
      for (int i = 1; i <= 5; i++) {
        Review review =
            Review.builder()
                .content("커서 페이지네이션 테스트 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);

      }

      // When - 첫 페이지 조회 (3개)
      List<Review> firstPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "createdAt",
              Direction.DESC,
              null,
              null,
              null,
              null,
              3
          );

      // Then - 첫 페이지 검증
      assertThat(firstPage).hasSize(3);

      assertThat(firstPage.get(0).getContent()).contains("커서 페이지네이션 테스트 리뷰 5");
      assertThat(firstPage.get(1).getContent()).contains("커서 페이지네이션 테스트 리뷰 4");
      assertThat(firstPage.get(2).getContent()).contains("커서 페이지네이션 테스트 리뷰 3");

      // 다음 페이지 조회
      Review lastReviewOfFirstPage = firstPage.get(firstPage.size() - 1);
      List<Review> secondPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "createdAt",
              Direction.DESC,
              null,
              null,
              lastReviewOfFirstPage.getId().toString(),
              lastReviewOfFirstPage.getCreatedAt(),
              3
          );

      // Then - 두 번째 페이지 검증
      assertThat(secondPage).hasSize(2);
      assertThat(secondPage.get(0).getContent()).contains("커서 페이지네이션 테스트 리뷰 2");
      assertThat(secondPage.get(1).getContent()).contains("커서 페이지네이션 테스트 리뷰 1");
    }

    @Test
    @DisplayName("커서 기반 페이지네이션으로 리뷰 목록 조회 (평점순)")
    void findReviewsWithCursor_byRating_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-cursor2@example.com")
              .nickname("커서테스터2")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("평점 테스트 도서")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609951")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 리뷰 5개 생성 (평점 5, 4, 3, 2, 1)
      for (int i = 5; i >= 1; i--) {
        Review review =
            Review.builder()
                .content("평점 " + i + "점 테스트 리뷰")
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);

      }

      // When - 평점 기준 내림차순 정렬, 첫 페이지(3개)
      List<Review> firstPage =
          reviewRepository.findReviewsWithCursor(
              null, // 키워드 없음
              "rating", // 평점 기준 정렬
              Direction.DESC, // 내림차순
              null, // 사용자 ID 필터 없음
              null, // 책 ID 필터 없음
              null, // 첫 페이지니까 커서 없음
              null, // 첫 페이지니까 after 없음
              3 // 페이지 크기 3
          );

      // Then - 첫 페이지 검증
      assertThat(firstPage).hasSize(3);
      assertThat(firstPage.get(0).getRating()).isEqualTo(5);
      assertThat(firstPage.get(1).getRating()).isEqualTo(4);
      assertThat(firstPage.get(2).getRating()).isEqualTo(3);

      // 다음 페이지 조회
      Review lastReviewOfFirstPage = firstPage.get(firstPage.size() - 1);
      List<Review> secondPage =
          reviewRepository.findReviewsWithCursor(
              null, // 키워드 없음
              "rating", // 평점 기준 정렬
              Direction.DESC, // 내림차순
              null, // 사용자 ID 필터 없음
              null, // 책 ID 필터 없음
              String.valueOf(lastReviewOfFirstPage.getRating()), // 마지막 리뷰 평점을 커서로 사용
              lastReviewOfFirstPage.getCreatedAt(), // 마지막 리뷰 생성 시간을 after로 사용
              3 // 페이지 크기 3
          );

      // Then
      assertThat(secondPage).hasSize(2);
      assertThat(secondPage.get(0).getRating()).isEqualTo(2);
      assertThat(secondPage.get(1).getRating()).isEqualTo(1);
    }

    @Test
    @DisplayName("키워드로 리뷰 검색 및 커서 페이지네이션")
    void findReviewsWithCursor_withKeyword_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-keyword@example.com")
              .nickname("키워드테스터")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("키워드 테스트 도서")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609952")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 검색될 리뷰 3개 생성
      for (int i = 1; i <= 3; i++) {
        Review review =
            Review.builder()
                .content("검색용 키워드가 포함된 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // 검색되지 않을 리뷰 2개 생성
      for (int i = 1; i <= 2; i++) {
        Review review =
            Review.builder()
                .content("다른 내용의 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When
      List<Review> searchResult =
          reviewRepository.findReviewsWithCursor(
              "검색용 키워드", // 검색 키워드
              "createdAt", // 생성 시간 기준 정렬
              Direction.DESC, // 내림차순
              null, // 사용자 ID 필터 없음
              null, // 책 ID 필터 없음
              null, // 첫 페이지니까 커서 없음
              null, // 첫 페이지니까 after 없음
              10 // 페이지 크기 10
          );

      // Then
      assertThat(searchResult).hasSize(3);
      for (Review review : searchResult) {
        assertThat(review.getContent()).contains("검색용 키워드");
      }
    }

    @Test
    @DisplayName("특정 사용자의 리뷰만 커서 페이지네이션으로 조회")
    void findReviewsWithCursor_byUserId_success() {
      // Given
      // 테스트 사용자 2명 생성
      User user1 =
          User.builder()
              .email("test-user1@example.com")
              .nickname("사용자1")
              .password("password")
              .build();
      User savedUser1 = userRepository.save(user1);

      User user2 =
          User.builder()
              .email("test-user2@example.com")
              .nickname("사용자2")
              .password("password")
              .build();
      User savedUser2 = userRepository.save(user2);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("사용자 필터 테스트 도서")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609953")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 사용자1의 리뷰 3개 생성
      for (int i = 1; i <= 3; i++) {
        Review review =
            Review.builder()
                .content("사용자1의 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser1)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // 사용자2의 리뷰 2개 생성
      for (int i = 1; i <= 2; i++) {
        Review review =
            Review.builder()
                .content("사용자2의 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser2)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When
      List<Review> user1Reviews =
          reviewRepository.findReviewsWithCursor(
              null, // 키워드 없음
              "createdAt", // 생성 시간 기준 정렬
              Direction.DESC, // 내림차순
              savedUser1.getId(), // 사용자1의 ID로 필터링
              null, // 책 ID 필터 없음
              null, // 첫 페이지니까 커서 없음
              null, // 첫 페이지니까 after 없음
              10 // 페이지 크기 10
          );

      // Then
      assertThat(user1Reviews).hasSize(3);
      for (Review review : user1Reviews) {
        assertThat(review.getUser().getId()).isEqualTo(savedUser1.getId());
        assertThat(review.getContent()).contains("사용자1의 리뷰");
      }
    }

    @Test
    @DisplayName("특정 도서의 리뷰만 커서 페이지네이션으로 조회")
    void findReviewsWithCursor_byBookId_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-book@example.com")
              .nickname("도서테스터")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 2개 생성
      Book book1 =
          Book.builder()
              .title("도서1")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609954")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook1 = bookRepository.save(book1);

      Book book2 =
          Book.builder()
              .title("도서2")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609955")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook2 = bookRepository.save(book2);

      // 도서1에 리뷰 3개 생성
      for (int i = 1; i <= 3; i++) {
        Review review =
            Review.builder()
                .content("도서1 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook1)
                .build();
        reviewRepository.save(review);
      }

      // 도서2에 리뷰 2개 생성
      for (int i = 1; i <= 2; i++) {
        Review review =
            Review.builder()
                .content("도서2 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook2)
                .build();
        reviewRepository.save(review);
      }

      // When
      List<Review> book1Reviews =
          reviewRepository.findReviewsWithCursor(
              null, // 키워드 없음
              "createdAt", // 생성 시간 기준 정렬
              Direction.DESC, // 내림차순
              null, // 사용자 ID 필터 없음
              savedBook1.getId(), // 도서1의 ID로 필터링
              null, // 첫 페이지니까 커서 없음
              null, // 첫 페이지니까 after 없음
              10 // 페이지 크기 10
          );

      // Then
      assertThat(book1Reviews).hasSize(3);
      for (Review review : book1Reviews) {
        assertThat(review.getBook().getId()).isEqualTo(savedBook1.getId());
        assertThat(review.getContent()).contains("도서1 리뷰");
      }
    }

    @Test
    @DisplayName("평점 정렬인데 커서 없음, after만 있는 경우")
    void findReview_ratingSort_afterOnly() {
      // Given
      User user = User.builder()
          .email("test-after@example.com")
          .nickname("after테스터")
          .password("password")
          .build();
      User savedUser = userRepository.save(user);

      Book book = Book.builder()
          .title("after 테스트 도서")
          .author("테스트 작가")
          .publisher("테스트 출판사")
          .isbn("9788956609999")
          .publishedDate(LocalDate.now())
          .isDeleted(false)
          .build();
      Book savedBook = bookRepository.save(book);

      // 리뷰 3개 생성
      for (int i = 1; i <= 3; i++) {
        Review review = Review.builder()
            .content("after 테스트 리뷰 " + i)
            .rating(i)
            .likeCount(0)
            .commentCount(0)
            .user(savedUser)
            .book(savedBook)
            .build();
        reviewRepository.save(review);
      }

      // When
      Instant afterTime = Instant.now().minusSeconds(60); // 현재시간보다 1분 전
      List<Review> result = reviewRepository.findReviewsWithCursor(
          null, "rating", Direction.DESC, null, null, null, afterTime, 5
      );

      // Then
      assertThat(result).isNotNull();
      assertThat(result).isNotEmpty();
      assertThat(result.get(0).getRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("잘못된 orderBy 필드가 들어온 경우")
    void findReview_invalidOrderBy() {
      // Given
      User user = User.builder()
          .email("test-invalid@example.com")
          .nickname("invalid테스터")
          .password("password")
          .build();
      User savedUser = userRepository.save(user);

      Book book = Book.builder()
          .title("invalid 테스트 도서")
          .author("테스트 작가")
          .publisher("테스트 출판사")
          .isbn("9788956609991")
          .publishedDate(LocalDate.now())
          .isDeleted(false)
          .build();
      Book savedBook = bookRepository.save(book);

      // 리뷰 생성
      Review review = Review.builder()
          .content("invalid 테스트 리뷰")
          .rating(5)
          .likeCount(0)
          .commentCount(0)
          .user(savedUser)
          .book(savedBook)
          .build();
      Review savedReview = reviewRepository.save(review);

      // When
      List<Review> result = reviewRepository.findReviewsWithCursor(
          null, "invalidField", Direction.DESC, null, null, null, null, 5
      );

      // Then
      assertThat(result).isNotNull();
      assertThat(result).isNotEmpty();
      assertThat(result).contains(savedReview);
    }

    @Test
    @DisplayName("생성시간 오름차순(ASC) 정렬 및 커서 페이지네이션")
    void findReviewsWithCursor_byCreatedAtAsc_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-cursor-asc@example.com")
              .nickname("커서테스터ASC")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("커서 테스트 도서 ASC")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609953")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 리뷰 5개 생성
      for (int i = 1; i <= 5; i++) {
        Review review =
            Review.builder()
                .content("생성시간 오름차순 테스트 리뷰 " + i)
                .rating(i)
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When - 첫 페이지 조회 (3개)
      List<Review> firstPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "createdAt",
              Direction.ASC,
              null,
              null,
              null,
              null,
              3
          );

      // Then - 첫 페이지 검증 (오름차순이므로 1, 2, 3이 나와야 함)
      assertThat(firstPage).hasSize(3);
      assertThat(firstPage.get(0).getContent()).contains("생성시간 오름차순 테스트 리뷰 1");
      assertThat(firstPage.get(1).getContent()).contains("생성시간 오름차순 테스트 리뷰 2");
      assertThat(firstPage.get(2).getContent()).contains("생성시간 오름차순 테스트 리뷰 3");

      // 다음 페이지 조회
      Review lastReviewOfFirstPage = firstPage.get(firstPage.size() - 1);
      List<Review> secondPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "createdAt",
              Direction.ASC,
              null,
              null,
              lastReviewOfFirstPage.getId().toString(),
              lastReviewOfFirstPage.getCreatedAt(),
              3
          );

      // Then - 두 번째 페이지 검증
      assertThat(secondPage).hasSize(2);
      assertThat(secondPage.get(0).getContent()).contains("생성시간 오름차순 테스트 리뷰 4");
      assertThat(secondPage.get(1).getContent()).contains("생성시간 오름차순 테스트 리뷰 5");
    }

    @Test
    @DisplayName("평점 오름차순(ASC) 정렬 및 커서 페이지네이션")
    void findReviewsWithCursor_byRatingAsc_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-rating-asc@example.com")
              .nickname("평점테스터ASC")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("평점 테스트 도서 ASC")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609954")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 평점이 다른 리뷰 5개 생성 (순서를 섞어서 생성)
      int[] ratings = {3, 1, 5, 2, 4};
      for (int i = 0; i < 5; i++) {
        Review review =
            Review.builder()
                .content("평점 오름차순 테스트 리뷰 " + ratings[i])
                .rating(ratings[i])
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When - 첫 페이지 조회 (3개)
      List<Review> firstPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "rating",
              Direction.ASC,
              null,
              null,
              null,
              null,
              3
          );

      // Then - 첫 페이지 검증 (오름차순이므로 평점 1, 2, 3이 나와야 함)
      assertThat(firstPage).hasSize(3);
      assertThat(firstPage.get(0).getRating()).isEqualTo(1);
      assertThat(firstPage.get(1).getRating()).isEqualTo(2);
      assertThat(firstPage.get(2).getRating()).isEqualTo(3);

      // 다음 페이지 조회
      Review lastReviewOfFirstPage = firstPage.get(firstPage.size() - 1);
      List<Review> secondPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "rating",
              Direction.ASC,
              null,
              null,
              String.valueOf(lastReviewOfFirstPage.getRating()),
              lastReviewOfFirstPage.getCreatedAt(),
              3
          );

      // Then - 두 번째 페이지 검증
      assertThat(secondPage).hasSize(2);
      assertThat(secondPage.get(0).getRating()).isEqualTo(4);
      assertThat(secondPage.get(1).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("평점 내림차순(DESC) 정렬 및 커서 페이지네이션")
    void findReviewsWithCursor_byRatingDesc_success() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-rating-desc@example.com")
              .nickname("평점테스터DESC")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("평점 테스트 도서 DESC")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609955")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 평점이 다른 리뷰 5개 생성 (순서를 섞어서 생성)
      int[] ratings = {3, 1, 5, 2, 4};
      for (int i = 0; i < 5; i++) {
        Review review =
            Review.builder()
                .content("평점 내림차순 테스트 리뷰 " + ratings[i])
                .rating(ratings[i])
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When - 첫 페이지 조회 (3개)
      List<Review> firstPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "rating",
              Direction.DESC,
              null,
              null,
              null,
              null,
              3
          );

      // Then - 첫 페이지 검증 (내림차순이므로 평점 5, 4, 3이 나와야 함)
      assertThat(firstPage).hasSize(3);
      assertThat(firstPage.get(0).getRating()).isEqualTo(5);
      assertThat(firstPage.get(1).getRating()).isEqualTo(4);
      assertThat(firstPage.get(2).getRating()).isEqualTo(3);

      // 다음 페이지 조회
      Review lastReviewOfFirstPage = firstPage.get(firstPage.size() - 1);
      List<Review> secondPage =
          reviewRepository.findReviewsWithCursor(
              null,
              "rating",
              Direction.DESC,
              null,
              null,
              String.valueOf(lastReviewOfFirstPage.getRating()),
              lastReviewOfFirstPage.getCreatedAt(),
              3
          );

      // Then - 두 번째 페이지 검증
      assertThat(secondPage).hasSize(2);
      assertThat(secondPage.get(0).getRating()).isEqualTo(2);
      assertThat(secondPage.get(1).getRating()).isEqualTo(1);
    }

    @Test
    @DisplayName("평점이 동일한 경우 생성 시간으로 정렬 (평점 오름차순)")
    void findReviewsWithCursor_byRatingAsc_sameRating_sortByCreatedAt() {
      // Given
      // 테스트 사용자 생성
      User user =
          User.builder()
              .email("test-rating-same@example.com")
              .nickname("평점동일테스터")
              .password("password")
              .build();
      User savedUser = userRepository.save(user);

      // 테스트 도서 생성
      Book book =
          Book.builder()
              .title("평점 동일 테스트 도서")
              .author("테스트 작가")
              .publisher("테스트 출판사")
              .isbn("9788956609956")
              .publishedDate(LocalDate.now())
              .isDeleted(false)
              .build();
      Book savedBook = bookRepository.save(book);

      // 평점이 동일한 리뷰 3개 생성
      for (int i = 1; i <= 3; i++) {
        Review review =
            Review.builder()
                .content("평점 동일 테스트 리뷰 " + i)
                .rating(3) // 평점 모두 3으로 동일
                .likeCount(0)
                .commentCount(0)
                .user(savedUser)
                .book(savedBook)
                .build();
        reviewRepository.save(review);
      }

      // When - 오름차순 조회
      List<Review> ascResults =
          reviewRepository.findReviewsWithCursor(
              null,
              "rating",
              Direction.ASC,
              null,
              null,
              null,
              null,
              3
          );

      // Then - 평점이 같으므로 생성 시간 오름차순으로 정렬되어야 함
      assertThat(ascResults).hasSize(3);
      assertThat(ascResults.get(0).getContent()).contains("평점 동일 테스트 리뷰 1");
      assertThat(ascResults.get(1).getContent()).contains("평점 동일 테스트 리뷰 2");
      assertThat(ascResults.get(2).getContent()).contains("평점 동일 테스트 리뷰 3");
    }
  }
}