# NotiAlarm
AlarmManager사용
Notification사용
Broadcast receiver 사용 ->Notification수신

[Background작업] 
알람이 울리는 것을 앱이 실행되어있고 실행되는 동안 계속 체크해서 알림을 울려야함. 
앱이 살아있을 것이라는 보장이 없기에 백그라운드 작업필요
	->immediate tasks(즉시 실행되어야하는 작업)
		->Tread(DB 작업 같은 무거운 부분은 타 쓰레드 사용) 복습: uI쓰레드는 메인쓰레드
		->Handler
		->Kotlin coroutines //비동기
	->Deferred tasks(지연된 작업)
		->WorkManager
	->Exact tasks(정시에 실행해야 하는 작업)
		AlarmManager //pendingIntent를 이용

AlarmManager
	->Real Time(실제시간)으로 실행시기는 방법
	->Elapsed Time (기기가 부팅된지부터 얼마나 지났는지 )으로 실행시키는 방법

알람앱
지정된시간에 알람이 울리게 할 수 있음.
지정된 시간 이후에는 매일 같은 시간에 반복되게 알람이 울리게 할 수 있음.
