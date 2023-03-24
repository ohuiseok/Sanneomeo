package com.hikers.sanneomeo.repository;


import static com.hikers.sanneomeo.domain.QUser.user;
import static com.hikers.sanneomeo.domain.QMountain.mountain;
import static com.hikers.sanneomeo.domain.QReview.review;

import com.hikers.sanneomeo.domain.Mountain;
import com.hikers.sanneomeo.domain.User;
import com.hikers.sanneomeo.dto.response.ChallengeResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    private JPAQueryFactory query;


    public Optional<User> findUserBySocialId(String social, String socialId) {

        return Optional.ofNullable(query
                .select(Projections.fields(User.class,
                        user.userSeq, user.level))
                .from(user)
                .where(user.social.eq(social).and(user.socialId.eq(socialId)))
                .fetchOne());

    }

    public List<ChallengeResponseDto> challengeistNotMember() {
        return query
                .select(Projections.fields(ChallengeResponseDto.class, mountain.mountainSeq, mountain.name, mountain.altitude, mountain.si
                        , mountain.gu, mountain.dong, mountain.difficulty))
                .from(mountain)
                .where(mountain.top100.eq(1))
                .fetch();

    }


    public long reviewNum(Long authUserSeq, String mountainSeq) {
        return query
                .select(review.count())
                .from(review)
                .where(review.userSeq.eq(authUserSeq).and(review.mountainSeq.eq(mountainSeq)))
                .fetchOne()
                ;
    }




}
