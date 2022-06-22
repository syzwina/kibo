package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.graphics.Bitmap;
import android.util.Log;

import gov.nasa.arc.astrobee.Kinematics;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;
import org.opencv.aruco.Aruco;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    private final String TAG = this.getClass().getSimpleName();
    // initializing zones
    private final KeepOutZone KOZ01 = new KeepOutZone(9.8585f, -9.4500f, 4.82063f, 12.0085f, -8.5000f, 4.87063f);
    private final KeepOutZone KOZ02 = new KeepOutZone(9.8673f, -9.18813f, 3.81957f, 10.7673f, -8.28813f, 4.81957f);
    private final KeepOutZone KOZ03 = new KeepOutZone(11.1067f, -9.44819f, 4.87385f, 12.0067f, -8.89819f, 5.87385f);

    private final KeepInZone KIZ01 = new KeepInZone(10.3f, -10.2f, 4.32f, 11.55f, -6.4f, 5.57f);
    private final KeepInZone KIZ02 = new KeepInZone(9.5f, -10.5f, 4.02f, 10.5f, -9.6f, 4.8f);

    private final int LOOP_MAX = 5;


    @Override
    protected void runPlan1(){

        // the mission starts
        api.startMission();
        Log.i(TAG, "start mission!");



        // POINT 1
        Point point = new Point(10.71f, -7.9f, 4.48f);
        Quaternion quaternion = new Quaternion(0f, 0.707f, 0f, 0.707f);
        moveBee(point, quaternion, 1);

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



        // POINT 2 : lower than POINT 1
        point = new Point(10.7f, -7.7f, 5f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        moveBee(point, quaternion, 2);

        // POINT 3 : move forward towards target 2
        point = new Point(10.7f, -9.5f, 5f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        moveBee(point, quaternion, 3);

        // get a camera imageMat
        imageMat = api.getMatNavCam();
        api.saveMatImage(imageMat, "nearTarget2.png");

        // best not to turn lasers on when ydk if bee is pointing to the right thing

        // irradiate the laser
        Log.i(TAG, "turn laser on");
        api.laserControl(true);

        // take target2 snapshots
        api.takeTarget2Snapshot();

        // turn the laser off
        Log.i(TAG, "turn laser off");
        api.laserControl(false);

        // POINT 4 : go back to POINT 2
        point = new Point(10.7f, -7.7f, 5f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        moveBee(point, quaternion, 4);

        // POINT 5 : go back to STARTING POINT
        point = new Point(10.76150f, -6.88490f, 5.31647f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        moveBee(point, quaternion, 5);

        // send mission completion
        api.reportMissionCompletion();



//        Kinematics kinematics =  api.getRobotKinematics();
//        kinematics.
//        Aruco.detectMarkers();

        // get/process a bitmap imageBitMap
        // Bitmap imageBitMap = any_function();
        // save the imageMat
        // api.saveBitmapImage(imageBitMap, “file_name_1”);
        // get/process a mat imageMat
        // imageMat = any_function_mat();
        // save the imageMat
        // api.saveMatImage(img, “file_name_2”);

    }

    private boolean checksForKOZ(Point point){
        float x = (float) point.getX();
        float y = (float) point.getY();
        float z = (float) point.getZ();
        if (KOZ01.contains(x,y,z) || KOZ02.contains(x,y,z) || KOZ03.contains(x,y,x)) return false;
        return true;
    }

    private boolean checksForKIZ(Point point){
        float x = (float) point.getX();
        float y = (float) point.getY();
        float z = (float) point.getZ();
        if (KIZ01.contains(x,y,z) || KIZ02.contains(x,y,z)) return true;
        return false;
    }

    private void moveBee(Point point, Quaternion quaternion, int pointNumber){

        if (checksForKOZ(point)) Log.i(TAG, "point " + pointNumber + " is not in KOZ");
        if (checksForKIZ(point)) Log.i(TAG, "point " + pointNumber + " is in KIZ");
        Log.i(TAG, "move to point " + pointNumber);
        Result result = api.moveTo(point, quaternion, false);

        // check result and loop while moveTo api is not succeeded
        int loopCounter = 0;
        while(!result.hasSucceeded() && loopCounter < LOOP_MAX){
            // retry
            result = api.moveTo(point, quaternion, false);
            ++loopCounter;
        }
        if (result.hasSucceeded()) Log.i(TAG, "successfully moved to point " + pointNumber);
        else Log.i(TAG, "failed to moved to point " + pointNumber);
    }

}

