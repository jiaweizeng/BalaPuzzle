package com.example.admin.balapuzzlegame

import android.graphics.Bitmap
import android.widget.ImageView

/**
 * Created by zjw on 2018/9/19.
 */
class ImagePiece {

    companion object {
        val TYPE_NORMAL = 0
        val TYPE_EMPTY = 1
    }


    private var type = TYPE_NORMAL
    private var index: Int = 0
    private var bitmap: Bitmap? = null
    private var imageView: ImageView? = null

    fun getImageView(): ImageView? {
        return imageView
    }

    fun setImageView(imageView: ImageView) {
        this.imageView = imageView
    }

    fun getType(): Int {
        return type
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun getIndex(): Int {
        return index
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    override fun toString(): String {
        return super.toString()
    }
}