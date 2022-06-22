package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.graphics.Bitmap;
import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void runPlan1(){

        // initializing zones
        KeepOutZone KOZ01 = new KeepOutZone(9.8585f, -9.4500f, 4.82063f, 12.0085f, -8.5000f, 4.87063f);
        KeepOutZone KOZ02 = new KeepOutZone(9.8673f, -9.18813f, 3.81957f, 10.7673f, -8.28813f, 4.81957f);
        KeepOutZone KOZ03 = new KeepOutZone(11.1067f, -9.44819f, 4.87385f, 12.0067f, -8.89819f, 5.87385f);

        KeepInZone KIZ01 = new KeepInZone(10.3f, -10.2f, 4.32f, 11.55f, -6.4f, 5.57f);
        KeepInZone KIZ02 = new KeepInZone(9.5f, -10.5f, 4.02f, 10.5f, -9.6f, 4.8f);

        // the mission starts
        api.startMission();
        Log.i(TAG, "start mission!");




        // attempt to enter KOZ1
        Log.i(TAG, "move to KOZ1");
        Point point = new Point(10f, -9f, 4.84f);
        Quaternion quaternion = new Quaternion(0f, 0.707f, 0f, 0.707f);
        Result result = api.moveTo(point, quaternion, false);

        final int LOOP_MAX = 5;

        // check result and loop while moveTo api is not succeeded
        int loopCounter = 0;
        while(!result.hasSucceeded() && loopCounter < LOOP_MAX){
            // retry
            result = api.moveTo(point, quaternion, false);
            ++loopCounter;
        }
        if (result.hasSucceeded()) Log.i(TAG, "successfully moved to KOZ1");
        else Log.i(TAG, "failed to moved to KOZ1");





        // move to a point
        Log.i(TAG, "move to point 1");
        point = new Point(10.71000f, -7.70000f, 4.48000f);
        quaternion = new Quaternion(0f, 0.707f, 0f, 0.707f);
        result = api.moveTo(point, quaternion, false);


        // check result and loop while moveTo api is not succeeded
        loopCounter = 0;
        while(!result.hasSucceeded() && loopCounter < LOOP_MAX){
            // retry
            result = api.moveTo(point, quaternion, false);
            ++loopCounter;
        }
        if (result.hasSucceeded()) Log.i(TAG, "successfully moved to point 1");
        else Log.i(TAG, "failed to moved to point 1");


        // report point1 arrival
        api.reportPoint1Arrival();

        // get a camera imageMat
        Mat imageMat = api.getMatNavCam();

        // irradiate the laser
        Log.i(TAG, "turn laser on");
        api.laserControl(true);

        // take target1 snapshots
        Log.i(TAG, "take target 1 snapshot");
        api.takeTarget1Snapshot();

        // turn the laser off
        Log.i(TAG, "turn laser off");
        api.laserControl(false);

        // move to a point
        Log.i(TAG, "move to point 2");
        point = new Point(11.27460f, -9.92284f, 5.29881f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        result = api.moveTo(point, quaternion, false);

        // check result and loop while moveTo api is not succeeded
        loopCounter = 0;
        while(!result.hasSucceeded() && loopCounter < LOOP_MAX){
            // retry
            result = api.moveTo(point, quaternion, false);
            ++loopCounter;
        }
        if (result.hasSucceeded()) Log.i(TAG, "successfully moved to point 2");
        else Log.i(TAG, "failed to moved to point 2"    );

        // report point2 arrival
        // api.reportPoint2Arrival();

        // get a camera imageMat
        imageMat = api.getMatNavCam();
        api.saveMatImage(imageMat, "MatNavCam1");

        // irradiate the laser
        Log.i(TAG, "turn laser on");
        api.laserControl(true);

        // take target2 snapshots
        api.takeTarget2Snapshot();

        // turn the laser off
        Log.i(TAG, "turn laser off");
        api.laserControl(false);

        // send mission completion
        api.reportMissionCompletion();



        // get/process a bitmap imageBitMap
        // Bitmap imageBitMap = any_function();
        // save the imageMat
        // api.saveBitmapImage(imageBitMap, “file_name_1”);
        // get/process a mat imageMat
        // imageMat = any_function_mat();
        // save the imageMat
        // api.saveMatImage(img, “file_name_2”);

    }

    // You can write your other plan here, but it’s not run on the web simulator.

    @Override
    protected void runPlan2(){
        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }

    // You can add your method
    private void moveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w){

        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float)qua_x, (float)qua_y,
                                                     (float)qua_z, (float)qua_w);

        api.moveTo(point, quaternion, true);
    }

    private void relativeMoveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w) {

        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y,
                (float) qua_z, (float) qua_w);

        api.relativeMoveTo(point, quaternion, true);
    }

}

