package jp.jaxa.iss.kibo.rpc.sampleapk;

abstract class Zone {
    protected float x_min;
    protected float y_min;
    protected float z_min;
    protected float x_max;
    protected float y_max;
    protected float z_max;

    public Zone(float x_min, float y_min, float z_min, float x_max, float y_max, float z_max){
        this.x_min = x_min;
        this.y_min = y_min;
        this.z_min = z_min;
        this.x_max = x_max;
        this.y_max = y_max;
        this.z_max = z_max;
    }

    float r1 =
    // r2 =
    // l1 =
    // l2 =
    // t1 =
    // t2 =
    // b1 =
    // b2 =

    // calculate r1 ... b2 with inputs
    //

    public boolean checkPoint(){
        //corner collision check
        if(r1>l2 && t2>b1 && t1>t2 && b1>b2){
            System.out.println("Corner Collision check 1");
            return true;
        }
        if(r2>l1 && t2>b1 && t1>t2 && b1>b2){
            System.out.println("Corner Collision check 2");
            return true;
        }
        if(r2>l1 && t1>b2 && t2>t1 && b2>b1){
            System.out.println("Corner Collision check 3");
            return true;

        }
        if(r1>l2 && t1>b2 && t2>t1 && b2>b1){
            System.out.println("Corner Collision check 4");
            return true;
        }

        //middle collision check
        if(l1>l2 && r1<r2 && t1<t2 && b1<b2){
            System.out.println("middle collision check 1");
            return true;
        }
        if(l1>l2 && r1<r2 && t1>t2 && b1>b2){
            System.out.println("middle Collision check 2");
            return true;
        }
        if(l1<l2 && r1<r2 && t1<t2 && b1>b2){
            System.out.println("middle Collision check 3");
            return true;
        }
        if(l1>l2 && r1>r2 && t1<t2 && b1>b2){
            return true;
        }

        return false;
    }
}
