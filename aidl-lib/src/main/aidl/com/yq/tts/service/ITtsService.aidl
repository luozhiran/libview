/**
 * ****************************************************************
 *
 * Copyright (C) YQTH Corporation. All rights reserved.
 *
 * FileName : ITtsService.aidl
 * Description : Interface for remote TTS service revoke.
 *
 ******************************************************************
 */
package com.yq.tts.service;
import com.yq.tts.service.TtsTask;

interface ITtsService {
        /**
         * Activates the permission to access all other methods,
         * which need internet connection and some very small data transition.
         * It is only needed to call once after apk install.
         * @param
         * @return
         *     0 if successful.
         *     -1 if network connection failed.
         *     -2 if there is no permission for the calling application. Read online doc to apply permission.
         */
        int activate();

        /**
         * Sends speak task to TTS engine
         * @param ttsTask
         *     The tts task object used in TTS task queue
         * @return
         *     0 if successful.
         *     -1 if no permission.
         *     -2 if not in play period, either the switch is off or out of the time control window.
         *     -3 if initialization failed.
         */
        int speak(in TtsTask ttsTask);

         /**
         * Removes the first task sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
        int stopCaller(String caller);

         /**
         * Removes all tasks sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
        int stopCallerAll(String caller);

         /**
         * Stops the current speaking task. The next task of speaking will start.
         * @param tag
         *     The status of the stop task
         * @return
         *     0 if successful.
         */
        int stop(String tag);

        /**
         * Saves the TTS speech to the specified path
         * @param str
         *     The input string which you want to transfer to speech
         * @param filePathDir
         *     The path where save the output .pcm file
         * @return
         *     0 if successful.
         */
        int save(String str, String filePathDir);

        /**
         * Stops the current speaking task and clear the task queue.
         * @param
         * @return
         *     0 if successful.
         */
        int stopAll();

         /**
         * Blocks the current speaking task. The speaking task will be restarted
         * If unBlock().
         * @param
         * @return
         *     0 if successful.
         */
        int block();

         /**
         * Unblocks the current speaking task. The speaking task will be restarted.
         * @param
         * @return
         *     0 if successful.
         */
        int unBlock();

         /**
         * Gets the voice object of the first speaking task.
         * @param
         * @return TtsTask
         *     The tts task object of the first speaking task.
         */
        TtsTask getCurTtsTask();

        /**
         * Sets the timbre parameter to the speaker.
         * @param alpha
         *     The first parameter
         * @param beta
         *     The second parameter
         * @return
         *     0 if successful.
         */
        int setTimbre(int alpha, int beta);

        /**
         * Sets the pitch scale to the speaker.
         * @param pitchScale
         *     The pitch scale parameter
         * @return
         *     0 if successful.
         */
        int setPitchScale(float pitchScale);

        /**
         * Sets the pitch mode to the speaker.
         * @param pitchMode
         *     The pitch mode parameter
         *     0 means normal generated pitch
         *     1 means aliens style pitch
         *     2 means ghost style pitch
         * @return
         *     0 if successful.
         */
        int setPitchMode(int pitchMode);

        /**
         * Sets the speed scale to the speaker.
         * @param speedScale
         *     The speed scale parameter
         * @return
         *     0 if successful.
         */
        int setSpeedScale(float speedScale);

        /**
         * Sets the echo decay to the speaker.
         * @param echoDecay
         *     The echo decay parameter
         * @return
         *     0 if successful.
         */
        int setEchoDecay(float echoDecay);

        /**
         * Sets the echo delay to the speaker.
         * @param echoDelay
         *     The echo delay parameter
         * @return
         *     0 if successful.
         */
        int setEchoDelay(float echoDelay);

        /**
         * Sets the echo init volume to the speaker.
         * @param echoInitVolume
         *     The echo init volume parameter
         * @return
         *     0 if successful.
         */
        int setEchoInitVolume(float echoInitVolume);
}
