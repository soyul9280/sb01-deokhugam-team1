//package com.codeit.duckhu.global.devdata;
//
//import com.codeit.duckhu.domain.book.entity.Book;
//import com.codeit.duckhu.domain.book.repository.BookRepository;
//import com.codeit.duckhu.domain.review.entity.LikedUserId;
//import com.codeit.duckhu.domain.review.entity.Review;
//import com.codeit.duckhu.domain.review.repository.ReviewRepository;
//import com.codeit.duckhu.domain.user.entity.User;
//import com.codeit.duckhu.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Component
//@Profile("dev") // local 대신 dev 쓴다면 여기 맞춰줘야 함
//@RequiredArgsConstructor
//public class DummyDataLoader implements CommandLineRunner {
//
//  private final UserRepository userRepository;
//  private final BookRepository bookRepository;
//  private final ReviewRepository reviewRepository;
//  private final LikedUserIdRepository likedUserIdRepository;
//
//  @Override
//  public void run(String... args) {
//    try {
//      System.out.println("🔥 DummyDataLoader 실행됨");
//
//      if (userRepository.findByEmail("test@duckhu.com").isPresent()) {
//        System.out.println("🚫 이미 생성된 더미 유저가 있음. Skip");
//        return;
//      }
//
//      User user = userRepository.save(User.builder()
//          .email("test@duckhu.com")
//          .nickname("testuser")
//          .password("pw")
//          .build());
//
//      UUID userId = user.getId();
//      List<Review> allReviews = new ArrayList<>();
//
//      for (int i = 1; i <= 1000; i++) {
//        Book book = bookRepository.save(Book.builder()
//            .title("Book " + i)
//            .author("Author " + i)
//            .description("Description of book " + i)
//            .publisher("Publisher " + i)
//            .publishedDate(LocalDate.now().minusDays(i))
//            .isbn("ISBN-" + i)
//            .thumbnailUrl("http://dummyimg.com/" + i)
//            .build());
//
//        Review review = reviewRepository.save(Review.builder()
//            .user(user)
//            .book(book)
//            .content("This is review #" + i)
//            .rating((int) (Math.random() * 5 + 1))
//            .isDeleted(false)
//            .build());
//
//        allReviews.add(review);
//      }
//
//      System.out.println("✅ 리뷰 1000개 생성 완료");
//
//      Collections.shuffle(allReviews);
//      List<Review> likedReviews = allReviews.subList(0, 300);
//
//      int success = 0;
//      int skipped = 0;
//
//      for (Review review : likedReviews) {
//        try {
//          likedUserIdRepository.save(LikedUserId.of(review, userId));
//          review.increaseLikeCount(userId);
//          reviewRepository.save(review);
//          success++;
//        } catch (DataIntegrityViolationException e) {
//          System.out.println("⚠️ 중복 좋아요 발생 → 무시: reviewId = " + review.getId());
//          skipped++;
//        }
//      }
//
//      System.out.printf("✅ 좋아요 완료 (%d 성공, %d 중복 무시)\n", success, skipped);
//      System.out.println("🎉 더미 데이터 생성 완료");
//
//    } catch (Exception e) {
//      System.out.println("❌ 더미 생성 중 전체 에러 발생");
//      e.printStackTrace();
//    }
//  }
//}