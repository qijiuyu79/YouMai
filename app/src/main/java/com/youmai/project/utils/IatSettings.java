package com.youmai.project.utils;


import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

/**
 * 听写设置界面
 */
public class IatSettings extends PreferenceActivity implements
		OnPreferenceChangeListener {

	public static final String PREFER_NAME = "com.iflytek.setting";
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
	
	/**
	 * 初始化监听器。
	 */
	public static  InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
		}
	};
	
	
	/**
	 * 参数设置
	 * 
	 * @return
	 */
	public static void setParam(SharedPreferences mSharedPreferences,SpeechRecognizer mIat) {
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
	}
}
