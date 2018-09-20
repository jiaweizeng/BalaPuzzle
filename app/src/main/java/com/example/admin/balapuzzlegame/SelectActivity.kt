package com.example.admin.balapuzzlegame

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * Created by zjw on 2018/9/19.
 */
class SelectActivity: AppCompatActivity() {

    private var selectId:Int?=null

    private val llIvList by lazy {
        findViewById<LinearLayout>(R.id.ll_iv_list)
    }

    private val llSelectedIv by lazy {
        findViewById<LinearLayout>(R.id.ll_selected)
    }

    private val ivSelected by lazy {
        findViewById<ImageView>(R.id.iv_selected)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
    }

    fun selectClick(view:View){
        when(view.id){
            R.id.fl_1 -> showSelected(R.mipmap.sdhy)
            R.id.fl_2 -> showSelected(R.mipmap.dmxyzy)
            R.id.fl_3 -> showSelected(R.mipmap.dfzs)
            R.id.fl_4 -> showSelected(R.mipmap.ll)
            R.id.ivDelete->{
                llIvList.visibility = View.VISIBLE
                llSelectedIv.visibility = View.GONE
            }
            R.id.start->{
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("id",selectId)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun showSelected(id:Int) {
        llIvList.visibility = View.GONE
        llSelectedIv.visibility = View.VISIBLE
        ivSelected.setImageResource(id)
        selectId=id
    }
}