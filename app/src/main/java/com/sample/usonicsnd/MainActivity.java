package com.sample.usonicsnd;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.icu.text.MessageFormat;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
	private static final int SAMPLE_RATE = 48000;
	private static final float SEC_PER_SAMPLEPOINT = 1.0f / SAMPLE_RATE;
	private static final int SOUND_TIME = 3;
	private static final int AMP = 4000;
	private static final int DO = 262 * 2;
	private static final int RE = 294 * 2;
	private static final int MI = 330 * 2;
	private static final int FA = 349 * 2;
	private static final int SO = 392 * 2;
	private static final int RA = 440 * 2;
	private static final int SI = 494 * 2;
	private static final int HEXDO = 1;
	private static final int HEXRE = 2;
	private static final int HEXMI = 3;
	private static final int HEXFA = 4;
	private static final int HEXSO = 5;
	private static final int HEXRA = 6;
	private static final int HEXSI = 7;
	private static final int HEXDOMI = 8;
	private static final int HEXREFA = 9;
	private static final int HEXMISO = 10;
	private static final int HEXFARA = 11;
	private static final int HEXSOMI = 12;
	private static final int HEXRARE = 13;
	private static final int HEXSIRE = 14;
	final int bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
	AudioTrack mAudioTrack = new AudioTrack.Builder()
			.setAudioAttributes(new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_UNKNOWN)
					.setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
					.build())
			.setAudioFormat(new AudioFormat.Builder()
					.setEncoding(AudioFormat.ENCODING_PCM_16BIT)
					.setSampleRate(SAMPLE_RATE)
					.setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
					.build())
			.setBufferSizeInBytes(bufferSizeInBytes)
			.build();
	private static final Map<Integer, short[]> mSineWaves;
	static {
		Map<Integer, short[]> tmpmap = new HashMap<Integer, short[]>();
		tmpmap.put(  280, createSineWave(  280, AMP));
		tmpmap.put(  400, createSineWave(  400, AMP));
		tmpmap.put(  800, createSineWave(  800, AMP));
		tmpmap.put( 1600, createSineWave( 1600, AMP));
		tmpmap.put( 2000, createSineWave( 2000, AMP));
		tmpmap.put( 4000, createSineWave( 4000, AMP));
		tmpmap.put( 8000, createSineWave( 8000, AMP));
		tmpmap.put(10000, createSineWave(10000, AMP));
		tmpmap.put(15000, createSineWave(15000, AMP));
		tmpmap.put(20000, createSineWave(20000, AMP));
		tmpmap.put(21000, createSineWave(21000, AMP));
		tmpmap.put(22000, createSineWave(22000, AMP));
		tmpmap.put(23000, createSineWave(23000, AMP));
		/* Song */
		tmpmap.put(HEXDO, createSineWave(DO, AMP));	/* ??? */
		tmpmap.put(HEXRE, createSineWave(RE, AMP));	/* ??? */
		tmpmap.put(HEXMI, createSineWave(MI, AMP));	/* ??? */
		tmpmap.put(HEXFA, createSineWave(FA, AMP));	/* ?????? */
		tmpmap.put(HEXSO, createSineWave(SO, AMP));	/* ??? */
		tmpmap.put(HEXRA, createSineWave(RA, AMP));	/* ??? */
		tmpmap.put(HEXSI, createSineWave(SI, AMP));	/* ??? */
		/* Song(??????) */
		tmpmap.put(HEXDOMI, createSineWaveChord(DO, AMP, MI, AMP));	/* ???,??? */
		tmpmap.put(HEXREFA, createSineWaveChord(RE, AMP, FA, AMP));	/* ???,?????? */
		tmpmap.put(HEXMISO, createSineWaveChord(MI, AMP, SO, AMP));	/* ???,??? */
		tmpmap.put(HEXFARA, createSineWaveChord(FA, AMP, RA, AMP));	/* ??????,??? */
		tmpmap.put(HEXSOMI, createSineWaveChord(SO, AMP, MI, AMP));	/* ???,??? */
		tmpmap.put(HEXRARE, createSineWaveChord(RA, AMP, RE, AMP));	/* ???,??? */
		tmpmap.put(HEXSIRE, createSineWaveChord(SI, AMP, RE, AMP));	/* ???,??? */

		mSineWaves = Collections.unmodifiableMap(tmpmap);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* ?????????????????? ????????????Audio???????????????????????????????????? */
		if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			/* RECORD_AUDIO???????????????????????? */
			requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2222);
		}

		/* ????????????????????????????????? */
		View.OnClickListener listner = v -> {
			/* ????????????????????????????????????????????? */
			if( ((MaterialButtonToggleGroup)findViewById(R.id.grpPlay)).getCheckedButtonId() == View.NO_ID)
				return;

			new Thread(() -> {
				/* ??????????????? */
				String idstr = (String)((MaterialButton)v).getText();
				String Hzstr = idstr.replace("Hz", "").replace("k", "000");
				if(Hzstr.contains("."))
					Hzstr = Hzstr.replace(".","").replace("000", "00");
				int FREQ = Integer.parseInt(Hzstr);
				TLog.d("FREQS={0}[hz]", FREQ);

				/* AudioTrack?????? */
				TLog.d("AudioTrack???????????? 5s");
				mAudioTrack.play();
				/* ??????????????????????????? */
				new Thread(() -> {
					for(int lpct = 0; lpct <= SOUND_TIME; lpct++) {
						int finalLpct = lpct;
						runOnUiThread(() -> { ((TextView)findViewById(R.id.txtTimer)).setText(MessageFormat.format("{0}s", finalLpct));});
						try { Thread.sleep(1000); } catch(InterruptedException e) { e.printStackTrace();}
					}
				}).start();
				for (int lpct = 0; lpct < SOUND_TIME; lpct++)	/* 5?????????????????? */
					mAudioTrack.write(mSineWaves.get(FREQ), 0, SAMPLE_RATE, AudioTrack.WRITE_BLOCKING);
				TLog.d("AudioTrack????????????");
				mAudioTrack.stop();
				mAudioTrack.flush();
				TLog.d("AudioTrack????????????");
			}).start();
		};
		findViewById(R.id.btn280hz).setOnClickListener(listner);
		findViewById(R.id.btn400hz).setOnClickListener(listner);
		findViewById(R.id.btn800hz).setOnClickListener(listner);
		findViewById(R.id.btn1600hz).setOnClickListener(listner);
		findViewById(R.id.btn2khz).setOnClickListener(listner);
		findViewById(R.id.btn4khz).setOnClickListener(listner);
		findViewById(R.id.btn8khz).setOnClickListener(listner);
		findViewById(R.id.btn10khz).setOnClickListener(listner);
		findViewById(R.id.btn15khz).setOnClickListener(listner);
		findViewById(R.id.btn20khz).setOnClickListener(listner);
		findViewById(R.id.btn21khz).setOnClickListener(listner);
		findViewById(R.id.btn22khz).setOnClickListener(listner);
		findViewById(R.id.btn23khz).setOnClickListener(listner);
		/* Song */
		findViewById(R.id.btnSong).setOnClickListener(v -> {
			/* ????????????????????????????????????????????? */
			if( ((MaterialButtonToggleGroup)findViewById(R.id.grpPlay)).getCheckedButtonId() == View.NO_ID)
				return;

			new Thread(() -> {
				/* AudioTrack?????? */
				TLog.d("AudioTrack???????????? 5s");
				mAudioTrack.play();
				/* ????????? */
				mAudioTrack.write(mSineWaves.get(HEXDO), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXRE), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXMI), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXFA), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXMI), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXRE), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXDO), 0, SAMPLE_RATE, AudioTrack.WRITE_BLOCKING);
				/* ?????????(??????) */
				mAudioTrack.write(mSineWaves.get(HEXDOMI), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXREFA), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXMISO), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXFARA), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXMISO), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXREFA), 0, SAMPLE_RATE/2, AudioTrack.WRITE_BLOCKING);
				mAudioTrack.write(mSineWaves.get(HEXDOMI), 0, SAMPLE_RATE, AudioTrack.WRITE_BLOCKING);
				TLog.d("AudioTrack????????????");
				mAudioTrack.stop();
				mAudioTrack.flush();
				TLog.d("AudioTrack????????????");
			}).start();
		});
	}

	/* 1??????????????????????????????????????? */
	private static short[] createSineWave(int freq, int amplitude) {
		short[] retSineWave = new short[SAMPLE_RATE]; /* 1?????????????????????????????? */

		for (int i = 0; i < SAMPLE_RATE; i++) {
			double currentSec = i * SEC_PER_SAMPLEPOINT; /* ??????????????????????????? */
			/* y(t) = Amp * sin(2?? * f * t) */
			double val = amplitude * Math.sin(2.0 * Math.PI * freq * currentSec);
			retSineWave[i] = (short)val;
		}
		return retSineWave;
	}

	/* 1???????????????????????????????????????(??????????????????) */
	private static short[] createSineWave(short[] orgwave, int freq, int amplitude) {
		short[] retSineWave = new short[SAMPLE_RATE]; /* 1?????????????????????????????? */

		for (int i = 0; i < SAMPLE_RATE; i++) {
			double currentSec = i * SEC_PER_SAMPLEPOINT; /* ??????????????????????????? */
			/* y(t) = Amp * sin(2?? * f * t) */
			double val = amplitude * Math.sin(2.0 * Math.PI * freq * currentSec);
			retSineWave[i] += orgwave[i] + (short)val;
		}
		return retSineWave;
	}

	/* 1??????????????????????????????????????? */
	private static short[] createSineWaveChord(int freq1, int amplitude1, int freq2, int amplitude2) {
		short[] wavebase = createSineWave(freq1, amplitude1);
		return createSineWave(wavebase, freq2, amplitude2);
	}

	/* ?????????????????????????????? */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		TLog.d("s");
		/* ?????????????????????????????????????????????. */
		if (requestCode == 2222) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				/* RECORD_AUDIO?????????????????? */
				TLog.d("RECORD_AUDIO????????????????????????!! OK.");
			} else {
				ErrPopUp.create(MainActivity.this).setErrMsg("?????????????????????\n\"??????\"????????????????????????????????????AUDIO???????????????????????????????????????\n??????????????????").Show(MainActivity.this);
			}
		}
		/* ???????????????????????????????????? */
		else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
		TLog.d("e");
	}

	/* ???????????? */
	public static class TLog {
		public static void d(String logstr) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			Log.d("aaaaa", MessageFormat.format("{0} {1}",head, logstr));
		}

		public static void d(String fmt, Object... args) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			String arglogstr =  MessageFormat.format(fmt, (Object[])args);
			Log.d("aaaaa", MessageFormat.format("{0} {1}",head, arglogstr));
		}

		public static void i(String logstr) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			Log.i("aaaaa", MessageFormat.format("{0} {1}",head, logstr));
		}

		public static void i(String fmt, Object... args) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			String arglogstr =  MessageFormat.format(fmt, (Object[])args);
			Log.i("aaaaa", MessageFormat.format("{0} {1}",head, arglogstr));
		}

		public static void w(String logstr) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			Log.w("aaaaa", MessageFormat.format("{0} {1}",head, logstr));
		}

		public static void w(String fmt, Object... args) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			String arglogstr =  MessageFormat.format(fmt, (Object[])args);
			Log.w("aaaaa", MessageFormat.format("{0} {1}",head, arglogstr));
		}

		public static void e(String logstr) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			Log.e("aaaaa", MessageFormat.format("{0} {1}",head, logstr));
		}

		public static void e(String fmt, Object... args) {
			StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[1];
			String head = MessageFormat.format("{0}::{1}({2})", throwableStackTraceElement.getClassName(), throwableStackTraceElement.getMethodName(), throwableStackTraceElement.getLineNumber());
			String arglogstr =  MessageFormat.format(fmt, (Object[])args);
			Log.e("aaaaa", MessageFormat.format("{0} {1}",head, arglogstr));
		}
	}

	/* ?????????popup */
	public static class ErrPopUp extends PopupWindow {
		/* ????????????????????? */
		private ErrPopUp(Activity activity) {
			super(activity);
		}

		/* windows?????? */
		public static ErrPopUp create(Activity activity) {
			ErrPopUp retwindow = new ErrPopUp(activity);
			View popupView = activity.getLayoutInflater().inflate(R.layout.popup_layout, null);
			popupView.findViewById(R.id.btnClose).setOnClickListener(v -> {
				android.os.Process.killProcess(android.os.Process.myPid());
			});
			retwindow.setContentView(popupView);
			/* ???????????? */
			retwindow.setBackgroundDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.popup_background, null));

			/* ?????????????????????View?????????????????????????????????????????? */
			retwindow.setOutsideTouchable(true);
			retwindow.setFocusable(true);

			/* ???????????????????????? ????????????300dp */
			float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, activity.getResources().getDisplayMetrics());
			retwindow.setWidth((int)width);
			retwindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
			return retwindow;
		}

		/* ??????????????? */
		public ErrPopUp setErrMsg(String errmsg) {
			((TextView)this.getContentView().findViewById(R.id.txtErrMsg)).setText(errmsg);
			return this;
		}

		/* ?????? */
		public void Show(Activity activity) {
			View anchor = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
			this.showAtLocation(anchor, Gravity.CENTER,0, 0);
		}
	}
}