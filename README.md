<h1>DeveloperProject (첫 번째 팀 프로젝트)</h1> 
Spring Framework를 활용한 가스 요금 조회 및 납부 내역 관리 시스템입니다.

<h2>프로젝트 구조</h2>
<code>
DeveloperProject
├─ src/main/java                        # Java 소스 파일 디렉토리
│  ├─ com.developer.controller          # 컨트롤러 패키지
│  │  ├─ MainController.java           # 메인 페이지 및 요금 조회 컨트롤러
│  │  ├─ MemberController.java         # 회원 관리 컨트롤러 (로그인, 회원가입 등)
│  │  └─ PaymentReceiptController.java # 납부 내역 관리 컨트롤러
│  ├─ com.developer.dao                # 데이터 액세스 객체 패키지
│  ├─ com.developer.dto                # 데이터 전송 객체 패키지
│  └─ com.developer.service            # 비즈니스 로직 서비스 패키지
│
├─ src/main/resources                   # 리소스 파일 디렉토리
│  ├─ com.developer.dao                # MyBatis 매퍼 XML 파일 디렉토리
│  │  ├─ BillingMapper.xml            # 요금 조회 관련 SQL 매핑
│  │  ├─ MemberMapper.xml             # 회원 관리 관련 SQL 매핑
│  │  └─ PaymentReceiptMapper.xml     # 납부 내역 관련 SQL 매핑
│  └─ log4j.xml                       # 로깅 설정 파일
│
└─ src/main/webapp                      # 웹 애플리케이션 루트 디렉토리
   ├─ WEB-INF                          # 웹 설정 디렉토리
   │  ├─ spring                        # 스프링 설정 파일 디렉토리
   │  ├─ views                         # JSP 뷰 파일 디렉토리
   │  │  ├─ detail                    # 상세 페이지 뷰
   │  │  │  ├─ PaymentReceipt.jsp    # 납부 내역 출력 페이지
   │  │  │  └─ view_details.jsp      # 상세 정보 조회 페이지
   │  │  ├─ main                      # 메인 페이지 관련 뷰
   │  │  │  └─ main.jsp              # 메인 페이지
   │  │  └─ member                    # 회원 관련 뷰
   │  │      ├─ alertPrint.jsp       # 알림 메시지 출력 페이지
   │  │      ├─ header.jsp           # 공통 헤더 페이지
   │  │      ├─ join.jsp             # 회원가입 페이지
   │  │      └─ login.jsp            # 로그인 페이지
   │  └─ web.xml                      # 웹 애플리케이션 설정 파일
   └─ resources                        # 정적 리소스 디렉토리 (CSS, JS, 이미지 등)
</code>

<h2>주요 기능</h2>
<ul>
   <li>회원 관리
      <ul>
         <li>회원가입: 아이디 중복 체크, 유효성 검증</li>
         <li>로그인/로그아웃: 세션 관리, Remember Me 쿠키 지원</li>
         <li>회원탈퇴: 계정 삭제 및 세션 정리</li>
      </ul>
   </li>
   <li>요금 조회
      <ul>
         <li>사용계약번호와 청구년월 기준 요금 조회</li>
         <li>사용량, 공급가액, 부가세, 연체료 등 상세 내역 표시</li>
         <li>Chart.js를 활용한 요금 구성 시각화</li>
      </ul>
   </li>
   <li>납부 내역 관리
      <ul>
         <li>연간 납부 내역 조회 및 출력</li>
         <li>고객명, 계약번호 기반 조회</li>
         <li>납부확인서 PDF 출력</li>
      </ul>
   </li>
</ul>

<h2>기술 스택</h2>
<ol>
  <li>Backend
    <ul>
      <li>Spring MVC</li>
      <li>MyBatis</li>
      <li>MySQL (데이터베이스)</li>
      <li>Java 11</li>
    </ul>
  </li>
  <li>Frontend
    <ul>
      <li>JSP</li>
      <li>HTML/CSS/JavaScript</li>
      <li>jQuery(ajax사용)</li>
      <li>Chart.js (차트 시각화)</li>
    </ul>
  </li>
  <li>ETC
    <ul>
      <li>Apache Tomcat (서버)</li>
      <li>Maven(의존성 관리)</li>
      <li>Spring Tool Suite 3(사용 툴)</li>
      <li>Visual Studio Code(사용 툴)</li>
    </ul>
  </li>
</ol>

<h2>맡은 역할</h2>
<ul>
   <li>백엔드
      <ul>
         <li>회원가입/로그인</li>
         <li>DB와 Java연결</li>
      </ul>
   </li>
</ul>

<hr>

<h1>UI_Project (개인 프로젝트 + 두 번째 팀 프로젝트)</h1> 
Spring Framework, MyBatis, Java, JSP, JSTL, MySQL, JavaScript, jQuery 등을 활용하여 구현된 웹 프로젝트입니다. 주요 기능은 회원 가입/로그인, 마이페이지, 데이터 분석 (Python 연동), 가스 대시보드 등으로 구성되어 있습니다.

<hr>
<h2>프로젝트 개요</h2>
<ol>
  <li>개발 목적
    <ul>
      <li>HTML, CSS, JavaScript, Java, MySQL, Python, JSP, Spring, Github등 배운것을 기록하고, 활용하기 위함.</li>
      <li>웹 서비스의 전반적인 아키텍처(프론트엔드 & 백엔드 & DB)를 이해.</li>
      <li>Python과 Spring(자바) 간의 연동(데이터 분석 및 시각화)</li>
      <li>내가 배운 것을 정리 및 기록하는 공간</li>
    </ul>
  </li>
  <li>주요 특징:
    <ul>
      <li>세션과 쿠키를 이용한 로그인/로그아웃</li>
      <li>회원 가입, 정보수정, 탈퇴 기능</li>
      <li>Python 스크립트(머신러닝, 데이터 분석) 연동</li>
      <li>가스수급 예측 대시보드 시각화(Chart.js 활용)</li>
    </ul>
  </li>
</ol>

<hr>
<h2>기술 스택</h2>
<ol>
  <li>Backend
    <ul>
      <li>Spring MVC</li>
      <li>MyBatis</li>
      <li>MySQL (데이터베이스)</li>
      <li>Java 11</li>
    </ul>
  </li>
  <li>Frontend
    <ul>
      <li>JSP</li>
      <li>HTML/CSS/JavaScript</li>
      <li>jQuery(ajax사용)</li>
      <li>Chart.js (차트 시각화)</li>
    </ul>
  </li>
  <li>ETC
    <ul>
      <li>Python (데이터 분석 및 머신러닝)</li>
      <li>Apache Tomcat (서버)</li>
      <li>Maven(의존성 관리)</li>
      <li>Spring Tool Suite 3(사용 툴)</li>
      <li>Visual Studio Code(사용 툴)</li>
    </ul>
  </li>
</ol>

<hr>
<h2>프로젝트 구조</h2>
<code>
UI_Project
├─ src
│  └─ main
│     ├─ java
│     │  └─ com
│     │     └─ UI
│     │        ├─ controller          // 컨트롤러(Controller) 클래스들
│     │        ├─ dao                 // DAO(Data Access Object) 클래스들
│     │        ├─ domain              // DTO/VO 등 도메인 객체
│     │        ├─ service             // 서비스(Service) 클래스들
│     │        └─ utills              // 기타 유틸리티 클래스(ex: PythonExecutor)
│     ├─ resources
│     │  └─ com
│     │     └─ UI
│     │        └─ dao
│     │           └─ MemberMapper.xml // MyBatis 매퍼
│     │  ├─ META-INF                  // (기타 설정파일 위치)
│     │  └─ log4j.xml                 // 로그(log4j) 설정 파일
│     └─ webapp
│        ├─ resources
│        │  ├─ CSS                    // CSS 파일
│        │  ├─ img                    // 이미지 파일
│        │  ├─ JavaScript            // 자바스크립트 파일
│        │  ├─ languageContents      // 각 언어/기술 콘텐츠(HTML 등)
│        │  ├─ LanguageContentsExcelFile // 엑셀 파일
│        │  ├─ Python                // Python 스크립트
│        │  └─ temp                  // 임시 파일 디렉터리
│        └─ WEB-INF
│           ├─ classes               // 컴파일된 클래스(.class) 파일
│           ├─ spring                // 스프링 설정 파일(xml) 등
│           ├─ views                 // JSP 파일 등 뷰(View) 관련
│           └─ web.xml               // 배포 디스크립터(DD)
└── (기타) Maven Dependencies / Deployed Resources 등 IDE가 생성한 항목
</code>

<hr>
<h2>주요 기능</h2>
<ol>
  <li>학습자료
    <ul>
      <li>설명
        <ul>
          <li>Java / JSP / CSS / HTML / JavaScript / Python / MySQL / Spring / Github 등의 학습자료를 메뉴별로 정리하여 클릭 시 해당 콘텐츠를 불러오도록 구현.</li>
        </ul>
      </li>
      <li>기능
        <ul>
          <li>BookController.java에서 각 카테고리별 매핑(@GetMapping)으로 'CommandCenter(HTML부터 github중 선택)' 페이지 이동</li>
          <li>index.jsp에서 각 언어(기술) 박스를 클릭하면 /HTML/CommandCenter 등으로 이동</li>
        </ul>
      </li>
    </ul>
  </li>
  <li>회원 관리 (가입/로그인/마이페이지)
      <ol>
        <li>회원가입(join.jsp)
          <ul>
            <li>MemberController의 /join GET 요청 시 페이지 반환</li>
            <li>아이디 중복 체크 AJAX 요청 (/join/idOverlap)</li>
            <li>정규표현식을 통한 입력값 유효성 검사</li>
            <li>회원가입 처리 후 alertPrint.jsp로 이동 → 자동 리다이렉트</li>
          </ul>
        </li>
        <li>로그인(login.jsp)
          <ul>
            <li>MemberController의 /login GET 요청 시 페이지 반환</li>
            <li>login.js에서 폼 유효성 체크</li>
            <li>로그인 성공 시 세션에 id 저장</li>
            <li>Remember Me(아이디 저장) 체크박스 사용 시 쿠키 생성</li>
          </ul>
        </li>
        <li>마이페이지(myPage.jsp)
          <ul>
            <li>세션에서 가져온 id 기반으로 DB 조회 후 사용자 정보 표시</li>
            <li>정보 수정 기능(실제 구현 시 update 메서드 필요)</li>
            <li>회원 탈퇴 버튼 클릭 시 AJAX 요청(WithdrawalMembership() 함수)</li>
          </ul>
        </li>
        <li>
          <code>
            create table member(
            	id varchar(20) PRIMARY KEY
                , pw VARCHAR(20) NOT NULL
                , email VARCHAR(50) NOT NULL
                , name VARCHAR(20) NOT NULL
                , birth DATETIME
                , tel VARCHAR(20) not null
                , gender ENUM('female', 'male')
            );
          </code>
        </li>
      </ol>
  </li>
  <li>데이터 분석 대시보드
      <ul>
        <li>설명
          <ul>
            <li>사용자 업로드 CSV 파일을 Python 코드로 전달하여 그래프(분석 결과)를 받고, 이를 웹 상에서 Chart.js 대신 base64 이미지로 표시.</li>
          </ul>
        </li>
        <li>구현
          <ul>
            <li>projectForecastDashboard.jsp: 첫 번째 박스가 기본으로 보이고, CSV 선택 후 X축·Y축 컬럼 선택</li>
            <li>파일 업로드 후, AJAX로 /dataAnalysis/analyze에 전송</li>
            <li>Python 실행(PythonExecutor) → 분석 결과(base64 이미지) 반환</li>
            <li>JSP에서 동적으로 새 박스를 최대 9개까지 생성</li>
          </ul>
        </li>
      </ul>
  </li>
  <li>가스 대시보드(팀 프로젝트)
      <ul>
        <li>설명
          <ul>
            <li>Python으로 국내 가스 공급량, 온도-수요 상관관계, 회사별 예측오차, 지역별 평균수요 등을 분석 후, Chart.js 차트로 시각화.</li>
          </ul>
        </li>
        <li>구현
          <ul>
            <li>'/GasDashboard' 요청 시 projectGasDashboard.jsp 로 이동</li>
            <li>페이지 로드 시, '/GasDashboard/getGasSupplyData'로 AJAX 요청 → Python 스크립트(GasDashboard.py) 실행</li>
            <li>응답 JSON 데이터를 받아서 여러 Chart.js 차트 생성 (Bar, Line, etc.)</li>
            <li>회사별 예측 수요 및 RMSE를 표 형태로 표시</li>
          </ul>
        </li>
      </ul>
  </li>
</ol>

<h1>Old_team_project3 (세 번째 팀 프로젝트)</h1>
