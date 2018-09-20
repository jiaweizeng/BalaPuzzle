package com.example.admin.balapuzzlegame

import android.content.Context
import android.widget.Toast

/**
 * Created by zjw on 2018/9/19.
 */
class PuzzleGame(private val context: Context, private val puzzleLayou: PuzzleLayout):Game, PuzzleLayout.SuccessListener{
//    private val puzzleLayou: PuzzleLayout?=null
    private var stateListener: GameStateListener? = null
//    private val context: Context

    init {
        puzzleLayou.addSuccessListener(this)
    }

    fun addGameStateListener(stateListener: GameStateListener) {
        this.stateListener = stateListener
    }

    /*fun PuzzleGame(context: Context, puzzleLayout: PuzzleLayout): ??? {
        this.context = context.applicationContext
        this.puzzleLayou = puzzleLayout
        puzzleLayou.addSuccessListener(this)
    }*/

    private fun checkNull(): Boolean {
        return puzzleLayou == null
    }

    override fun addLevel() {
        if (checkNull()) {
            return
        }
        if (!puzzleLayou.addCount()) {
            Toast.makeText(context, context.getString(R.string.already_the_most_level), Toast.LENGTH_SHORT).show()
        }
        if (stateListener != null) {
            stateListener!!.setLevel(getLevel())
        }
    }

    override fun reduceLevel() {
        if (checkNull()) {
            return
        }
        if (!puzzleLayou.reduceCount()) {
            Toast.makeText(context, context.getString(R.string.already_the_less_level), Toast.LENGTH_SHORT).show()
        }
        if (stateListener != null) {
            stateListener!!.setLevel(getLevel())
        }
    }

    override fun changeMode(gameMode: String) {
        puzzleLayou.changeMode(gameMode,puzzleLayou.isOriginalIv)
    }

    override fun changeImage(res: Int) {
        puzzleLayou.changeRes(res)
    }

    fun getLevel(): Int {
        if (checkNull()) {
            return 0
        }
        val count = puzzleLayou.getCount()
        return count - 3 + 1
    }

    override fun success() {
        if (stateListener != null) {
            stateListener!!.gameSuccess(getLevel())
        }
    }

    interface GameStateListener {
        fun setLevel(level: Int)

        fun gameSuccess(level: Int)
    }
}