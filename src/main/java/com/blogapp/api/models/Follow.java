package com.blogapp.api.models;

import com.blogapp.api.models.compositeKey.FollowId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "follow")
@IdClass(FollowId.class)
public class Follow {

    @Id
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Id
    @Column(name = "following_id", nullable = false)
    private Long followingId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity following;
}
