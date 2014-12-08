package inf133.MainActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;
    MediaPlayer mp;

    private static final int SENSOR_DELAY = 500 * 5000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;
    private AssetFileDescriptor afd;


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

        mp = new MediaPlayer();
        afd = getApplicationContext().getResources().openRawResourceFd(R.raw.a);

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
                playAudio(afd);
            } else {

                update(event.values);

            }
        }
    }

    synchronized void playAudio(AssetFileDescriptor afd) {

        if (mp.isPlaying()) {

            return;

        } else {

            try {
                mp.reset();
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
                mp.start();
            } catch (IllegalArgumentException e) {
                Log.d("PlayingAudio: ", "" + e + "\n afd:: " + afd.toString());
            } catch (IllegalStateException e) {
                Log.d("PlayingAudio: ", "" + e + "\n afd:: " + afd.toString());
            } catch (IOException e) {
                Log.d("PlayingAudio: ", "" + e + "\n afd:: " + afd.toString());
            }
        }
    }

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
                " AXIS_MINUS_Y: " + worldAxisMinusY + " AXIS_MINUS_X: " + worldAxisMinusX + " AXIS_MINUS_Z: " + worldAxisMinusZ);
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;
        float yaw = orientation[0] * FROM_RADS_TO_DEGS;

        if (pitch < -60.0 && pitch > -90.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.a);
            playAudio(afd);
        } else if (pitch > 60.0 && pitch < 90.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.b);
            playAudio(afd);
        } else if (roll > 75.0 && roll < 105.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.c);
            playAudio(afd);
        } else if (roll < -75.0 && roll > -105.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.d);
            playAudio(afd);
        } else if (roll < 15.0 && roll > -15.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.e);
            playAudio(afd);
        } else if (roll > 165.0 && roll < 190.0) {
            afd = getApplicationContext().getResources().openRawResourceFd(R.raw.f);
            playAudio(afd);
        }

        ((TextView) findViewById(R.id.pitch)).setText("For sitting flat on the face or back: " + pitch);
        ((TextView) findViewById(R.id.roll)).setText("Degrees spinning on the X axis: " + roll);
        ((TextView) findViewById(R.id.yaw)).setText("Degrees turning the phone right or left: " + yaw);
    }

}