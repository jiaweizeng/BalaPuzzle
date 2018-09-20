package com.example.admin.balapuzzlegame

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import java.util.*

/**
 * Created by zjw on 2018/9/19.
 */
class PuzzleLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener{

    companion object {
        val GAME_MODE_NORMAL = "gameModeNormal"
        val GAME_MODE_EXCHANGE = "gameModeExchange"
    }


    private val DEFAULT_MARGIN = 3

    //游戏模式
    private var mGameMode = GAME_MODE_EXCHANGE

    //拼图布局为正方形，宽度为屏幕的宽度
    private var mViewWidth = 0

    //拼图游戏每一行的图片个数(默认为三个)
    private var mCount = 3

    //每张图片的宽度
    private var mItemWidth: Int = 0

    //拼图游戏bitmap集合
    private var mImagePieces: java.util.ArrayList<ImagePiece>? = null

    //用于给每个图片设置大小
    private var layoutParams: FrameLayout.LayoutParams? = null

    //大图
    private var mBitmap: Bitmap? = null

    //动画层
    private var mAnimLayout: RelativeLayout? = null

    //小图之间的margin
    private var mMargin: Int = 0

    //这个view的padding
    private var mPadding: Int = 0

    //选中的第一张图片
    private var mFirst: ImageView? = null

    //选中的第二张图片
    private var mSecond: ImageView? = null

    //是否添加了动画层
    private var isAddAnimatorLayout = false

    //是否正在进行动画
    private var isAnimation = false

    private var res = R.mipmap.sdhy

    var isOriginalIv=false//是否展示原始圖片

    init {
        init(context)
        initBitmaps()
        initBitmapsWidth()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mViewWidth, mViewWidth)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            if (getChildAt(i) is ImageView) {
                val imageView = getChildAt(i) as ImageView
                imageView.layout(imageView.left, imageView.top, imageView.right, imageView.bottom)
            } else {
                val relativeLayout = getChildAt(i) as RelativeLayout
                relativeLayout.layout(0, 0, mViewWidth, mViewWidth)
            }
        }
    }

    /**
     * 初始化初始变量
     *
     * @param context
     */
    private fun init(context: Context) {
        mMargin = Utils.dp2px(context, DEFAULT_MARGIN)
        mViewWidth = Utils.getScreenWidth(context)[0]
        mPadding = Utils.getMinLength(paddingBottom, paddingLeft, paddingRight, paddingTop)
        mItemWidth = (mViewWidth - mPadding * 2 - mMargin * (mCount - 1)) / mCount
    }

    /**
     * 将大图切割成多个小图
     */
    private fun initBitmaps() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(resources, res)
        }
        mImagePieces = Utils.splitImage(context, mBitmap!!, mCount, mGameMode)
        if (!isOriginalIv){
            sortImagePieces()
        }
    }

    /**
     * 对ImagePieces进行排序
     */
    private fun sortImagePieces() {
        try {
            Collections.sort(mImagePieces) { lhs, rhs -> if (Math.random() > 0.5) 1 else -1 }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (mGameMode == GAME_MODE_NORMAL) {
                //如果是第二种模式就将空图放在最后
                var tempImagePieces: ImagePiece? = null
                var tempIndex = 0
                for (i in mImagePieces!!.indices) {
                    val imagePiece = mImagePieces!!.get(i)
                    if (imagePiece.getType() === ImagePiece.TYPE_EMPTY) {
                        tempImagePieces = imagePiece
                        tempIndex = i
                        break
                    }
                }
                if (tempImagePieces == null) return
                mImagePieces!!.removeAt(tempIndex)
                mImagePieces!!.add(mImagePieces!!.size, tempImagePieces)
            }
        }
    }

    /**
     * 设置图片的大小和layout的属性
     */
    private fun initBitmapsWidth() {
        var line = 0
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        for (i in mImagePieces!!.indices) {
            val imageView = ImageView(context)
            imageView.setImageBitmap(mImagePieces!!.get(i).getBitmap())
            layoutParams = FrameLayout.LayoutParams(mItemWidth, mItemWidth)
            imageView.layoutParams = layoutParams
            if (i != 0 && i % mCount == 0) {
                line++
            }
            if (i % mCount == 0) {
                left = i % mCount * mItemWidth
            } else {
                left = i % mCount * mItemWidth + i % mCount * mMargin
            }
            top = mItemWidth * line + line * mMargin
            right = left + mItemWidth
            bottom = top + mItemWidth
            imageView.right = right
            imageView.left = left
            imageView.bottom = bottom
            imageView.top = top
            imageView.id = i
            imageView.setOnClickListener(this)
            mImagePieces!!.get(i).setImageView(imageView)
            addView(imageView)
        }
    }

    /**
     * 改变游戏模式
     *
     * @param gameMode
     */
    fun changeMode(gameMode: String,isOriginalIv:Boolean) {
        if (gameMode == mGameMode) {
            return
        }
        this.mGameMode = gameMode
        reset(isOriginalIv)
    }


    fun reset(isOriginalImageView:Boolean) {
        isOriginalIv=isOriginalImageView
        mItemWidth = (mViewWidth - mPadding * 2 - mMargin * (mCount - 1)) / mCount
        if (mImagePieces != null) {
            mImagePieces!!.clear()
        }
        isAddAnimatorLayout = false
        mBitmap = null
        removeAllViews()
        initBitmaps()
        initBitmapsWidth()
    }

    /**
     * 添加count 最多每行7个
     */
    fun addCount(): Boolean {
        mCount++
        if (mCount > 7) {
            mCount--
            return false
        }
        reset(isOriginalIv)
        return true
    }

    /**
     * 改变图片
     */
    fun changeRes(res: Int) {
        this.res = res
        reset(isOriginalIv)
    }

    /**
     * 减少count 最少每行三个，否则普通模式无法游戏
     */
    fun reduceCount(): Boolean {
        mCount--
        if (mCount < 3) {
            mCount++
            return false
        }
        reset(isOriginalIv)
        return true
    }



    override fun onClick(v: View?) {
        if (isAnimation) {
            //还在运行动画的时候，不允许点击
            return
        }
        if (v !is ImageView) {
            return
        }
        if (GAME_MODE_NORMAL == mGameMode) {
            val imagePiece = mImagePieces!!.get(v.id)
            if (imagePiece.getType() === ImagePiece.TYPE_EMPTY) {
                //普通模式，点击到空图不做处理
                return
            }
            if (mFirst == null) {
                mFirst = v
            }
            checkEmptyImage(mFirst!!)
        } else {
            //点的是同一个View
            if (mFirst === v) {
                mFirst?.setColorFilter(null)
                mFirst = null
                return
            }
            if (mFirst == null) {
                mFirst = v
                //选中之后添加一层颜色
                mFirst?.setColorFilter(Color.parseColor("#55FF0000"))
            } else {
                mSecond = v
                exChangeView()
            }
        }
    }

    private fun checkEmptyImage(imageView: ImageView) {
        val index = imageView.id
        val line = mImagePieces!!.size / mCount
        var imagePiece: ImagePiece? = null
        if (index < mCount) {
            //第一行（需要额外计算，下一行是否有空图）
            imagePiece = checkCurrentLine(index)
            //判断下一行同一列的图片是否为空
            imagePiece = checkOtherline(index + mCount, imagePiece)
        } else if (index < (line - 1) * mCount) {
            //中间的行（需要额外计算，上一行和下一行是否有空图）
            imagePiece = checkCurrentLine(index)
            //判断上一行同一列的图片是否为空
            imagePiece = checkOtherline(index - mCount, imagePiece)
            //判断下一行同一列的图片是否为空
            imagePiece = checkOtherline(index + mCount, imagePiece)
        } else {
            //最后一行（需要额外计算，上一行是否有空图））
            imagePiece = checkCurrentLine(index)
            //检查上一行同一列有没有空图
            imagePiece = checkOtherline(index - mCount, imagePiece)
        }
        if (imagePiece == null) {
            //周围没有空的imageView
            mFirst = null
            mSecond = null
        } else {
            //记录下第二张ImageView
            mSecond = imagePiece.getImageView()
            //选中第二个图片，开启动两张图片替换的动画
            exChangeView()
        }
    }

    /**
     * 检查上其他行同一列有没有空图
     *
     * @return
     */
    private fun checkOtherline(index: Int, imagePiece: ImagePiece?): ImagePiece? {
        return imagePiece ?: getCheckEmptyImageView(index)
    }

    /**
     * 检查当前行有没有空的图片
     *
     * @param index
     * @return
     */
    private fun checkCurrentLine(index: Int): ImagePiece? {
        var imagePiece: ImagePiece? = null
        //第一行
        if (index % mCount == 0) {
            //第一个
            imagePiece = getCheckEmptyImageView(index + 1)
        } else if (index % mCount == mCount - 1) {
            //最后一个
            imagePiece = getCheckEmptyImageView(index - 1)
        } else {
            imagePiece = getCheckEmptyImageView(index + 1)
            if (imagePiece == null) {
                imagePiece = getCheckEmptyImageView(index - 1)
            }
        }
        return imagePiece
    }

    private fun getCheckEmptyImageView(index: Int): ImagePiece? {
        val imagePiece = mImagePieces!!.get(index)
        return if (imagePiece.getType() === ImagePiece.TYPE_EMPTY) {
            //找到空的imageView
            imagePiece
        } else null
    }

    private fun addAnimationImageView(imageView: ImageView): ImageView {
        val getImage = ImageView(context)
        val firstParams = RelativeLayout.LayoutParams(mItemWidth, mItemWidth)
        firstParams.leftMargin = imageView.left - mPadding
        firstParams.topMargin = imageView.top - mPadding
        val firstBitmap = mImagePieces!!.get(imageView.id).getBitmap()
        getImage.setImageBitmap(firstBitmap)
        getImage.layoutParams = firstParams
        mAnimLayout!!.addView(getImage)
        return getImage
    }

    /**
     * 添加动画层，并且添加平移的动画
     */
    private fun exChangeView() {

        //添加动画层
        setUpAnimLayout()
        //添加第一个图片
        val first = addAnimationImageView(mFirst!!)
        //添加另一个图片
        val second = addAnimationImageView(mSecond!!)


        val secondXAnimator = ObjectAnimator.ofFloat(second, "TranslationX", /*0f,*/ -(mSecond!!.getLeft() - mFirst!!.getLeft()).toFloat())
        val secondYAnimator = ObjectAnimator.ofFloat(second, "TranslationY", 0f, -(mSecond!!.getTop() - mFirst!!.getTop()).toFloat())
        val firstXAnimator = ObjectAnimator.ofFloat(first, "TranslationX", 0f, mSecond!!.getLeft() - mFirst!!.getLeft().toFloat())
        val firstYAnimator = ObjectAnimator.ofFloat(first, "TranslationY", 0f, mSecond!!.getTop() - mFirst!!.getTop().toFloat())
        val secondAnimator = AnimatorSet()
        secondAnimator.play(secondXAnimator).with(secondYAnimator).with(firstXAnimator).with(firstYAnimator)
        secondAnimator.duration = 300

        val firstPiece = mImagePieces!!.get(mFirst!!.getId())
        val secondPiece = mImagePieces!!.get(mSecond!!.getId())
        val firstType = firstPiece.getType()
        val secondType = secondPiece.getType()
        val firstBitmap = mImagePieces!!.get(mFirst!!.getId()).getBitmap()
        val secondBitmap = mImagePieces!!.get(mSecond!!.getId()).getBitmap()
        //        final int firstIndex = mImagePieces.get(mFirst.getId()).getIndex();
        //        final int secondIndex = mImagePieces.get(mFirst.getId()).getIndex();
        secondAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                val fristIndex = firstPiece.getIndex()
                val secondeIndex = secondPiece.getIndex()
                if (mFirst != null) {
                    mFirst!!.setColorFilter(null)
                    mFirst!!.setVisibility(View.VISIBLE)
                    mFirst!!.setImageBitmap(secondBitmap)
                    firstPiece.setBitmap(secondBitmap!!)
                    firstPiece.setIndex(secondeIndex)
                }
                if (mSecond != null) {
                    mSecond!!.setVisibility(View.VISIBLE)
                    mSecond!!.setImageBitmap(firstBitmap)
                    secondPiece.setBitmap(firstBitmap!!)
                    secondPiece.setIndex(fristIndex)
                }
                if (mGameMode == GAME_MODE_NORMAL) {
                    firstPiece.setType(secondType)
                    secondPiece.setType(firstType)
                }

                mAnimLayout?.removeAllViews()
                mAnimLayout?.setVisibility(View.GONE)
                mFirst = null
                mSecond = null
                isAnimation = false
                invalidate()
                if (checkSuccess()) {
                    Toast.makeText(context, "成功!", Toast.LENGTH_SHORT).show()
                    if (mSuccessListener != null) {
                        mSuccessListener!!.success()
                    }
                }
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                isAnimation = true
                mAnimLayout?.setVisibility(View.VISIBLE)
                mFirst?.setVisibility(View.INVISIBLE)
                mSecond?.setVisibility(View.INVISIBLE)
            }
        })
        secondAnimator.start()
    }

    /**
     * 构造动画层 用于点击之后的动画
     * 为什么要做动画层？ 要保证动画在整个view上面执行。
     */
    private fun setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = RelativeLayout(context)
        }
        if (!isAddAnimatorLayout) {
            isAddAnimatorLayout = true
            addView(mAnimLayout)
        }
    }

    /**
     * 检测是否成功
     */
    private fun checkSuccess(): Boolean {

        var isSuccess = true
        for (i in mImagePieces!!.indices) {
            val imagePiece = mImagePieces?.get(i)
            if (i != imagePiece?.getIndex()) {
                isSuccess = false
            }
        }
        return isSuccess
    }

    fun getBitmap(): Bitmap {
        return mBitmap!!
    }

    fun getRes(): Int {
        return res
    }

    fun getCount(): Int {
        return mCount
    }

    private var mSuccessListener: SuccessListener? = null

    fun addSuccessListener(successListener: SuccessListener) {
        this.mSuccessListener = successListener
    }

    interface SuccessListener {
        fun success()
    }


}