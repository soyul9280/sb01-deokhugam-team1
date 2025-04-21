package com.codeit.duckhu.domain.user.entity;

import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity @Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "user")
    private List<Review> review = new ArrayList<>();

    public User(String email, String nickname, String password, boolean isDeleted) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isDeleted = isDeleted;
    }
}
