# 코스 데이터 수집

- 요구사항 분석
    1. 산림청에서 받아온 등산로 데이터는 갈래길마다 있어서, 하나의 코스를 받아올 수가 없었습니다.
    2. 직접 등산로를 연결해서 하나의 코스를 데이터베이스에 넣기로 결심하였습니다.
    3. 직접 눈으로 보고 선택하는 것이 효율이 좋을 것으로 보았습니다. 
- 설계
    1. nodejs를 이용하기로 결심.
    2. Express 프레임워크로 간단하게 만들기로 결심
    3. Mysql의 데이터를 출력하고, insert문을 console.log로 출력하기로 결심
    
- 개발
    1. mysql 패키지를 이용하여 mysql과 연결
    2. html에서 axios로 자기 자신의 웹서버를 연결하도록 설정
    3. 카카오 지도를 이용. 좌표들을 하나의 선으로 연결
    4. 선을 클릭 하면 코스에 포함됨.

- 아쉬운점
    1. 선택한 것들을 바로 DB에 넣거나, txt파일에 넣도록 했으면 더 간편했을 것으로 보입니다.



<b>등산로를 시작점부터 선택하여 하나의 코스를 만듭니다. 그것을 SQL문으로 뽑아서 저장하였습니다.<br/>

![gif](../../img/GIF.gif)
