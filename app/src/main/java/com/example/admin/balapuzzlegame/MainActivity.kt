package com.example.admin.balapuzzlegame

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity(), PuzzleGame.GameStateListener {

    private var second = 0
    private var minute =0
    private var hour = 0
    private var timeLoop = true

    private val puzzleGame by lazy {
        PuzzleGame(this, puzzle)
    }

    private val puzzle by lazy{
        findViewById<PuzzleLayout>(R.id.puzzle)
    }

    private val tvLevel by lazy {
        findViewById<TextView>(R.id.tvLevel)
    }

    private val spinner by lazy {
        findViewById<Spinner>(R.id.spinner)
    }

    private val selectIvId by lazy {
        intent.getIntExtra("id",R.mipmap.dfzs)
    }

    private val time by lazy {
        findViewById<TextView>(R.id.tv_time)
    }

    private val celebrateIv by lazy {
        findViewById<ImageView>(R.id.ivCelebrate)
    }

    private val puzzleHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){

                1->{
                    celebrateIv.visibility=View.GONE
                    val level:Int = msg.obj as Int
                    if (level!=5){
                        Toast.makeText(this@MainActivity,"挑战成功，现增加难度",Toast.LENGTH_SHORT).show()
                        puzzleGame.addLevel()
                    }else{
                        Toast.makeText(this@MainActivity,"已经是最高难度",Toast.LENGTH_SHORT).show()
                    }
                    timeLoop=true
                    playTime()
                }
                0->{
                    second++
                    if (second==60){
                        minute++
                        second=0
                    }
                    if (minute==60){
                        hour++
                        minute=0
                    }
                    var secondString = ""
                    var minuteString = ""
                    var hourString = ""
                    if (second<10){
                        secondString="0$second"
                    }else{
                        secondString="$second"
                    }
                    if (minute<10){
                        minuteString="0$minute"
                    }else{
                        minuteString="$minute"
                    }
                    if (hour<10){
                        hourString="0$hour"
                    }else{
                        hourString="$hour"
                    }

                    time.text="$hourString:$minuteString:$secondString"
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main)
        puzzleGame.addGameStateListener(this)
        puzzleGame.changeMode(PuzzleLayout.GAME_MODE_NORMAL)
        tvLevel.text = "难度等级：" + puzzleGame.getLevel()
        initListener()
        puzzleGame.changeImage(selectIvId)

        playTime()
    }

    private fun playTime() {
        Thread(Runnable {
            while (timeLoop) {
                puzzleHandler.sendEmptyMessage(0)
                SystemClock.sleep(1000)
            }
        }).start()
    }

    private fun initListener() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> puzzleGame.changeMode(PuzzleLayout.GAME_MODE_NORMAL)
                    1 -> puzzleGame.changeMode(PuzzleLayout.GAME_MODE_EXCHANGE)
                   /* 2 -> puzzleGame.addLevel()
                    3 -> puzzleGame.reduceLevel()*/
                }
            }
        }
    }

    fun reset(view: View) {
        puzzle.isOriginalIv = false
        puzzleGame.changeImage(R.mipmap.sdhy)
    }

    fun originalIv(view: View) {
        puzzle.isOriginalIv = true
        puzzleGame.changeImage(R.mipmap.sdhy)
    }

    override fun setLevel(level: Int) {
        tvLevel.text = "难度等级：" + level
    }

    override fun gameSuccess(level: Int) {
        celebrateIv.visibility=View.VISIBLE
        time.text="00:00:00"
        second = 0
        minute =0
        hour = 0
        timeLoop=false
        val message = Message.obtain()
        message.what=1
        message.obj=level
        puzzleHandler.sendMessageDelayed(message,3000)
//        puzzleHandler.sendEmptyMessageDelayed(1,1000)
//        SystemClock.sleep(1000)


    }


}

