package com.yxm.tile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yxm.library.Board
import com.yxm.library.TileView
import com.yxm.library.util.ExpressionUtil

class MainActivity : AppCompatActivity() {

    private var tileView: TileView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tileView = findViewById(R.id.tile)
        tileView?.board = Board()
        tileView?.setPutType(0)
        tileView?.boardUnlock=true
        val board = tileView?.board
        board?.setBoardSize(19)
        tileView?.invalidate() // 绘制棋盘
        setBackground(tileView!!,this)
    }

    private var bitmapBackgroud: Bitmap? = null

    fun setBackground(tileView: TileView, context: Context) {
        val sp_set = context.getSharedPreferences("setconfig", Context.MODE_PRIVATE)
        if (sp_set == null) {
            bitmapBackgroud = ExpressionUtil.scaleBitmapFix(
                context, R.mipmap.wood1, 2)
        } else {
            if (sp_set.getString("bg", null) == null) {
                bitmapBackgroud = ExpressionUtil.scaleBitmapFix(
                    context, R.mipmap.wood1, 2)
            } else {
                val resId1 = context.resources.getIdentifier(
                    sp_set.getString("bg", null), null, null)
                bitmapBackgroud = ExpressionUtil.scaleBitmapFix(
                    context, resId1, 2)
            }
        }
        tileView.background = BitmapDrawable(bitmapBackgroud)
    }
}