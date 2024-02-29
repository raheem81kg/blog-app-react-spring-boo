package com.blogapp.api.repository;

import com.blogapp.api.models.Follow;
import com.blogapp.api.models.compositeKey.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    long countByFollowingId(Long followingId);

    @Query("SELECT p.followingId FROM Follow p WHERE p.followerId = :followerId")
    List<Long> getFollowedUserIdsByFollowerId(@Param("followerId") Long followerId);


    long countByFollowerId(Long followerId);
}

