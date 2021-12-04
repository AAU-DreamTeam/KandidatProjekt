package com.example.androidapp.views

import android.media.MediaRecorder
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ScreenRecorder (activity: AppCompatActivity){
    val mediaRecorder = MediaRecorder()
    val metrics = DisplayMetrics()

    companion object {
        var isRecording = false
    }

    init {
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        initializeRecorder(activity)
    }

    private fun initializeRecorder(activity: AppCompatActivity) {
        try {
            val videoUrl = "${activity.filesDir.absolutePath}/${
                SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.getDefault()).format(
                    Date()
                )}.mp4"

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setOutputFile(videoUrl)
            mediaRecorder.setVideoSize(metrics.widthPixels, metrics.heightPixels)
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setVideoEncodingBitRate(512*1000)
            mediaRecorder.setVideoFrameRate(2)
            mediaRecorder.setOrientationHint(MainActivity.ORIENTATION.get(activity.windowManager.defaultDisplay.rotation))
            mediaRecorder.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun start(){
        mediaRecorder.start()
        isRecording = true
    }

    fun stop(){
        mediaRecorder.stop()
        isRecording = false
    }

    fun reset(){
        mediaRecorder.reset()
    }
}