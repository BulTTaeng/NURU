# NURU

### History

<details>
<summary>접기/펼치기</summary><br>

`2022.07.04`  
- Init Project


`2022.07.05`  
- ListAdapter 적용 
- 리스너 제거
- #4 이슈 발생


`2022.07.06`  
- 사용자 토큰 register 위치 변경(회원가입)
- 코루틴 적용
- CommunityContents 수정
- Viewpager ListAdapter 적용


`2022.07.07`  
- 구글 로그인 문제(admin , farmer) 해결
- 회원탈퇴 기능 추가
- 구글 로그아웃 에러 해결
- Init Refactoring
- Code Convention 적용

`2022.07.08`  
- 데이터 바인딩 작업중
- 회원 탈퇴 이슈 발생 (Github Issue #7)

`2022.07.08`  
- Activity data biding 다 끝냄
- Comments adapter 데이터 바인딩
- CommunityContent ViewModel , ViewModel Factory, Repository 추가

`2022.07.11`  
- communityContents 내부 내용 수정 , 농장 추가 및 삭제 -> onActivityResult로 내용 업데이트
- RecyclerView안에서 터치 이벤트 binding 해제
- Fragment databinding 끝
- onActivityResult로 글쓰면 update
- Community 삭제시 댓글collection도 같이 삭제
- 삭제된 커뮤니티 글 접근 block

`2022.07.12`  
- 회원탈퇴 service+broadCast로 구동, 탈퇴 후 firebase 삭제
- Adapter databinding

`2022.07.13`  
- Community 안에 있는 액티비티를 db접근 로직 분리
- SignupFragment Refactoring 완료
- LoginFragment Refactoring 완료(Google Login 부분 제외)
- View, ViewModel, Repository 분리
- Coroutine 적용

`2022.07.14`  
- SettingFragment Refactoring 완료  
- MyPageFragment Refactoring 완료
- Coroutine 적용
- View, ViewModel, Repository 분리
- EditCommunity activity 와 add community activity 병합
- Activity db 접근 로직 viewMode ,repository로 이전

`2022.07.15`  
- LoginFragment, SettingFragment, CheckTypeGoogleFragment Refactoring 완료
- Coroutine 적용
- View, ViewModel, Repository 분리
- MVVM 패턴 적용
- Issue #10, #14 해결 -> 인증서 문제 (SHA-1)

</details><br>  

--- 

### Description

스마트 임산물 관리  