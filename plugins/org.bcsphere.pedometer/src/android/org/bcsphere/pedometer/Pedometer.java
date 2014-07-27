package org.bcsphere.pedometer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Pedometer extends CordovaPlugin {

	private int times = 0;
	private Context context;
	private double output = 0;
	private double degree = 0;
	private int countSteps = 0;
	private final double K = 0.5;
	private final double alpha = 0.3;
	private boolean initialed = false;
	private double max = 0, min = 0;
	private double normFilter = 0;
	private SensorManager sensorManager;
	private double stepLength = 0;
	private double window[] = new double[16];
	private CallbackContext getDegreesCallBack;
	private OrientationListener orientationListener;
	private AccelerometerListener accelerometerListener;
	double ALPHA11 = 0.09;
    
	//definition for the Accelerometer
	boolean first_accelerometer = true;
	boolean flag = true;
	float[] values;
	long lastTime_acc = 0;
	long currentTime_acc = 0;
	double x1,y1,z1,x2,y2,z2,x3,y3,z3,length1,length2,length3;
	int steps = 0;
	int countNewSteps = 0;
	
	public Pedometer() {
		super();
		for (int i = 0; i < window.length; i++) {
			window[i] = 0;
		}
	}

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		if ("getSteps".equals(action)) {
			JSONObject steps = new JSONObject();
			steps.put("steps", getCountSteps());
			callbackContext.success(steps);
		} else if ("clearSteps".equals(action)) {
			setCountSteps(0);
			callbackContext.success();
		} else if ("getDegrees".equals(action)) {
			getDegreesCallBack = callbackContext;
		} else if ("getStepLength".equals(action)) {
			JSONObject obj = new JSONObject();
			obj.put("stepLength", getStepLength());
			callbackContext.success(obj);
		}
		return true;
	}
	
	private class AccelerometerListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (first_accelerometer){	
		 		x1 = event.values[SensorManager.DATA_X];
		 		y1 = event.values[SensorManager.DATA_Y];
		 		z1 = event.values[SensorManager.DATA_Z];
		 		x2 = event.values[SensorManager.DATA_X];
		 		y2 = event.values[SensorManager.DATA_Y];
		 		z2 = event.values[SensorManager.DATA_Z];
		 		x3 = event.values[SensorManager.DATA_X];
		 		y3 = event.values[SensorManager.DATA_Y];
		 		z3 = event.values[SensorManager.DATA_Z];
		 		first_accelerometer = false;
		 		stepLength(x1, y1, z1);
	 		}else{
	 			x3 = x2;
	 			y3 = y2;
	 			z3 = z2;
	 			x2 = x1;
	 			y2 = y1;
	 			z2 = z1;
	 			x1=event.values[SensorManager.DATA_X];
	 			y1=event.values[SensorManager.DATA_Y];
	 			z1=event.values[SensorManager.DATA_Z];
	 			length1=(Math.sqrt(x1*x1+y1*y1+z1*z1));
	 			length2=(Math.sqrt(x2*x2+y2*y2+z2*z2));
	 			length3=(Math.sqrt(x3*x3+y3*y3+z3*z3));	 			
	 			
	 			stepLength(x1, y1, z1);
	 			
	 			if(length2 > 10.6){
	 				if(length2 > length3 && length2 > length1){
	 					steps = 1;
	 				}
	 			}
	 			
	 			if(steps ==1 && length2 < 9.7){
	 				if(length2 < length3 && length2 < length1){
	 					if(flag){
	 						currentTime_acc = System.currentTimeMillis();
	 						countSteps++;
	 						steps = 0;
	 						flag = false;
	 					}else{
	 						currentTime_acc = System.currentTimeMillis();
	 						steps = 0;
	 						if(currentTime_acc - lastTime_acc > 200 && currentTime_acc - lastTime_acc < 2000){
	 							countSteps++;
	 						}
	 					}
	 					lastTime_acc = currentTime_acc;
	 				}
	 			}
	 		}
		}
	}

	private class OrientationListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (getDegreesCallBack != null) {
				JSONObject degrees = new JSONObject();
				setDegree(event.values[0]);
				try {
					degrees.put("degrees", event.values[0]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				PluginResult pluginResult = new PluginResult(
						PluginResult.Status.OK, degrees);
				pluginResult.setKeepCallback(true);
				getDegreesCallBack.sendPluginResult(pluginResult);
			}
		}
	}

	public double getStepLength() {
		return stepLength;
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		context = cordova.getActivity();
		accelerometerListener = new AccelerometerListener();
		orientationListener = new OrientationListener();
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor acceleromererSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		@SuppressWarnings("deprecation")
		Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(accelerometerListener,acceleromererSensor, SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(orientationListener, orientationSensor,SensorManager.SENSOR_DELAY_UI);
	}

	public double norm(double x, double y, double z) {
		double res = (x * x) + (y * y) + (z * z);
		return Math.sqrt(res);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(accelerometerListener);
		sensorManager.unregisterListener(orientationListener);
		sensorManager = null;
	}

	public void stepLength(double accX, double accY, double accZ) {
		double x = accX;
		double y = accY;
		double z = accZ;
		double[] gravity = { 0, 0, 0 };
		gravity[0] = 0.8 * gravity[0] + (1 - 0.8) * x;
		gravity[1] = 0.8 * gravity[1] + (1 - 0.8) * y;
		gravity[2] = 0.8 * gravity[2] + (1 - 0.8) * z;
		x = x - gravity[0];
		y = y - gravity[1];
		z = z - gravity[2];
		double norm = norm(x, y, z);
		if (!initialed) {
			normFilter = norm;
		} else {
			normFilter = alpha * norm + (1 - alpha) * normFilter;
		}
		output = normFilter;
		if (!initialed) {
			output = normFilter;
			initialed = true;
		} else {
			output = (16 * output + normFilter) / 17;
		}
		int count = times % 16;
		window[count] = output;
		max = window[0];
		min = window[0];
		for (int i = 1; i < window.length; i++) {
			if (window[i] > max) {
				max = window[i];
			}
			if (window[i] < min) {
				min = window[i];
			}
		}
		stepLength = K * Math.pow((max - min), 0.25);
		times++;
	}

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}
	
	public double sum(double[] source){
		double total = 0.0;
		for(double i:source){
			total += i;
		}
		return total;
	}

	public int getCountSteps() {
		return countSteps;
	}

	public void setCountSteps(int countSteps) {
		this.countSteps = countSteps;
	}
	
	
}
