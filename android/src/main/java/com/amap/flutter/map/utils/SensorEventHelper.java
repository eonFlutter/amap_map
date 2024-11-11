package com.amap.flutter.map.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.amap.api.maps.model.Marker;

public class SensorEventHelper implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mRotationVectorSensor;
	private Sensor mGravitySensor;
	private Sensor mMagneticFieldSensor;

	private long lastTime = 0;
	private final int TIME_SENSOR = 16; // 16 毫秒更新一次
	private float mAngle;
	private Context mContext;
	private Marker mMarker;

	private float bearing; // 地图选择的角度

	private float[] mRotationMatrix = new float[9];  // 用于保存旋转矩阵
	private float[] mOrientation = new float[3];     // 用于保存方位角、俯仰角、滚转角
	private float[] gravity = new float[3];          // 用于保存重力传感器数据
	private float[] geomagnetic = new float[3];      // 用于保存磁力计传感器数据

	public void updateBearing(float f) {
		bearing = f;
	}

	public SensorEventHelper(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); // 使用旋转向量传感器
		mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); // 使用重力传感器
		mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // 使用磁场传感器
	}

	public void registerSensorListener() {
		mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
	}

	public void unRegisterSensorListener() {
		mSensorManager.unregisterListener(this, mRotationVectorSensor);
		mSensorManager.unregisterListener(this, mGravitySensor);
		mSensorManager.unregisterListener(this, mMagneticFieldSensor);
	}

	public void setCurrentMarker(Marker marker) {
		mMarker = marker;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// 这里没有处理，通常情况下可以忽略

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
			return;
		}

		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			// 保存重力传感器数据
			System.arraycopy(event.values, 0, gravity, 0, event.values.length);
//			Log.d("SensorEventHelper", "Gravity sensor data: " + gravity[0] + ", " + gravity[1] + ", " + gravity[2]);
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// 保存磁力计传感器数据
			System.arraycopy(event.values, 0, geomagnetic, 0, event.values.length);
//			Log.d("SensorEventHelper", "Magnetic field sensor data: " + geomagnetic[0] + ", " + geomagnetic[1] + ", " + geomagnetic[2]);
		}

		// 如果重力传感器和磁力计传感器数据都已准备好
		if (gravity != null && geomagnetic != null) {
			// 获取旋转矩阵
			boolean success = SensorManager.getRotationMatrix(mRotationMatrix, null, gravity, geomagnetic);
//			Log.d("SensorEventHelper", "success--------------------x");
			if (success) {
//				Log.d("SensorEventHelper", "success--------------------z");

				// 获取方位角、俯仰角和滚转角
				SensorManager.getOrientation(mRotationMatrix, mOrientation);

				// mOrientation[0] 是设备的方位角，表示设备与地理北极的夹角
				float x = mOrientation[0]; // 获取设备的方向角度（方位角）
				x = (float) Math.toDegrees(x);
				Log.d("SensorEventHelper", "change--------------------setRotateAngle:" + x);

				// 获取当前设备的旋转角度
				x += getScreenRotationOnPhone(mContext);
				x %= 360.0F;
				if (x > 180.0F)
					x -= 360.0F;
				else if (x < -180.0F)
					x += 360.0F;

				// 合并设备的旋转角度和地图的旋转角度
				float finalAngle = (x - bearing) % 360.0F;
				if (finalAngle > 180.0F) {
					finalAngle -= 360.0F;
				} else if (finalAngle < -180.0F) {
					finalAngle += 360.0F;
				}

				// 如果角度变化大于3度，则更新标记的旋转角度
				if (Math.abs(mAngle - finalAngle) >= 3.0f) {
					mAngle = Float.isNaN(finalAngle) ? 0 : finalAngle;
					if (mMarker != null) {
						mMarker.setRotateAngle(360 - mAngle); // 设置标记旋转角度
					}
				}

				lastTime = System.currentTimeMillis();
			}
		}
	}

	public static int getScreenRotationOnPhone(Context context) {
		final Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
			case Surface.ROTATION_0:
				return 0;

			case Surface.ROTATION_90:
				return 90;

			case Surface.ROTATION_180:
				return 180;

			case Surface.ROTATION_270:
				return -90;
		}
		return 0;
	}
}
