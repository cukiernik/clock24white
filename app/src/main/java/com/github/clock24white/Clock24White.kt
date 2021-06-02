package com.github.clock24white

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.*
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder

import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.TimeZone

private const val INTERACTIVE_UPDATE_RATE_MS = 40
private const val MSG_UPDATE_TIME = 0

class Clock24White : CanvasWatchFaceService() {

    inner class Engine : CanvasWatchFaceService.Engine() {

        private var mCalendar=Calendar.getInstance()

        private var mCenterX: Int=240
        private var mCenterY: Int=240
        private var radius: Int=120
        private var numbers = Paint().apply {
            color = Color.DKGRAY
            isAntiAlias = false
            textAlign=Paint.Align.CENTER
            textSize=20f
        }
        private var hour = Paint().apply {
            color = Color.BLACK
            strokeWidth = 9f
            isAntiAlias = false
            style = Paint.Style.STROKE
            textAlign=Paint.Align.RIGHT
            textSize=64f
        }
        private var minute = Paint().apply {
            color = Color.argb(255,0,128,0)
            strokeWidth = 3f
            isAntiAlias = false
            style = Paint.Style.STROKE
            textAlign=Paint.Align.LEFT
            textSize=64f
        }
        private var second = Paint().apply {
            color = Color.RED
            isAntiAlias = false
        }

        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            println("### 222 onTapCommand ")
            timetick=0
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                }
                WatchFaceService.TAP_TYPE_TAP -> {
                }
//                    Toast.makeText(applicationContext, R.string.message, Toast.LENGTH_SHORT) .show()
            }
        }

        private fun draw1(canvas: Canvas,line:Paint,len:Float,kat:Double,len2:Float=0f) {
            canvas.drawLine(mCenterX+Math.sin(kat*Math.PI).toFloat()*len2, mCenterY-Math.cos(kat*Math.PI).toFloat()*len2,
                mCenterX+Math.sin(kat*Math.PI).toFloat()*len, mCenterY-Math.cos(kat*Math.PI).toFloat()*len,
                line)
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            timetick++
            mCenterX=bounds.centerX()
            mCenterY=bounds.centerY()
            radius=Math.min(mCenterX,mCenterY)
            mCalendar.timeInMillis = System.currentTimeMillis()
            canvas.drawColor(Color.WHITE)
//            canvas.drawText(bounds.toString(),100f,200f,numbers)
//            if(radius<=8)return
            canvas.save()
            for (tickIndex in 0..23) {
                canvas.drawText(tickIndex.toString(),mCenterX-Math.sin(Math.PI*tickIndex/12).toFloat()*radius*0.9f-1, mCenterY+Math.cos(Math.PI*tickIndex/12).toFloat()*radius*0.9f,numbers)
            }
            if(INTERACTIVE_UPDATE_RATE_MS<15000){
                if(INTERACTIVE_UPDATE_RATE_MS<250)
                    draw1(canvas,second,10f*radius/INTERACTIVE_UPDATE_RATE_MS,mCalendar.get(Calendar.MILLISECOND)/500.0)
                draw1(canvas,second,radius*0.5f,(mCalendar.get(Calendar.MILLISECOND)/1000.0+mCalendar.get(Calendar.SECOND))/30f)
                }
            draw1(canvas,minute,radius*0.7f,(mCalendar.get(Calendar.SECOND)/60.0+mCalendar.get(Calendar.MINUTE))/30f)
            draw1(canvas,hour,radius*1f,1+(mCalendar.get(Calendar.MINUTE)/60.0+mCalendar.get(Calendar.HOUR_OF_DAY))/12f,radius*0.1f)
            //canvas.drawText( timetick.toString(),mCenterX.toFloat(),mCenterY.toFloat(),minute)
            canvas.restore()
            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, INTERACTIVE_UPDATE_RATE_MS.toLong())
        }

        var timetick=0

        override fun onTimeTick() {
            super.onTimeTick()
//            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
//            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, INTERACTIVE_UPDATE_RATE_MS.toLong())
            invalidate()
//            vib()
        }

        override fun onPropertiesChanged(properties: Bundle?) {
            println("### onPropertiesChanged "+properties.toString())
            super.onPropertiesChanged(properties)
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            println("### onAmbientModeChanged "+inAmbientMode.toString())
            super.onAmbientModeChanged(inAmbientMode)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            println("### onVisibilityChanged "+ visible.toString()+" "+this.toString())
            super.onVisibilityChanged(visible)
            if (visible) {
//                postInvalidate()
//                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            } else {
//              mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            }
        }

        override fun onDestroy() {
            println("### onDestroy ")
//            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
//            mUpdateTimeHandler.
            super.onDestroy()
            println("### onDestroy ")
        }

        override fun onCreate(holder: SurfaceHolder) {
            mCalendar.timeZone = TimeZone.getDefault()
            super.onCreate(holder)
            setWatchFaceStyle(WatchFaceStyle.Builder(this@Clock24White)
//                .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_PERSISTENT)
//                .setShowSystemUiTime(true)
//                .SetCardPeekMode (WatchFaceStyle.PeekModeShort)
                .setAcceptsTapEvents(true)
//                .setViewProtectionMode(1)
                .setAccentColor(Color.WHITE)
                .build()
            )

        }
        private val mUpdateTimeHandler = EngineHandler(this)

        fun handleUpdateTimeMessage() {
            postInvalidate()
//            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, INTERACTIVE_UPDATE_RATE_MS.toLong())
        }

        }
    override fun onCreateEngine(): Engine {
        println("### onCreateEngine")
        return Engine()
    }

    private class EngineHandler(reference: Clock24White.Engine) : Handler(Looper.getMainLooper()) {
        private val mWeakReference: WeakReference<Clock24White.Engine> = WeakReference(reference)
        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }
}