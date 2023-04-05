import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getTrailSpotList, spotList } from '@features/trail/spotSlice';
import { useAppSelector, useAppDispatch } from '@app/hooks';
import { getTrailDetail, trailDetail } from '@features/trail/trailSlice';
import { RiArrowGoBackFill } from 'react-icons/ri';
import MapTrailDetail from '@components/common/MapTrailDetail';

function MountainTrail() {
  const navigate = useNavigate();

  const { trailSeq } = useParams<{ trailSeq: string }>();
  const [parsedTrailSeq, setParsedTrailSeq] = useState(0);
  // console.log(typeof parsedTrailSeq);
  // 등산로 패스 받아오기

  const backHandler = () => {
    navigate(-1);
  };
  const trailListData = useAppSelector(trailDetail);
  const trailListDispatch = useAppDispatch();

  // 등산로 스팟 리스트 받아오기
  const spotListData = useAppSelector(spotList);
  const spotListDispatch = useAppDispatch();
  useEffect(() => {
    setParsedTrailSeq(parseInt(trailSeq ?? '0', 10));

    console.log('parsedtrailseq', parsedTrailSeq);
    // 등산로 trails 가져오기
    trailListDispatch(getTrailDetail(parsedTrailSeq));
    // 등산로 스팟가져오기
    spotListDispatch(getTrailSpotList(parsedTrailSeq));
  }, [trailSeq]);

  return (
    <>
      <h1>지도 페이지</h1>
      <RiArrowGoBackFill onClick={backHandler} />
      <div className="kakao-map-trail">
        <MapTrailDetail
          trailListData={trailListData}
          spotListData={spotListData}
        />
      </div>
    </>
  );
}

export default MountainTrail;
