package ca.mcgill.ecse211.project;
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.sensor.SensorMode;

public class LightLocalization {
  //blue room
  public static double BLK_VAL = 0.20;
  public static double TILE_VAL = 0.48;

  //normal room
  public static double BLK_VAL2 = 0.3;
  public static double TILE_VAL2 = 0.58;

  // Array to store the color values
  public static SensorMode color = sideColorSensor1.getRedMode();;
  public static float colorData[] = new float[color.sampleSize()];

  public static SensorMode color2 = sideColorSensor2.getRedMode();;
  public static float colorData2[] = new float[color2.sampleSize()];


  public static float color_indicator; // Colors indicated by a certain value from the sensor
  public static float color_indicator2;
  
  public static final int error = 10;

  public LightLocalization() {
  }

  public static float get_colorVal() { // Returns the color value to be used for detection
    return color_indicator;
  }

  public static float get_colorVal2() { // Returns the color value to be used for detection
    return color_indicator2;
  }
  
  public static void localize() {
    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);
    leftMotor.forward();
    rightMotor.forward();
    while(leftMotor.isMoving() || rightMotor.isMoving()) {
      color.fetchSample(colorData, 0);
      float sensorColor1 = colorData[0];

      color2.fetchSample(colorData2, 0);
      float sensorColor2 = colorData2[0];
       
      if(sensorColor1 <= BLK_VAL && sensorColor2 <= BLK_VAL) {
        leftMotor.stop();
        rightMotor.stop();
      } else if((sensorColor1 <= BLK_VAL)) {
        leftMotor.stop();
        rightMotor.stop();
        rightMotor.setSpeed(ROTATE_SPEED/2);
        rightMotor.forward();
      } else if((sensorColor2 <= BLK_VAL)) {
        rightMotor.stop();
        leftMotor.stop();
        leftMotor.setSpeed(ROTATE_SPEED/2);
        leftMotor.forward();
      }
    }
    odometer.setTheta(0);
    navigation.turnTo(90);
    
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    leftMotor.forward();
    rightMotor.forward();
    while(leftMotor.isMoving() || rightMotor.isMoving()) {
      color.fetchSample(colorData, 0);
      float sensorColor1 = colorData[0];

      color2.fetchSample(colorData2, 0);
      float sensorColor2 = colorData2[0];
       
      if(sensorColor1 <= BLK_VAL && sensorColor2 <= BLK_VAL) {
        leftMotor.stop();
        rightMotor.stop();
      } else if((sensorColor1 <= BLK_VAL)) {
        leftMotor.stop(); 
      } else if((sensorColor2 <= BLK_VAL))
        rightMotor.stop();
    }
    navigation.turnTo(0);
    odometer.setTheta(0);
  }

  /*
   * Method to detect the black lines on the board
   */
  public static void detection() {
    (new Thread() {
      public void run() {
        while (true) {
          color.fetchSample(colorData, 0);
          float sensorColor1 = colorData[0];
          color_indicator = sensorColor1;
          // System.out.println(color_indicator);

          color2.fetchSample(colorData2, 0);
          float sensorColor2 = colorData2[0];
          color_indicator2 = sensorColor2;
          // System.out.println(color_indicator2);

          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
          }
        }
      }
    }).start();

  }

  /*
   * Method to orient accordingly to (1,l) on the board
   */
  public static void orientation() {
    
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    while(true) {

      color.fetchSample(colorData, 0);
      float sensorColor1 = colorData[0];
      // color_indicator = sensorColor1;
      // System.out.println(color_indicator);

      color2.fetchSample(colorData2, 0);
      float sensorColor2 = colorData2[0];
      // color_indicator2 = sensorColor2;
      // System.out.println(color_indicator2);
       
      if(sensorColor1 <= BLK_VAL && sensorColor2 <= BLK_VAL) {
        if (isPerpendicular(odometer.getXyt()[2])) {
        leftMotor.stop();
        rightMotor.stop();
        break;
        }
      } else if((sensorColor1 <= BLK_VAL)) {
        leftMotor.stop(); 
      } else if((sensorColor2 <= BLK_VAL))
        rightMotor.stop();

      /*if ((LightLocalization.get_colorVal() > BLK_VAL) && (LightLocalization.get_colorVal2() > BLK_VAL))// Maintain fwd motion til it reaches line
      {
        leftMotor.setSpeed(100);
        rightMotor.setSpeed(100);
        leftMotor.forward();
        rightMotor.backward();          
      }*/

      /*if(LightLocalization.get_colorVal() <= BLK_VAL && LightLocalization.get_colorVal2() <= BLK_VAL) {
      leftMotor.stop();
      rightMotor.stop();
      }*/

    }

  }
  
  private static boolean isPerpendicular(double theta) {
    if((theta > 90 - error && theta < 90 + error) || (theta > 180 - error && theta < 180 + error)
        || (theta > 270 - error && theta < 270 + error) || (theta > 360 - error && theta < 0 + error)) {
      return true;
    }
    return false;
  }

  /*
   * End of LightSensor Localization class
   */
}

