# 알람메모 Alamemo
### 원래 만들었던 앱을 다시 만든 프로젝트. (기본 삼성 메모 어플리케이션이 복잡해서 간단한 알람이 울리는 메모 어플리케이션을 만들고 싶었다.)
##### - 원래 있던 코드가 하드 코딩이고, 유지 보수가 떨어지는 코드였다.
##### - UI가 맘에들지 않았다.
##### - 몇 가지 새로운 기능(반복 일정, 아이콘 선택 등)을 만들고 싶었다.
[![playStore Badge](https://img.shields.io/badge/Google%20PlayStore-0D96F6?style=for-the-badge&logo=AppStore&logoColor=white)](https://play.google.com/store/apps/details?id=com.landvibe.alamemo&hl=en_AU&gl=US)
##
- Room DB를 활용한 메모 저장
- Data Binding과 View Model을 활용해서 MVVM패턴으로 앱을 재구성했다.
- LiveData를 활용해서 값의 변화에 따라 UI가 자동으로 변할 수 있도록 했다.
- BroadcastReceiver를 통해 알람을 설정할 수 있도록 만들었고, PendingIntent를 같이 사용해서 만들었다. 이를 통해 부팅할 때도 설정된 알람들을 설정할 수 있었다.
- Android 8 이상의 폰에 알람이 갈 수 있도록 NotificationChannel을 만들었다.
- BaseActivity, BaseFragment를 사용해서 코드의 중복을 줄였다.
##
### 앱 스크린샷
##
![helper_add_2](https://user-images.githubusercontent.com/63734277/141643524-a21c09f9-bdd7-49e3-95e6-2cc96e77a779.png)
![helper_add_3](https://user-images.githubusercontent.com/63734277/141643538-44744c0a-9c5e-4738-bb91-8c50872fa095.png)
![helper_add_5](https://user-images.githubusercontent.com/63734277/141643541-81a5b4d2-e986-4e02-a6f1-83e9d77a9a9c.png)
![helper_add_8](https://user-images.githubusercontent.com/63734277/141643542-30d8632f-6882-433d-8b18-d2a37c39e4e4.png)
![helper_detail_3](https://user-images.githubusercontent.com/63734277/141643546-b04c748b-fcbf-434e-af14-9f9934acb089.png)
![helper_delete_1](https://user-images.githubusercontent.com/63734277/141643549-9e614b40-b846-496e-8132-c0dce32cedb2.png)
![helper_delete_2](https://user-images.githubusercontent.com/63734277/141643552-5fbdec00-7228-4b97-8947-4e1d70b53812.png)
![helper_alarm_fix_noti_5](https://user-images.githubusercontent.com/63734277/141643555-f90a2cbb-b467-4180-b88b-6e8e664e3167.png)
