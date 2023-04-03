import React, { useEffect, useState } from 'react';
import { Map, MapMarker, MapTypeId } from 'react-kakao-maps-sdk';
import { useNavigate } from 'react-router-dom';

function MapContainerMain(searchPlace: any) {
  // 검색결과 담기
  const [Place, setPlace] = useState('');

  // 인포윈도우 관리
  const [isOpen, setIsOpen] = useState(false);
  const [state, setState] = useState({
    center: {
      lat: 33.450701,
      lng: 126.570667,
    },
    errMsg: null,
    isLoading: true,
    isPanto: false,
  });

  const navigate = useNavigate();

  useEffect(() => {
    if (navigator.geolocation) {
      // GeoLocation을 이용해서 접속 위치를 얻어옵니다
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setState((prev) => ({
            ...prev,
            center: {
              lat: position.coords.latitude, // 위도
              lng: position.coords.longitude, // 경도
            },
            isLoading: false,
          }));
        },
        (err) => {
          setState((prev: any) => ({
            ...prev,
            errMsg: err.message,
            isLoading: false,
          }));
        },
      );
    } else {
      // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다
      setState((prev: any) => ({
        ...prev,
        errMsg: 'geolocation을 사용할수 없어요..',
        isLoading: false,
      }));
    }

    const ps = new kakao.maps.services.Places();
    ps.keywordSearch(searchPlace, placeSearchCB);

    function placeSearchCB(data: any, status: any) {
      if (status === kakao.maps.services.Status.OK) {
        let bounds = new kakao.maps.LatLngBounds();
      }
    }
  }, []);
  const toMountainDetail = () => {
    navigate('/mountain/detail');
  };

  const toMapCenter = () => {
    setState({
      ...state,
      center: {
        lat: state.center.lat,
        lng: state.center.lng,
      },
      errMsg: null,
      isLoading: false,
      isPanto: true,
    });
    console.log(state);
    console.log(state.center.lat);
    console.log(state.center.lng);
  };

  return (
    <div className="kakao-map">
      <Map
        center={state.center}
        isPanto={state.isPanto}
        style={{ width: '320px', height: '360px', zIndex: '1' }}
        level={6}
      >
        <MapMarker
          position={state.center}
          clickable
          onClick={() => setIsOpen(true)}
        >
          {/* MapMarker의 자식을 넣어줌으로 해당 자식이 InfoWindow로 만들어지게 합니다 */}
          {/* 인포윈도우에 표출될 내용으로 HTML 문자열이나 React Component가 가능합니다 */}
          {isOpen && (
            <div style={{ minWidth: '150px' }}>
              <img
                alt="close"
                width="14"
                height="13"
                src="https://t1.daumcdn.net/localimg/localimages/07/mapjsapi/2x/bt_close.gif"
                style={{
                  position: 'absolute',
                  right: '5px',
                  top: '5px',
                  cursor: 'pointer',
                }}
                role="presentation"
                onClick={() => setIsOpen(false)}
                onKeyDown={() => setIsOpen(false)}
              />
              <div
                style={{ padding: '5px', color: '#000' }}
                role="presentation"
                onClick={toMountainDetail}
                onKeyDown={toMountainDetail}
              >
                도봉산
              </div>
            </div>
          )}
        </MapMarker>
        {/* 지도에 지형정보를 표시하도록 지도타입을 추가합니다 */}
        <MapTypeId type={kakao.maps.MapTypeId.TERRAIN} />
        <button type="button" onClick={toMapCenter}>
          현재위치로 이동
        </button>
        <button
          type="button"
          onClick={() =>
            setState({
              center: {
                lat: state.center.lat,
                lng: state.center.lng,
              },
              errMsg: null,
              isLoading: false,
              isPanto: false,
            })
          }
        >
          ehdeh 이동
        </button>
      </Map>
    </div>
  );
}

export default MapContainerMain;
