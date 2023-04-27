# React Native Android Foreground용 Cron Java 모듈

> This is not a library.
>
> 이것은 라이브러리가 아닙니다.
>
> Foreground에서 예약된 시간에 React Native에 특정 함수를 실행하기 위해 만든, 개인적으로 쓰기 위해 만든 모듈입니다.
>
> notification을 이용하는 방식이라 앱 첫 실행 시 알림 권한 팝업이 뜹니다. (사용자가 사용 안함 해도 상관 없음)
>
> Android의 AlarmManager를 이용해 만들었습니다.
>
> java를 사용했습니다.
>
> app/src/main/java/패키지에 AlarmModule 관련 파일들을 추가했습니다. (패키지명을 넣어서 쓰세요.)

<br>


- AndroidMenifest.xml
```xml
<!-- Permission을 추가합니다 -->
<uses-permission android:name="android.permission.SET_ALARM" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATION" />

<!-- Application 태그 안 receiver와 service를 추가합니다. -->
<!-- 저는 Alarm이라고 이름을 지었습니다. -->
<receiver android:name=".Alarm.AlarmReceiver" />

        <service
            android:name=".Alarm.AlarmService"
            android:enabled="true"
            android:exported="false">
        </service>
        <service android:name=".Alarm.AlarmEvent"/>
```

<br>

- MainApplication.java
```java
@Override
protected List<ReactPackage> getPackages() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    List<ReactPackage> packages = new PackageList(this).getPackages();

    // 해당 코드를 추가했습니다.
    packages.add(new AlarmPackage());

    return packages;
}
```

<br>

- React Native에 안드로이드와 연결하는 파일을 추가했습니다. 저는 AlarmModule.js 라고 지었습니다.
```js
import {NativeModules} from 'react-native';

let {AlarmModule} = NativeModules;

AlarmModule.removeListeners = () => {};
AlarmModule.addListener = () => {};

export default AlarmModule;
```

<br>

- 사용 예시
```js
// 위에서 만든 AlarmModule.js를 import해서 사용해주세요.

const AlarmModuleTask = async () => {
    // 실행하고자 하는 함수를 넣어주세요.


    AlarmModule.completeTask();
};
AppRegistry.registerHeadlessTask('CRON', () => AlarmModuleTask);

// 시간은 0 <= 23, 분은 0<=59 사이로 넣어주세요. 저는 오전 6시를 기준으로 작업했습니다.
AlarmModule.startCron(6, 0);
```