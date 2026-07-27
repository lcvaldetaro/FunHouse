package com.funhouse.shared.common.services

import com.funhouse.shared.common.AppData.isListening
import com.funhouse.shared.common.AppData.voiceService
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import club.gepetto.GcLog
import java.lang.ref.WeakReference

class VoiceService : Service() {
    private val mBinder: IBinder = LocalBinder()
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mSpeechRecognizerIntent: Intent? = null
    private val mServerMessenger = Messenger(IncomingHandler(this))
    private var listener = SpeechRecognitionListener()
    private var amanager: AudioManager? = null
    private var mIsListening = false
    var text = ""
        private set

    override fun onBind(intent: Intent): IBinder? {
        GcLog.d("onBind Called")
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: VoiceService
            get() {
                GcLog.d("LocalBinder called")
                return this@VoiceService
            }
    }

    override fun onCreate() {
        super.onCreate()
        GcLog.d("onCreate Called")
        voiceService = this
        amanager = getSystemService(AUDIO_SERVICE) as AudioManager
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
  //TODO kotlin bitches      //mSpeechRecognizer.setRecognitionListener(listener)
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        mSpeechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        // mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
    }

    fun startListening() {
        GcLog.d("startListening called")
        mSpeechRecognizer!!.startListening(mSpeechRecognizerIntent)
    }

    fun stopListening() {
        amanager!!.setStreamMute(AudioManager.STREAM_MUSIC, false)
        mSpeechRecognizer!!.stopListening()
    }

    protected inner class IncomingHandler internal constructor(target: VoiceService) : Handler() {
        private val mtarget: WeakReference<VoiceService>
        override fun handleMessage(msg: Message) {
            val target = mtarget.get()
            GcLog.d("handleMessage called.")
            when (msg.what) {
                MSG_RECOGNIZER_START_LISTENING -> if (!target!!.mIsListening) {
                    target.mSpeechRecognizer!!.startListening(target.mSpeechRecognizerIntent)
                    target.mIsListening = true
                    GcLog.d("message start listening")
                }
                MSG_RECOGNIZER_CANCEL -> {
                    target!!.mSpeechRecognizer!!.cancel()
                    target.mIsListening = false
                    GcLog.d("message canceled recognizer")
                }
            }
        }

        init {
            mtarget = WeakReference(target)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GcLog.d("onDestroy called")
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer!!.destroy()
        }
    }

    protected inner class SpeechRecognitionListener : RecognitionListener {
        override fun onBeginningOfSpeech() {
            GcLog.d("onBeginingOfSpeech")
            isListening = true
        }

        override fun onBufferReceived(buffer: ByteArray) {
            GcLog.d("onBufferReceived")
        }

        override fun onEndOfSpeech() {
            GcLog.d("onEndOfSpeech")
        }

        override fun onError(error: Int) {
            mIsListening = false
            val message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING)
            try {
                mServerMessenger.send(message)
            } catch (_: RemoteException) {
            }
            /*
            Errors:
            ERROR_NETWORK_TIMEOUT 1
            ERROR_NETWORK 2
            ERROR_AUDIO 3
            ERROR_CLIENT 5
            ERROR_SPEECH_TIMEOUT 6
            ERROR_NO_MATCH 7
            ERROR_RECOGNIZER_BUSY 8
            ERROR_INSUFFICIENT_PERMISSIONS 9
             */
            GcLog.d("error = " + error)
            text = ""
            //voiceActivity!!.commandSpoken("")
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            GcLog.d("onEvent")
        }

        override fun onPartialResults(partialResults: Bundle) {
            GcLog.d("onPartialResults")
        }

        override fun onReadyForSpeech(params: Bundle) {
            GcLog.d("onReadyForSpeech")
        }

        override fun onResults(results: Bundle) {
            var i: Int
            GcLog.d("onResults")

            val sk = "results_recognition"
            for (key in results.keySet()) {
                GcLog.d(" " + key + " => " + results[key] + ";")
            }
            text = results[sk].toString()
            text = text.replace("[", "")
            text = text.replace("]", "")
            val spoken = text.split(",").toTypedArray()
            var Sentence = ""
            GcLog.d("content = " + text)
            /*
            // this is for continous speech - doesn't work that well
            for (int s = 0; s < spoken.length; s++) {
                spoken[s] = spoken[s].toLowerCase();
                int st = 1;

                if (
                        spoken[s].contains("elisa") || spoken[s].contains("eliza") || spoken[s].contains("zaza") || spoken[s].contains("zazza") ||
                        spoken[s].contains("gabby") || spoken[s].contains("gaby") || spoken[s].contains("gabbie") ||
                        spoken[s].contains("gabriela") || spoken[s].contains("gabi")) {

                    for (i=0; i < spoken.length; i++) {
                        if (spoken[i].startsWith(" "))
                            spoken[i] = spoken[i].substring(1);
                        String[] word = spoken[i].split(" ");
                        int p = st;
                        for (int w = st; w < word.length; w++) {
                            word[w] = word[w].replace(" ", "");
                            Log.d(LOGTAG, "word " + i + "/" + w + " ='" + word[w] + "'");
                            if (word[w].equals(""))
                                continue;
                            if (p == st)
                                Sentence = word[w];
                            else
                                Sentence = Sentence + " " + word[w];
                            p++;
                        }
                        Log.d(LOGTAG, " Sentence " + i + " = '" + Sentence + "'");
                        int r = GameWrapper.vocabulary(Sentence);
                        Log.d(LOGTAG, "Vocabulary returned " + r);
                        if (r == 1) {
                            Log.d(LOGTAG, "Picked '" + Sentence + "'");
                            break;
                        }
                    }

                    if(i < spoken.length) {
                        ApplicationData.getActivity().commandSpoken(Sentence);
                    }
                    else {
                        String[] word = spoken[0].split(" ");
                        String newSentence = "";
                        int p = st;
                        for (int w = st; w < word.length; w++) {
                            word[w] = word[w].replace(" ", "");
                            if (word[w].equals(""))
                                continue;
                            if (p == st)
                                newSentence = word[w];
                            else
                                newSentence = newSentence + " " + word[w];
                            p++;
                        }
                        ApplicationData.getActivity().commandSpoken(newSentence);
                    }

                    //amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);

                    //ApplicationData.getActivity().rawSpeak("I'm listening... please say a command:");
                    if (!ApplicationData.isListening) {
                        Log.e(LOGTAG, "Voice activated");
                    }
                    ApplicationData.isListening = true;
                    while (ApplicationData.getActivity().isSpeaking())
                        ApplicationData.sleep(1000);
                    Log.e(LOGTAG, "Voice ready to listen");
                    //amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);

                    startListening();
                    return;
                }
            }

            if (!ApplicationData.isListening) {
                Log.e(LOGTAG, "Voice ignored");
                //startListening();
                return;
            }
            */
            i = 0
            while (i < spoken.size) {
                GcLog.d("spoken " + i + " ='" + spoken[i] + "'")
                val word = spoken[i].split(" ").toTypedArray()
                var p = 0
                for (w in word.indices) {
                    word[w] = word[w].replace(" ", "")
                    GcLog.d("word " + i + "/" + w + " ='" + word[w] + "'")
                    if (word[w] == "") continue
                    Sentence = if (p == 0) word[w] else Sentence + " " + word[w]
                    p++
                }
                GcLog.d(" Sentence " + i + " = " + Sentence)
                val r = 0 // TODO Gengame.vocabulary(Sentence)
                GcLog.d("Vocabulary returned " + r)
                if (r == 1) {
                    GcLog.d("Picked '" + Sentence + "'")
                    break
                }
                i++
            }
            if (i < spoken.size) {
                //voiceActivity!!.commandSpoken(Sentence)
            } else {
                /*voiceActivity!!.rawSpeak(""" I didn't understand what ${spoken[0]} means in this context.
I'm listening.
""")*/
                //while (speakingActivity!!.isSpeaking()) sleep(100)
                startListening()
            }
            isListening = false
            GcLog.e("Voice deactivated")
            //startListening();
        }

        override fun onRmsChanged(rmsdB: Float) {
        }
    }

    companion object {
        const val MSG_RECOGNIZER_START_LISTENING = 1
        const val MSG_RECOGNIZER_CANCEL = 2
    }
}