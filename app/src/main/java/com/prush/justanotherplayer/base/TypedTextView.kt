/*
 * Copyright 2019 Purushottam Pawar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prush.justanotherplayer.base


import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.util.Preconditions
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class TypedTextView : AppCompatTextView, LifecycleObserver {
    private var mTypedText: CharSequence? = null
    private var mOnCharacterTypedListener: OnCharacterTypedListener? = null
    private var mIndex: Int = 0

    private var mSentencePauseMillis = DEFAULT_SENTENCE_PAUSE
    private var mCursorBlinkSpeedMillis = DEFAULT_CURSOR_BLINK_SPEED
    private var mRandomTypingSeedMillis = DEFAULT_RANDOM_TYPING_SEED
    private var mTypingSpeedMillis = DEFAULT_TYPING_SPEED
    private var mbShowCursor = SHOW_CURSOR
    private var mbSplitSentences = SPLIT_SENTENCES
    private var mbRandomizeTyping = RANDOMIZE_TYPING
    private var mbPlayKeyStrokesAudio = PLAY_KEYSTROKES_AUDIO
    private var mKeyStrokeAudioRes = DEFAULT_KEYSTROKES_AUDIO_RES

    private var mMediaPlayer: MediaPlayer? = null
    private val mHandler = Handler()

    private val mTypeWriter = object : Runnable {
        override fun run() {
            if (mIndex < mTypedText!!.length) {
                //extract characters by index
                var charSequence = mTypedText!!.subSequence(0, mIndex)

                //append cursor
                if (mbShowCursor) {
                    charSequence = "$charSequence|"
                }

                randomizeTyping()

                //play keystrokes
                playKeystrokes()

                //set character by character
                text = charSequence

                if (mOnCharacterTypedListener != null) {
                    mOnCharacterTypedListener!!.onCharacterTyped(mTypedText!![mIndex], mIndex)
                }

                mHandler.postDelayed(this, mTypingSpeedMillis)

                addSentencePause()

                mIndex++
            } else {
                //end of text.
                mHandler.removeCallbacks(this)

                //stop playing keystrokes
                stopKeystrokes()

                //typing completed. show blinking cursor.
                if (mbShowCursor) {
                    mHandler.postDelayed(mCursorProxyRunnable, mCursorBlinkSpeedMillis)
                }
            }
        }
    }

    private val mCursorProxyRunnable = object : Runnable {
        override fun run() {
            /*

            If TextView gravity is set to center, appending and removing pipe in each execution,
            re-aligns the text in order to keep it centered.

            To overcome this, an empty space is added which replaces pipe | in order to keep the text in same position.

            if cursor is not shown and empty space is not shown, append cursor.
            else Replace empty space with cursor/pipe.
            else Replace cursor/pipe with empty space.

            */
            var charSequence = mTypedText

            if (charSequence!![charSequence.length - 1] != '|' && charSequence[charSequence.length - 1] != ' ') {
                charSequence = "$charSequence|"
            } else if (charSequence[charSequence.length - 1] == ' ') {
                charSequence = charSequence.subSequence(0, charSequence.length - 1)
                charSequence = "$charSequence|"
            } else {
                charSequence = charSequence.subSequence(0, charSequence.length - 1)
                charSequence = "$charSequence "
            }
            mTypedText = charSequence
            text = charSequence
            mHandler.postDelayed(this, mCursorBlinkSpeedMillis)
        }
    }

    /**
     * Returns a LifecycleObserver that expects to be notified when the LifecycleOwner changes state.
     * Add this as a [LifecycleObserver] to [androidx.appcompat.app.AppCompatActivity] or
     * [androidx.fragment.app.Fragment]
     *
     * @return LifecycleObserver
     */
    val lifecycleObserver: LifecycleObserver
        get() = this


    /**
     * Callback to be invoked when typing is started.
     */
    interface OnCharacterTypedListener {
        /**
         * Provides current index from the passed String.
         *
         * @param index The index of last typed character on screen.
         */
        fun onCharacterTyped(character: Char, index: Int)
    }

    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    ) {

        mSentencePauseMillis = DEFAULT_SENTENCE_PAUSE
        mCursorBlinkSpeedMillis = DEFAULT_CURSOR_BLINK_SPEED
        mRandomTypingSeedMillis = DEFAULT_RANDOM_TYPING_SEED
        mTypingSpeedMillis = DEFAULT_TYPING_SPEED
        mbShowCursor = SHOW_CURSOR
        mbSplitSentences = SPLIT_SENTENCES
        mbRandomizeTyping = RANDOMIZE_TYPING
        mbPlayKeyStrokesAudio = PLAY_KEYSTROKES_AUDIO
        mKeyStrokeAudioRes = DEFAULT_KEYSTROKES_AUDIO_RES
    }

    private fun stopKeystrokes() {
        if (mbPlayKeyStrokesAudio) {
            mMediaPlayer!!.stop()
        }
    }

    private fun playKeystrokes() {
        if (mbPlayKeyStrokesAudio) {
            mMediaPlayer!!.start()
        }
    }

    private fun prepareMediaPlayer() {
        if (mbPlayKeyStrokesAudio) {
            mMediaPlayer = MediaPlayer.create(context, mKeyStrokeAudioRes)
        }
    }

    private fun removeCallbacks() {
        mHandler.removeCallbacks(mTypeWriter)
        if (mbShowCursor) {
            mHandler.removeCallbacks(mCursorProxyRunnable)
        }
    }

    private fun pauseKeyStrokes() {
        if (mbPlayKeyStrokesAudio) {
            mMediaPlayer!!.pause()
        }
    }

    private fun randomizeTyping() {
        if (mbRandomizeTyping) {
            if (mTypingSpeedMillis == 0L) {
                mTypingSpeedMillis = mRandomTypingSeedMillis
            }
            mTypingSpeedMillis =
                mRandomTypingSeedMillis + Random().nextInt(mTypingSpeedMillis.toInt())
        }
    }

    private fun addSentencePause() {
        //introduce sentence pause
        if (mIndex != 0 && (mTypedText!![mIndex - 1] == '.' || mTypedText!![mIndex - 1] == ',')) {
            //pause keystrokes as well
            pauseKeyStrokes()

            mHandler.removeCallbacks(mTypeWriter)
            mHandler.postDelayed(mTypeWriter, mSentencePauseMillis)
        }
    }

    /**
     * Set text to be typed with the TypeWriter effect.
     *
     * @param text [String] text to be typed character by character.
     */
    fun setTypedText(text: String) {
        Preconditions.checkNotNull(text)

        //split sentences on new line
        mTypedText = if (mbSplitSentences) splitSentences(text) else text

        mIndex = 0
        setText("")

        removeCallbacks()

        prepareMediaPlayer()

        //start typing
        mHandler.postDelayed(mTypeWriter, mTypingSpeedMillis)
    }

    /**
     * Set text to be typed with the TypeWriter effect.
     *
     * @param resId int resource Id of String to be typed.
     */
    fun setTypedText(@StringRes resId: Int) {
        val text = context.getString(resId)
        setTypedText(text)
    }

    /**
     * Get the text to be typed.
     *
     * @return CharSequence text being/to be typed by the view.
     */
    override fun getText(): CharSequence? {
        return mTypedText
    }

    /**
     * Set text to be typed with the TypeWriter effect.
     *
     * @param charSequence [CharSequence] to be typed character by character.
     */
    fun setTypedText(charSequence: CharSequence) {
        val text = charSequence.toString()
        setTypedText(text)
    }

    private fun splitSentences(text: String): String {
        Preconditions.checkNotNull(text)
        var modifiedText = text
        var index = modifiedText.indexOf('.')
        var lastIndex = modifiedText.lastIndexOf('.')
        if (index != lastIndex) {
            //multiple sentences found.
            //introduce new lines for every full stop except the last one terminating string.
            do {
                modifiedText = modifiedText.replaceFirst("\\. ".toRegex(), ".\n")

                index = modifiedText.indexOf('.', index + 1)
                lastIndex = modifiedText.lastIndexOf('.')

            } while (index != -1 && index != lastIndex)
        }

        return modifiedText
    }

    /**
     * Register a callback to be invoked when typing is started.
     *
     * @param onCharacterTypedListener [OnCharacterTypedListener]
     */
    fun setOnCharacterTypedListener(onCharacterTypedListener: OnCharacterTypedListener) {
        mOnCharacterTypedListener = onCharacterTypedListener
    }

    /**
     * Show cursor while typing
     *
     * @param bShowCursor boolean display blinking cursor while typing.
     */
    fun showCursor(bShowCursor: Boolean) {
        this.mbShowCursor = bShowCursor
    }

    /**
     * Split sentences on a new line.
     *
     * @param bSplitSentences boolean Type Writer splits sentences onto new line based on fullstops
     * found in the passed string
     */
    fun splitSentences(bSplitSentences: Boolean) {
        this.mbSplitSentences = bSplitSentences
    }

    /**
     * Set duration to wait after every sentence
     *
     * @param sentencePauseMillis long duration in milliseconds to wait after every sentence
     */
    fun setSentencePause(sentencePauseMillis: Long) {
        mSentencePauseMillis = sentencePauseMillis
    }

    /**
     * Set duration to wait after every cursor blink
     *
     * @param cursorBlinkSpeedMillis long duration in milliseconds between every cursor blink
     */
    fun setCursorBlinkSpeed(cursorBlinkSpeedMillis: Long) {
        showCursor(true)
        mCursorBlinkSpeedMillis = cursorBlinkSpeedMillis
    }

    /**
     * Set duration to wait after every character typed
     *
     * @param typingSpeedMillis long duration in milliseconds to wait after every character typed
     */
    fun setTypingSpeed(typingSpeedMillis: Long) {
        mTypingSpeedMillis = typingSpeedMillis
    }

    /**
     * Randomize Typing delay
     *
     * @param seed long seed to randomize the default typing speed
     */
    fun randomizeTypeSeed(seed: Long) {
        randomizeTypingSpeed(true)
        mRandomTypingSeedMillis = seed
    }

    /**
     * Simulate human typing by randomize typing speed
     *
     * @param bRandomizeTypeSpeed boolean enable random typing speed.
     */
    fun randomizeTypingSpeed(bRandomizeTypeSpeed: Boolean) {
        mbRandomizeTyping = bRandomizeTypeSpeed
    }

    /**
     * Play default keystrokes sound along with typing characters
     *
     * @param bPlayKeystrokesAudio boolean
     */
    fun playKeyStrokesAudio(bPlayKeystrokesAudio: Boolean) {
        mbPlayKeyStrokesAudio = bPlayKeystrokesAudio
    }

    /**
     * Play specified keystrokes sound along with typing characters
     *
     * @param keyStrokeAudioRes @RawRes int resourceId of audio resource
     */
    fun playKeyStrokesAudioWith(@RawRes keyStrokeAudioRes: Int) {
        playKeyStrokesAudio(true)
        mKeyStrokeAudioRes = keyStrokeAudioRes
    }

    class Builder(private val mTypedTextView: TypedTextView) {
        private var mRandomTypingSeedMillis = DEFAULT_RANDOM_TYPING_SEED
        private var mbShowCursor = SHOW_CURSOR

        /**
         * Randomize Typing delay
         *
         * @param seed long seed to randomize the default typing speed
         */
        fun randomizeTypeSeed(seed: Long): Builder {
            this.mRandomTypingSeedMillis = seed
            return this
        }

        /**
         * Show cursor while typing
         *
         * @param bShowCursor boolean display blinking cursor while typing.
         */
        fun showCursor(bShowCursor: Boolean): Builder {
            this.mbShowCursor = bShowCursor
            return this
        }

        /**
         * Simulate human typing by randomize typing speed
         *
         * @param bRandomizeTypeSpeed boolean enable random typing speed.
         */
        fun randomizeTypingSpeed(bRandomizeTypeSpeed: Boolean): Builder {
            mTypedTextView.randomizeTypingSpeed(bRandomizeTypeSpeed)
            return this
        }

        /**
         * Play specified keystrokes sound along with typing characters
         *
         * @param keyStrokeAudioRes @RawRes int resourceId of audio resource
         */
        fun playKeyStrokesAudioWith(@RawRes keyStrokeAudioRes: Int): Builder {
            mTypedTextView.playKeyStrokesAudioWith(keyStrokeAudioRes)
            return this
        }

        /**
         * Play default keystrokes sound along with typing characters
         *
         * @param bPlayKeystrokesAudio boolean
         */
        fun playKeyStrokesAudio(bPlayKeystrokesAudio: Boolean): Builder {
            mTypedTextView.playKeyStrokesAudio(bPlayKeystrokesAudio)
            return this
        }

        /**
         * Split sentences on a new line.
         *
         * @param bSplitSentences boolean Type Writer splits sentences onto new line based on fullstops
         * found in the passed string
         */
        fun splitSentences(bSplitSentences: Boolean): Builder {
            mTypedTextView.splitSentences(bSplitSentences)
            return this
        }

        /**
         * Set duration to wait after every sentence
         *
         * @param sentencePauseMillis long duration in milliseconds to wait after every sentence
         */
        fun setSentencePause(sentencePauseMillis: Long): Builder {
            mTypedTextView.setSentencePause(sentencePauseMillis)
            return this
        }

        /**
         * Set duration to wait after every cursor blink
         *
         * @param cursorBlinkSpeedMillis long duration in milliseconds between every cursor blink
         */
        fun setCursorBlinkSpeed(cursorBlinkSpeedMillis: Long): Builder {
            mTypedTextView.setCursorBlinkSpeed(cursorBlinkSpeedMillis)
            return this
        }

        /**
         * Set duration to wait after every character typed
         *
         * @param typingSpeedMillis long duration in milliseconds to wait after every character typed
         */
        fun setTypingSpeed(typingSpeedMillis: Long): Builder {
            mTypedTextView.setTypingSpeed(typingSpeedMillis)
            return this
        }

        fun build(): TypedTextView {
            return mTypedTextView
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onViewStarted() {
        //resume typing if view was stopped before entire text was displayed.
        if (mTypedText != null && mIndex != 0 && mIndex != mTypedText!!.length) {
            //resume playing keystrokes
            playKeystrokes()
            mHandler.postDelayed(mTypeWriter, mTypingSpeedMillis)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onViewStopped() {
        //stop typing as view is now in stopped state.
        removeCallbacks()

        //pause playing keystrokes
        pauseKeyStrokes()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentIndex = mIndex
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mIndex = savedState.currentIndex
    }

    /**
     * Class to save view's internal state across lifecycle owner's state changes.
     */
    private class SavedState : BaseSavedState {
        var currentIndex: Int = 0

        constructor(source: Parcel) : super(source) {
            currentIndex = source.readInt()
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(currentIndex)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        private const val DEFAULT_SENTENCE_PAUSE: Long = 1500
        private const val DEFAULT_CURSOR_BLINK_SPEED: Long = 530
        private const val DEFAULT_RANDOM_TYPING_SEED: Long = 75
        private const val DEFAULT_TYPING_SPEED: Long = 175
        private const val DEFAULT_KEYSTROKES_AUDIO_RES = -1

        private const val SHOW_CURSOR = true
        private const val SPLIT_SENTENCES = true
        private const val RANDOMIZE_TYPING = true
        private const val PLAY_KEYSTROKES_AUDIO = false
    }
}