import React, { useState, useEffect } from 'react';
import { GiHamburgerMenu } from 'react-icons/gi';
import Navbar from '@components/common/Navbar';
import { useNavigate, useLocation } from 'react-router-dom';
import TempLogo from '@assets/images/temp_logo.png'


function Header() {
  const [isNavOpen, setIsNavOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // 해당 페이지로 이동선언하는 함수
  const navigateToMain = () => {
    console.log('go to main!');
    navigate('/');
  };
  useEffect(() => {
    setIsNavOpen(false);
  }, [location.pathname, navigate]);

  const toggleNavHandler = () => {
    setIsNavOpen(!isNavOpen);
  };

  const pageTitle = [
    '들어가기',
    '람쥐의 추천',
    '추천 결과!',
    '찜리스트',
    '나의기록',
    '100대 명산 완등',
    '사진 업로드',
  ];

  return (
    <div className="common-header">
      <div className="header-nav">
          <div
            className="nav-logo"
            role="presentation"
            onClick={navigateToMain}
            onKeyDown={navigateToMain}
          >
            <img src={TempLogo} alt="sanneomeo" />
            <h1>산너머</h1>
          </div>
          <div className={`navbar-wrapper ${isNavOpen ? 'open' : 'hidden'}`}>
            <Navbar isOpen={isNavOpen} onClose={toggleNavHandler} />
          </div>
          <GiHamburgerMenu
            onClick={toggleNavHandler}
            className="cursor-pointer"
            color="white"
            size="30px"
            style={{ zIndex: 9999 }}
          />
        </div>

      {/* <div className="img-background">
        <div className="main-banner">
          <Lottie
          animationData={mountainAnimation}
          >

          </Lottie>

        </div> */}

        {/* page-title 향후 페이지명 동적바인딩  */}
        {/* <div className="page-title">산너머</div> */}
      {/* </div> */}
    </div>
  );
}

export default Header;
