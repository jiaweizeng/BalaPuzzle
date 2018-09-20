package com.example.admin.balapuzzlegame

/**
 * Created by zjw on 2018/9/19.
 */
interface Game {
    /**
     * 增加难度
     */
     fun addLevel()

    /**
     * 减少难度
     */
     fun reduceLevel()

    /**
     * 修改游戏模式
     */
     fun changeMode(gameMode: String)

    /**
     * 修改图片
     *
     * @param res
     */
     fun changeImage(res: Int)
}