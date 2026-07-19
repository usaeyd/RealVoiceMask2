
package com.realvoicemask
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlin.concurrent.thread

class RealVoiceEngine {
    private var isRunning = false
    private var audioThread: Thread? = null
    // Girl voice params: pitch up + formant shift
    var pitchFactor = 1.6f
    var girlMode = true

    fun start() {
        if (isRunning) return
        isRunning = true
        audioThread = thread {
            val sampleRate = 44100
            val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2
            val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
            val track = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build())
                .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(bufferSize).setTransferMode(AudioTrack.MODE_STREAM).build()
            recorder.startRecording(); track.play()
            val buffer = ShortArray(bufferSize/2)
            while (isRunning) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    // simple pitch up by resampling (girl effect)
                    val out = ShortArray((read / pitchFactor).toInt())
                    for (i in out.indices) {
                        val srcIndex = (i * pitchFactor).toInt().coerceIn(0, read-1)
                        out[i] = buffer[srcIndex]
                    }
                    track.write(out, 0, out.size)
                }
            }
            recorder.stop(); recorder.release(); track.stop(); track.release()
        }
    }
    fun stop() { isRunning = false; audioThread?.join(500) }
}
