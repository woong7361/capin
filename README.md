<h1 align="center"> ☕📍오늘 스터디카페는 여기로 Pin 하자!📌📖</h1>
<br>
<p align='center'>
  <img width='70%' src='https://user-images.githubusercontent.com/100892492/172284953-1e994f7f-655f-42cb-babd-29d6ca932fdd.png'>
</p>
<p align='center'>
  <img src='https://img.shields.io/badge/React-v18.1.0-blue?logo=React'/>
   <img src='https://img.shields.io/badge/ReactRouter-v6.3.0-pink?logo=React Router'/>
  <img src='https://img.shields.io/badge/TypeScript-v4.4.2-blue?logo=TypeScript'/>
   <img src='https://img.shields.io/badge/RTK-v1.8.1-blueviolet?logo=Redux'/>
  <img src='https://img.shields.io/badge/StyledComponents-v^5.3.5-violet?logo=styled-components'/>
  <br>
  <img src="https://img.shields.io/badge/AWS S3-569A31">
  <img src="https://img.shields.io/badge/Route53-E68B49">
  <img src='https://img.shields.io/badge/CloudFront-red?'/>
  
  
</p>
<br>


## 🔭 둘러보기
- Capin link: https://capin.shop
- Front-End GitHub Repository : https://github.com/Hyung-Keun/CaPin_FE
- Back-End GitHub Respository : https://github.com/woong7361/hanghae99_final
- Project Presentation Video : youtube
 <details>
  <summary> Project Demo Video </summary>
  <br>
  
  https://user-images.githubusercontent.com/100892492/172292751-26da2a0e-5374-4f85-a256-25608b3b9de6.mp4
  
</details>

<br>

## 🗓 Project Duration
- April. 29. 2022. ~ June. 03. 2022. (about 5 weeks)

<br>

## Introduce: Capin 
### 
- 코로나로 인한 사회적 거리두기가 점차 해제됨에 따라, 밖에서 다른 사람들과 함께 공부를 하고싶은 사람들을 대상으로, 스터디를 모집하는 과정에서 모집 후 카카오톡으로 이동하는등 불필요한 이동이 많고 스터디를 진행할 위치를 찾는 과정이 조율이 잘 되지않는 점을 착안해 서비스를 기획하게 되었습니다.

<br>

## 📡 Main Function
### 1. 사용자 위치 확인 / 스터디카페 추천
> + Kakao Maps API의 geolocation을 이용해 GPS 기능으로 사용자의 현재 위치를 표시합니다.
> + 현재위치를 바탕으로, 사용자 주변의 스터디카페들의 정보들을 Kakao Maps API로 부터 받아와서 보여줍니다.
> <details>
>  <summary>  메인화면에, 사용자가 등록한 스터디그룹과 추천 스터디카페들의 정보들을 볼 수 있습니다.</summary>
>  <br>
>  <img width='300px' src='https://user-images.githubusercontent.com/100892492/172299150-9fa7cc05-f7ca-49f3-a9ab-566d08dd7b89.png'>
> </details>
### 2. 주소 검색 / 중간장소 찾아주기
> + Kakao 주소검색 API를 활용하여 사용자가 원하는 주소를 검색하고, 주소의 위치를 위 · 경도값으로 저장합니다.
> + 같은 스터디 그룹내의 사용자들이 입력한 주소들의 위 · 경도값을 바탕으로, 지도 위에 각 사용자들의 중간에 위치한 스터디카페들을 추천해줍니다.

> <details>
>  <summary>  카카오 지도를 기반으로 그룹내의 사용자가 입력한 주소를 기반으로, 중간에 위치한 스터디카페들을 보여줍니다.</summary>
>  <br>
>
>  <img width='300px' src='https://user-images.githubusercontent.com/100892492/172300482-9201fa8d-4942-4a55-9d80-e2f5e922a482.png'>
>  <img width='300px' src='https://user-images.githubusercontent.com/100892492/172300015-c2856661-eaa7-4ec0-b1ea-3b17dceb680e.png'>
> </details>

<br>


## 🏛 프로젝트 구조

  <summary>Service Architecture</summary>
  <br>
  <img width="70%" src="https://user-images.githubusercontent.com/100892492/172302213-51b9b148-0649-4225-9635-39f8b40572b6.png">

<details markdown="1">
  <summary>API 명세서</summary>
  <br>
  <img width="650px" alt="스크린샷 2022-04-05 오후 9 34 44" src="https://user-images.githubusercontent.com/97425636/161757957-d4122d8f-7117-41ec-88e3-b70a6f86bd68.png">
  <img width="650px" alt="스크린샷 2022-04-05 오후 9 36 01" src="https://user-images.githubusercontent.com/97425636/161758708-d9fd4867-47d2-48de-b649-d695561c1efe.png">
  <img width="650px" alt="스크린샷 2022-04-05 오후 9 37 06" src="https://user-images.githubusercontent.com/97425636/161758776-4e4dc203-70bb-4f9d-895e-6ce43a29d703.png">
  <img width="650px" alt="스크린샷 2022-04-05 오후 9 33 40" src="https://user-images.githubusercontent.com/97425636/161758851-e1712c27-1c00-47ed-8b78-686b38c6ffa0.png">
</details>

<br>

##  Back-End Tech Stack
<p align="center">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> 
<br>
<img src="https://img.shields.io/badge/docker-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/redis-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/jenkins-3178C6?style=for-the-badge&logo=typescript&logoColor=black">
<img src="https://img.shields.io/badge/MySQL-61DAFB?style=for-the-badge&logo=React&logoColor=black">
<img src="https://img.shields.io/badge/amazon EC2-764ABC?style=for-the-badge&logo=Redux&logoColor=white">
<br>
<img src="https://img.shields.io/badge/S3-569A31?style=for-the-badge&logo=S3&logoColor=white">
<img src="https://img.shields.io/badge/CloudFront-D05C4B?style=for-the-badge&logo=CloudFront&logoColor=white">
<img src="https://img.shields.io/badge/Spring Boot-E68B49?style=for-the-badge&logo=Route53s&logoColor=white">
<img src="https://img.shields.io/badge/JWT-E68B49?style=for-the-badge&logo=Route53s&logoColor=white">
<img src="https://img.shields.io/badge/OAuth 2.0-E68B49?style=for-the-badge&logo=Route53s&logoColor=white">
<img src="https://img.shields.io/badge/Nginx-E68B49?style=for-the-badge&logo=Route53s&logoColor=white">
<br>
<br>
  
  
## 🚨 트러블 슈팅  
<details>
<summary> ✴준비중</summary>
  
  1. 
  
  2. 
  
  3. 
  
</details>
<br>

<details>
  <summary> ✴️ 준비중</summary>
  
  1. 
  
  2. 
</details>
    
<br>
<details>
  <summary> ✴️ 준비중</summary>
  
  >
  
  1. 
  >- 
  >- 
  
  2. 
  >- 
  >- 
  >- 
  
  3. 
  
    
</details>

<br>
<details>
  <summary> ✴️ 준비중</summary>
  
  1. 
  >- 
  >- 
  2. 
  >- 
  >- 
  3. 
</details>
  
<br>
<br>

## 🃏 팀원소개

| Name     | GitHub & Email                     | Position  |
| -------- | ---------------------------------- | --------- |
| 강형근🐝  | https://github.com/Hyung-Keun        | Front-End   |
| 박세현     | https://github.com/Keeper991           | Front-End  |
| 이현웅 🐜 |  https://github.com/woong7361      | Back-End     |
| 권민주     |    https://github.com/chrysan5     | Back-End     |
| 송승훈     |                 | UI & UX     |
| 신민선     |                | UI & UX     |

<br>
