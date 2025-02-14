<h1>UI_Project (개인 프로젝트 + 두 번째 팀 프로젝트)</h1>

<h2>프로젝트 소개</h2>
<p>Spring Framework 기반의 웹 애플리케이션으로, 회원 관리 시스템과 데이터 분석 대시보드를 구현한 프로젝트 입니다. Python과 Java의 연동을 통해 데이터 분석 및 시각화 기능을 제공합니다.</p>

<h2>개발 목적</h2>
<ul>
  <li>웹 개발의 전반적인 기술 스택 실습 및 통합</li>
  <li>Python - Java 연동을 통한 데이터 분석 시스템 구현</li>
  <li>학습 내용 정리 및 실무 역량 강화</li>
</ul>

<h2>주요 기능</h2>
<ol>
  <li>회원 관리 시스템
    <ul>
      <li>회원 가입: BCrypt 암호화, 유효성 검증</li>
      <li>로그인/로그아웃: 세션/쿠키 기반 인증</li>
      <li>마이페이지: 회원정보 수정, 탈퇴</li>
    </ul>
  </li>
  <li>데이터 분석 대시보드
    <ul>
      <li>CSV 파일 업로드 및 분석</li>
      <li>Python 기반 데이터 처리</li>
      <li>Chart.js를 활용한 시각화</li>
    </ul>
  </li>
  <li>가스 공급량 수요 예측 시스템
    <ul>
      <li>실시간 가스 공급량 모니터링</li>
      <li>온도-수요 상관관계 분석</li>
      <li>지역별 수요 예측</li>
    </ul>
  </li>
</ol>

<h2>기술 스택</h2>
<ol>
  <li>Backend
    <ul>
      <li>Spring MVC</li>
      <li>MyBatis</li>
      <li>MySQL</li>
      <li>Java 17</li>
      <li>Python (API 데이터 분석)</li>
    </ul>
  </li>
  <li>Frontend
    <ul>
      <li>Thymeleaf</li>
      <li>JavaScript/jQuery</li>
      <li>Chart.js</li>
    </ul>
  </li>
  <li>개발 도구
    <ul>
      <li>Spring Tool Suite4</li>
      <li>VSCode</li>
      <li>Maven</li>
      <li>Git/GitHub</li>
    </ul>
  </li>
</ol>

<h2>주요 구현 사항</h2>
<ol>
  <li>데이터베이스 연동
    <ul>
      <li>HikariCP 커넥션 풀 사용</li>
      <li>MyBatis를 통한 효율적인 쿼리 관리</li>
      <li>트랜잭션 관리 및 예외 처리</li>
    </ul>
  </li>
  <li>보안
    <ul>
      <li>BCrypt 기반 비밀번호 암호화</li>
      <li>XSS 방지</li>
      <li>SQL Injection 대비</li>
    </ul>
  </li>
  <li>Python 연동
    <ul>
      <li>ProcessBuilder를 통한 Python 스크립트 실행</li>
      <li>JSON 기반 데이터 교환</li>
      <li>비동기 처리로 성능 최적화</li>
    </ul>
  </li>
</ol>
