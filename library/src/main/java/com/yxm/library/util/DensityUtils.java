package com.yxm.library.util;

import android.content.Context;

/**
 * 根据手机分辨率进行px和dp之间的互转
 * @author yxm
 *
 */
public class DensityUtils {

	/**
	 * dp转px
	 */
	public static float dp2px(Context context,float dpValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return dpValue*scale+0.5f;
	}

	/**
	 * px转dp
	 */
	public static float px2dp(Context context,float pxValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return pxValue/scale+0.5f;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 */
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 */
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}
}
