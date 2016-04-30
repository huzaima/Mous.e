package com.huzaima.mouse;

/**
 * Created by Huzaima Khan on 4/14/2016.
 */
public class KalmanFilter {
    public float q; //process noise covariance
    public float r; //measurement noise covariance
    public float filteredValue; //value
    public float last;
    public float p; //estimation error covariance
    public float k; //kalman gain

    public KalmanFilter(float value) {
        this.q = 0.0625f;
        this.r = 32;
        this.filteredValue = value;
        this.p = 1.3833094f;
        this.k = 0.043228418f;
    }

    public void update(float measurement) {

        p = p + q;
        //measurement update
        k = p / (p + r);
        last = filteredValue;
        filteredValue = filteredValue + k * (measurement - filteredValue);
        p = (1 - k) * p;
    }
}
