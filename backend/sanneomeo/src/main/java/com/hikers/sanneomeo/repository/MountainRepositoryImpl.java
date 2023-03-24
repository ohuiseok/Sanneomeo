package com.hikers.sanneomeo.repository;

import static com.hikers.sanneomeo.domain.QMountain.mountain;

import com.hikers.sanneomeo.dto.response.MountainDetailResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.hikers.sanneomeo.repository fileName       : MountainRepositoryImpl author :
 * SSAFY date           : 2023-03-23 description    :
 * <p>
 * =========================================================== DATE              AUTHOR NOTE
 * -----------------------------------------------------------
 * <p>
 * 2023-03-23        SSAFY       최초 생성
 */
@RequiredArgsConstructor
public class MountainRepositoryImpl implements MountainRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<MountainDetailResponseDto> findMountainBySequence(String sequence) {
    return Optional.ofNullable(
        queryFactory
            .select(
                Projections.constructor(MountainDetailResponseDto.class, mountain.mountainSeq,
                    mountain.latitude, mountain.longitude, mountain.altitude, mountain.si,
                    mountain.gu, mountain.dong, mountain.name, mountain.img, mountain.introduction,
                    mountain.difficulty, mountain.top100, mountain.spring, mountain.summer,
                    mountain.fall, mountain.winter, mountain.sunrise, mountain.sunset)
            )
            .from(mountain)
            .where(mountain.mountainSeq.eq(sequence))
            .fetchFirst()
    );
  }
}
