/*
 * Copyright (C) 2011-2014 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of libflvstream (https://github.com/fyhertz/libflvstream)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package cn.codepanda.live;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Random;

import cn.codepanda.live.audio.AudioStream;
import cn.codepanda.live.media.AbstractMediaFilesComposer;
import cn.codepanda.live.video.VideoStream;
import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

/**
 * A MediaRecorder that streams what it records using a packetizer from the RTP package.
 * You can't use this class directly !
 */
public abstract class MediaStream implements Stream {

	protected static final String TAG = "MediaStream";
	
	/** Raw audio/video will be encoded using the MediaCodec API with buffers. */
	public static final byte MODE_MEDIACODEC_API = 0x02;

	/** Raw audio/video will be encoded using the MediaCode API with a surface. */
	public static final byte MODE_MEDIACODEC_API_2 = 0x05;

	/** Prefix that will be used for all shared preferences saved by libflvstream */
	protected static final String PREF_PREFIX = "libflvstream-";

	protected static byte sSuggestedMode = MODE_MEDIACODEC_API;
	protected byte mMode;

	protected boolean mStreaming = false;
	protected MediaCodec mMediaCodec;
	
	static {
		// We determine whether or not the MediaCodec API should be used
		try {
			Class.forName("android.media.MediaCodec");
			// Will be set to MODE_MEDIACODEC_API at some point...
			sSuggestedMode = MODE_MEDIACODEC_API;
			Log.i(TAG,"Phone supports the MediaCoded API");
		} catch (ClassNotFoundException e) {
			Log.i(TAG,"Phone does not support the MediaCodec API");
			//post error..
		}
	}

	public MediaStream() {
		mMode = sSuggestedMode;
	}


	/**
	 * Returns an approximation of the bit rate consumed by the stream in bit per seconde.
	 */
	public long getBitrate() {
		return !mStreaming ? 0 : mmediacomposer.getBitrate(); 
	}

	/**
	 * Indicates if the {@link MediaStream} is streaming.
	 * @return A boolean indicating if the {@link MediaStream} is streaming
	 */
	public boolean isStreaming() {
		return mStreaming;
	}


	/** Starts the stream. */
	public synchronized void start() throws IllegalStateException, IOException {				
		encodeWithMediaCodec();
	}

	/** Stops the stream. */
	@SuppressLint("NewApi") 
	public synchronized  void stop() {
		if (mStreaming) {
			try {
				mMediaCodec.stop();
				mMediaCodec.release();
				mMediaCodec = null;
			} catch (Exception e) {
				e.printStackTrace();
			}	
			mStreaming = false;
		}
	}
 
	protected abstract void encodeWithMediaCodec() throws IOException;
	public abstract InputStream getInputStream();
	
	public String getStringParameter(String key){}
	
}
