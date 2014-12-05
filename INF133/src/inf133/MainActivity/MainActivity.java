package inf133.MainActivity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    @SuppressLint("NewApi")
	private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        int worldAxisY = SensorManager.AXIS_Y;
        int worldAxisMinusY = SensorManager.AXIS_MINUS_Y;
        int worldAxisMinusX = SensorManager.AXIS_MINUS_X;
        int worldAxisMinusZ = SensorManager.AXIS_MINUS_Z;

        System.out.print("AXIS_X: " + worldAxisX + " AXIS_Z: " + worldAxisZ + " AXIS_Y: " + worldAxisY +
                " AXIS_MINUS_Y: " + worldAxisMinusY + " AXIS_MINUS_X: " + worldAxisMinusX + " AXIS_MINUS_Z: " + worldAxisMinusZ );
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        float yaw = orientation[0] * FROM_RADS_TO_DEGS;
        ((TextView)findViewById(R.id.pitch)).setText("For sitting flat on the face or back: "+pitch);
        ((TextView)findViewById(R.id.roll)).setText("Degrees spinning on the X axis: "+roll);
        ((TextView)findViewById(R.id.yaw)).setText("Degrees turning the phone right or left: "+yaw);
    }

}