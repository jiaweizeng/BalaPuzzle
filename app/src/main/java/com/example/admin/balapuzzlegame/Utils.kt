package com.example.admin.balapuzzlegame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import java.util.*

/**
 * Created by zjw on 2018/9/19.
 */
class Utils {


    companion object {

        /**
         * 返回屏幕的宽高，用数组返回
         * 下标0，width。 下标1，height。
         *
         * @param context
         * @return
         */

        fun getScreenWidth(context: Context): IntArray {
            var context = context
            context = context.applicationContext
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(outMetrics)
            val width = outMetrics.widthPixels
            val height = outMetrics.heightPixels
            val size = IntArray(2)
            size[0] = width
            size[1] = height
            return size
        }

        /**
         * 传入一个bitmap 返回 一个picec集合
         *
         * @param bitmap
         * @param count
         * @return
         */
        fun splitImage(context: Context, bitmap: Bitmap, count: Int, gameMode: String): ArrayList<ImagePiece> {

            val imagePieces = ArrayList<ImagePiece>()
            val width = bitmap.width
            val height = bitmap.height

            val picWidth = Math.min(width, height) / count

            for (i in 0 until count) {
                for (j in 0 until count) {
                    val imagePiece = ImagePiece()
                    imagePiece.setIndex(j + i * count)
                    //为createBitmap 切割图片获取xy
                    val x = j * picWidth
                    val y = i * picWidth
                    if (gameMode == PuzzleLayout.GAME_MODE_NORMAL) {
                        if (i == count - 1 && j == count - 1) {
                            imagePiece.setType(ImagePiece.TYPE_EMPTY)
                            val emptyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.empty)
                            imagePiece.setBitmap(emptyBitmap)
                        } else {
                            imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picWidth))
                        }
                    } else {
                        imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, picWidth, picWidth))
                    }
                    imagePieces.add(imagePiece)
                }
            }
            return imagePieces
        }

        /**
         * 读取图片，按照缩放比保持长宽比例返回bitmap对象
         *
         *
         *
         * @param scale 缩放比例(1到10, 为2时，长和宽均缩放至原来的2分之1，为3时缩放至3分之1，以此类推)
         * @return Bitmap
         */
        @Synchronized
        fun readBitmap(context: Context, res: Int, scale: Int): Bitmap? {
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                options.inSampleSize = scale
                options.inPurgeable = true
                options.inInputShareable = true
                options.inPreferredConfig = Bitmap.Config.RGB_565
                return BitmapFactory.decodeResource(context.resources, res, options)
            } catch (e: Exception) {
                return null
            }

        }

        fun getMinLength(vararg params: Int): Int {
            var min = params[0]
            for (para in params) {
                if (para < min) {
                    min = para
                }
            }
            return min
        }

        //dp px
        fun dp2px(context: Context, dpval: Int): Int {
            var context = context
            context = context.applicationContext
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval.toFloat(), context.resources.displayMetrics).toInt()
        }
    }

}