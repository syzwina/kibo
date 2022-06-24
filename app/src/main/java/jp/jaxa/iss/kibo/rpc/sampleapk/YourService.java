package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.graphics.Bitmap;
import android.util.Log;

import gov.nasa.arc.astrobee.Kinematics;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.aruco.Aruco;

import java.util.ArrayList;
import java.util.List;

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
        Mat imageMatTarget1 = api.getMatNavCam();
        api.saveMatImage(imageMatTarget1, "nearTarget1.png");

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
        Mat imageMatTarget2 = api.getMatNavCam();
        api.saveMatImage(imageMatTarget2, "nearTarget2.png");

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

        // POINT 5 : go to GOAL / CREW
        point = new Point(11.27460f, -7.89178f, 4.96538f);
        quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        moveBee(point, quaternion, 5);



        Log.i(TAG, "make dictionary");
        // Dictionary dictionary = Dictionary.create(6,6);
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat();



        // TARGET 1 image processing
        Log.i(TAG, "TARGET 1 image processing");
        // Aruco.detectMarkers(Mat image, Dictionary dictionary, List<Mat> corners, Mat ids)
        Aruco.detectMarkers(imageMatTarget1, dictionary, corners, ids);
        Aruco.drawDetectedMarkers(imageMatTarget1, (List<Mat>) dictionary);

        api.saveMatImage(imageMatTarget1, "processedNearTarget1.png");

        // saving images
        for (int i=0;i<corners.size();i++) {
            api.saveMatImage(corners.get(i), "corners" + i + ".png");
        }
        if (ids.width()!=0 && ids.height()!=0) {
            api.saveMatImage(ids, "ids.png");
            Log.i(TAG, "saved ids image");
        }



        // TARGET 2 image processing
        Log.i(TAG, "TARGET 2 image processing");
        // Aruco.detectMarkers(Mat image, Dictionary dictionary, List<Mat> corners, Mat ids)
        Aruco.detectMarkers(imageMatTarget2, dictionary, corners, ids);
        Aruco.drawDetectedMarkers(imageMatTarget2, (List<Mat>) dictionary);

        api.saveMatImage(imageMatTarget2, "processedNearTarget2.png");

        // saving images
        for (int i=0;i<corners.size();i++) {
            api.saveMatImage(corners.get(i), "corners" + i + ".png");
            Log.i(TAG, "saved corner image");
        }
        if (ids.width()!=0 && ids.height()!=0) {
            api.saveMatImage(ids, "ids.png");
            Log.i(TAG, "saved ids image");
        }

//        Kinematics kinematics =  api.getRobotKinematics();

        // send mission completion
        api.reportMissionCompletion();
        Log.i(TAG, "reported mission completion");

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

        if (checksForKOZ(point)) Log.i(TAG, "point " + pointNumber + " is NOT in KOZ");
        else Log.e(TAG, "point " + pointNumber + " is in KOZ");
        if (checksForKIZ(point)) Log.i(TAG, "point " + pointNumber + " is in KIZ");
        else Log.e(TAG, "point " + pointNumber + " is NOT in KIZ");
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
        else Log.e(TAG, "failed to move to point " + pointNumber);
    }

}

