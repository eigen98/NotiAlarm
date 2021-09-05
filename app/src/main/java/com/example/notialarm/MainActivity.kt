package com.example.notialarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //step0. 뷰를 초기화해주기 //ex) onClickListner달아주기
        initOnOffButton()
        initChangeAlarmTimeButton()

        val model = fetchDataFromSharedPreferences()
        renderView(model)
        //step1. 데이터 가져오기
        //step2. 뷰에데이터를 그려주기
        //
    }


    private fun initOnOffButton(){
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener {
            // 저장한 데이터를 확인한다.

            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener//tag는 오브젝트로 저장되어있기에 형변환해줌 //실패를 대비하여 ?붙임
            val newModel = saveAlarmModel(model.hour, model.minute, !model.onOff.not())

            renderView(newModel)

            if(newModel.onOff){
                //켜진경우 -> 알람을 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if(before(Calendar.getInstance())){
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReciever::class.java)
                var pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

            }else{
                cancelAlarm()
                //꺼진경우 -> 알람을 제거
            }
            //온오프에 따라 작업을 처리한다.
               //오프 -> 알람을 제거
               //온 -> 알람을 등록

            //데이터를 저장한다.
        }
    }
    private fun initChangeAlarmTimeButton(){
        val changeAlarmButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        changeAlarmButton.setOnClickListener {

            // TimePickDialog 띄워줘서 시간을 설정을 하도록 하게끔 하고, 그 시간을 가져와서 //AlertDialog와는 다르게 시간을 지정할 수 있음

            val calendar = Calendar.getInstance()

            TimePickerDialog(this,{ picker, hour, minute ->
                //데이터를 저장한다.
                //뷰를 업데이트한다.
                //기존에 있던 알람을 삭제한다.

               val model = saveAlarmModel(hour,minute, false)
                renderView(model) //뷰를 업데이트한다.

                cancelAlarm()

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false )
                .show()

        }
    }

    private fun saveAlarmModel(
        hour : Int,
        minute : Int,
        onOff : Boolean
    ) : AlarmDisplayModel{
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putString(ALARM_KEY,model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }

//        sharedPreferences.edit{
//        }

        return model
    }

    private fun fetchDataFromSharedPreferences() : AlarmDisplayModel{
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")
        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )
        //보정이 필요함
        //ui는 off로 바꿔줘야함 //실제로 알람이 등록되어있는데 sharedpreference는 꺼져있다
        // 알람이 등록 확인은 broadcast를 가져와서 pendingIntent가 등록되어있는지 확인
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReciever::class.java),PendingIntent.FLAG_NO_CREATE )
//PendingIntent.FLAG_NO_CREATE 있으면 안 만들고 없으면 만듬
        if((pendingIntent==null) and alarmModel.onOff) {
            //알람은 꺼져있는데 데이터는 켜져있는 경우
            //데이터를 수정
            alarmModel.onOff = false
        }else if((pendingIntent != null) and alarmModel.onOff.not()){
            //알람은 켜져있는데 데이터는 등록 안 되어있는 경우
                //알람을 취소함
            pendingIntent.cancel()

        }
        return alarmModel
    }

    //뷰를 그려줌
    private fun renderView(model : AlarmDisplayModel){
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model //스토어 데이터를 위해서 사용가능 버튼을 누르면 태그의 데이터를 가져와서 구성
        }
    }

    private fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReciever::class.java),PendingIntent.FLAG_NO_CREATE )//이미 등록되어있는 알람이 있다면 캔슬
        pendingIntent?.cancel()
    }

    companion object{
        private const val SHARED_PREFERENCE_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }
}