package com.blogapp.api.repository;

import com.blogapp.api.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(long userId);

    @Query("SELECT p FROM Post p WHERE p.userId IN :userIds ORDER BY p.createdAt DESC")
    Page<Post> getPostsByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);


}
