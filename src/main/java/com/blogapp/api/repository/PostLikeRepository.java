package com.blogapp.api.repository;

import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostLike;
import com.blogapp.api.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    long countByPostId(Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserAndPost(UserEntity user, Post post);
}
