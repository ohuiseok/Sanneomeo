package com.hikers.sanneomeo.repository;

import static com.hikers.sanneomeo.domain.QMountain.mountain;
import static com.hikers.sanneomeo.domain.QMountainSpot.mountainSpot;
import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;

import com.hikers.sanneomeo.domain.QMountain;
import com.hikers.sanneomeo.dto.response.MountainDetailResponseDto;
import com.hikers.sanneomeo.dto.response.MountainSimpleInfoResponseDto;
import com.hikers.sanneomeo.dto.response.NearMountainResponseDto;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.math.BigDecimal;
import java.util.List;
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

  public List<MountainSimpleInfoResponseDto> seasonMountainList(String season){
    return queryFactory
            .select(Projections.fields(MountainSimpleInfoResponseDto.class, mountain.mountainSeq,
                    mountain.name, mountain.img,mountain.latitude, mountain.si, mountain.gu, mountain.dong, mountain.difficulty))
            .from(mountain)
            .where(Expressions.numberPath(Integer.class,mountain,season).eq(1))
            .fetch();
  }

  @Override
  public Optional<NearMountainResponseDto> findMountainSequenceByDistance(BigDecimal latitude,
      BigDecimal longitude) {
    NumberExpression<Double> distanceExpression = acos(sin(radians(Expressions.constant(latitude)))
        .multiply(sin(radians(mountain.latitude)))
        .add(cos(radians(Expressions.constant(latitude)))
            .multiply(cos(radians(mountain.latitude)))
            .multiply(cos(radians(Expressions.constant(longitude)).subtract(
                radians(mountain.longitude))))
        )).multiply(6371000);
    Path<Double> distancePath = Expressions.numberPath(Double.class, "distance");

    return Optional.ofNullable(
        queryFactory
            .select(
                Projections.constructor(NearMountainResponseDto.class,mountain.mountainSeq
                    , Expressions.as(distanceExpression, distancePath))
            )
            .from(mountain)
            .orderBy(((ComparableExpressionBase<Double>) distancePath).asc())
            .fetchFirst()
    );
  }

}