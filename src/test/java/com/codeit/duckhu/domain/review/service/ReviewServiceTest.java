package com.codeit.duckhu.domain.review.service;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.exception.ReviewCustomException;
import com.codeit.duckhu.domain.review.mapper.ReviewMapper;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 리뷰 서비스 테스트 클래스
 * TDD 방식으로 구현 예정
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {


/**
   * 서비스 계층 설계
   *
   * 1. ReviewService
   * - createReview: 리뷰 생성
   * - getReview: ID로 리뷰 조회
   * - updateReview: 리뷰 업데이트
   * - deleteReview: 리뷰 삭제
   * - likeReview: 리뷰 좋아요
   * - getAll: 목록 조회(커서 페이지네이션)
   *
   * 2. DTO 클래스 설계
   * - ReviewDto: 응답 데이터
   * - ReviewCreateRequest: 생성 요청 데이터
   * - ReviewUpdateRequest: 업데이트
   * 요청 데이터 - ReviewLikeDto: 리뷰 좋아요 데이터
   *
   * 3. 서비스 구현체 (ReviewServiceImpl) 생성 - 리포지토리를 통한 CRUD 구현
   */

  @Mock
  private UserRepository userRepository;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private ReviewRepository reviewRepository;
  
  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  private User testUser;
  private UserDto testUserDto;
  private Book testBook;
  private Book testBookDto;
  private Review testReview;
  private ReviewDto testReviewDto;
  private ReviewCreateRequest testCreateRequest;
  private UUID testReviewId;
  private UUID testUserId;
  private UUID testBookId;
  private ReviewUpdateRequest testreviewUpdateRequest;

  @BeforeEach
  void setUp() {
    // 테스트용 ID 생성
    testReviewId = UUID.randomUUID();
    testUserId = UUID.randomUUID();
    testBookId = UUID.randomUUID();

    // 테스트용 유저 엔티티 생성
    testUser = User.builder()
        .nickname("testUsser")
        .email("test@test.com")
        .password("qwer1234")
        .build();

    // 테스트용 유저 DTO 생성
    testUserDto = UserDto.builder()
        .nickname("testUser")
        .email("test@test.com")
        .build();

    // 테스트용 도서 엔티티 생성
    testBook = Book.builder()
        .title("테스트 도서")
        .author("테스트 저자")
        .publisher("테스트 출판사")
        .isbn("1234567890123")
        .build();

    // 테스트용 도서 DTO 생성
    testBookDto = Book.builder()
        .title("테스트 도서")
        .author("테스트 저자")
        .publisher("테스트 출판사")
        .isbn("1234567890123")
        .build();

    // 테스트용 리뷰 엔티티 생성
    testReview = Review.builder()
        .content("볼만해요")
        .rating(3)
        .likeCount(0)
        .commentCount(0)
        .user(testUser)
        .book(testBook)
        .build();

    // 테스트용 DTO 생성
    testReviewDto = ReviewDto.builder()
        .content("볼만해요")
        .rating(3)
        .commentCount(0)
        .likeCount(0)
        .likedByMe(false)
        .userId(testUserId)
        .bookId(testBookId)
        .build();
        
    // 테스트용 Create 요청 생성
    testCreateRequest = ReviewCreateRequest.builder()
        .userId(testUserId)
        .bookId(testBookId)
        .content("볼만해요")
        .rating(3)
        .build();

    // 테스트용 Update 요청 생성
    testreviewUpdateRequest = ReviewUpdateRequest.builder()
        .content("재밌어요")
        .rating(5)
        .userId(testUserId)
        .bookId(testBookId)
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 성공")
  void createReview_shouldCreateReview() {
    // Given
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
    when(bookRepository.findById(any(UUID.class))).thenReturn(Optional.of(testBook));
    // 리뷰가 존재하지 않는 경우
    when(reviewRepository.findByUserIdAndBookId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());
    when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
    when(reviewMapper.toDto(any(Review.class))).thenReturn(testReviewDto);
    when(reviewMapper.toEntity(any(ReviewCreateRequest.class), any(User.class), any(Book.class))).thenReturn(testReview);

    // When
    ReviewDto result = reviewService.createReview(testCreateRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRating()).isEqualTo(testCreateRequest.getRating());
    assertThat(result.getContent()).isEqualTo(testCreateRequest.getContent());
    assertThat(result.getLikeCount()).isEqualTo(0);
    assertThat(result.getCommentCount()).isEqualTo(0);
    assertThat(result.getUserId()).isEqualTo(testUserId);
    assertThat(result.getBookId()).isEqualTo(testBookId);
    verify(userRepository).findById(any(UUID.class));
    verify(bookRepository).findById(any(UUID.class));
    verify(reviewRepository).findByUserIdAndBookId(any(UUID.class), any(UUID.class));
    verify(reviewMapper).toEntity(any(ReviewCreateRequest.class), any(User.class), any(Book.class));
    verify(reviewRepository).save(any(Review.class));
    verify(reviewMapper).toDto(any(Review.class));
  }

  @Test
  @DisplayName("ID로 리뷰 조회 테스트")
  void getReviewById_shouldReturnReview() {
    // Given : 저장된 리뷰를 찾았다고 가정, 엔티티를 Dto로 변환
    when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(testReview));
    when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);
    
    // When : id로 리뷰 찾기
    ReviewDto result = reviewService.getReviewById(testReviewId);
    
    // Then : null이 아니여야 하고, content, rating 검증
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo("볼만해요");
    assertThat(result.getRating()).isEqualTo(3);
    assertThat(result.getUserId()).isEqualTo(testUserId);
    assertThat(result.getBookId()).isEqualTo(testBookId);
  }

  @Test
  @DisplayName("ID로 리뷰 삭제 테스트")
  void deleteReviewById_shouldReturnSuccess() {
    // Given: findById 리턴과 delete 설정
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    willDoNothing().given(reviewRepository).delete(testReview);

    // When : 서비스 호출 시 예외가 나지 않아야 하고
    assertDoesNotThrow(() -> reviewService.deleteReviewById(testReviewId));

    // Then: repository.findById + repository.delete 가 호출됐는지 검증, 불필요한 추가 호출이 없는지도 검증
    verify(reviewRepository).findById(testReviewId);
    verify(reviewRepository).delete(testReview);
    verifyNoMoreInteractions(reviewRepository);
  }

  @Test
  @DisplayName("리뷰 업데이트 테스트")
  void updateReview_shouldReturnUpdateReview() {
    // Given
    String updatedContent = "재밌어요";
    int updatedRating = 5;

    // 테스트를 위한 Mock 객체 생성
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getId()).thenReturn(testUserId);
    
    Review mockReview = Mockito.mock(Review.class);
    when(mockReview.getUser()).thenReturn(mockUser);
    
    Review updatedReview = Mockito.mock(Review.class);
    
    // 기본 모킹 설정
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(mockReview));
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
    when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
    when(reviewMapper.toDto(any(Review.class))).thenReturn(testReviewDto);

    // When
    ReviewDto result = reviewService.updateReview(testReviewId, testreviewUpdateRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(testReviewDto);
    verify(userRepository).findById(testUserId);
    verify(reviewRepository).findById(testReviewId);
    verify(mockReview).updateContent(updatedContent);
    verify(mockReview).updateRating(updatedRating);
    verify(reviewRepository).save(mockReview);
    verify(reviewMapper).toDto(any(Review.class));
  }

  @Test
  @DisplayName("이미 도서에 리뷰가 존재하는 경우 예외가 발생해야 함")
  void createReview_shouldThrowException_whenReviewAlreadyExists() {
    // Given
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
    when(bookRepository.findById(any(UUID.class))).thenReturn(Optional.of(testBook));
    
    // 이미 리뷰가 존재하는 상황 모킹
    when(reviewRepository.findByUserIdAndBookId(testUserId, testBookId))
        .thenReturn(Optional.of(testReview));
    
    // When & Then
    assertThrows(ReviewCustomException.class, () -> {
      reviewService.createReview(testCreateRequest);
    });
    
    // 리뷰 저장이 호출되지 않아야 함
    verify(userRepository).findById(any(UUID.class));
    verify(bookRepository).findById(any(UUID.class));
    verify(reviewRepository).findByUserIdAndBookId(any(UUID.class), any(UUID.class));
    verify(reviewRepository, never()).save(any(Review.class));
  }
}

