package ca.mcgill.ecse211.project;
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.sensor.SensorMode;

public class LightLocalization {
  //blue room
  public static double BLK_VAL = 0.4;
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

  public static final int error = 15;

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
    
    detection();
    
    odometer.setTheta(0);
    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);
    leftMotor.rotate(Navigation.convertDistance(5.5), true);
    rightMotor.rotate(Navigation.convertDistance(5.5), false);
    navigation.turnTo(90);

    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);
    leftMotor.forward();
    rightMotor.forward();
    
    detection();
    
    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);

    leftMotor.rotate(Navigation.convertDistance(5.5), true);
    rightMotor.rotate(Navigation.convertDistance(5.5), false);
    navigation.turnTo(0);
    odometer.setTheta(0);
    odometer.setX(TILE_SIZE);
    odometer.setX(TILE_SIZE);
  }

  /*
   * Method to detect the black lines on the board
   */
  public static void detection() {
    boolean leftMotorDetected = false;
    boolean rightMotorDetected = false;
    boolean inReverse = false;
    
    double xi = odometer.getXyt()[0];
    double yi = odometer.getXyt()[1];
   
    while(leftMotor.isMoving() || rightMotor.isMoving()) {
      double xf = odometer.getXyt()[0];
      double yf = odometer.getXyt()[1];
      
      double distanceTraveled = Math.sqrt(Math.pow((xf-xi),2) + Math.pow((yf-yi),2));
      if(distanceTraveled >= TILE_SIZE/2) {
        if(leftMotor.isMoving() && rightMotor.isMoving()) {
          leftMotor.setSpeed(FORWARD_SPEED/2);
          rightMotor.setSpeed(FORWARD_SPEED/2);
          leftMotor.backward();
          rightMotor.backward();
          inReverse = true;
        }
      }
      color.fetchSample(colorData, 0);
      float sensorColor1 = colorData[0];

      color2.fetchSample(colorData2, 0);
      float sensorColor2 = colorData2[0];

      if((sensorColor1 <= BLK_VAL && rightMotorDetected) || (sensorColor2 <= BLK_VAL && leftMotorDetected)
          || sensorColor1 <= BLK_VAL && sensorColor2 <= BLK_VAL) {
        lcd.drawString("Both detected", 0, 6);
        if(isPerpendicular(odometer.getXyt()[2])) {
          lcd.drawString("isPerpendicular", 0, 5);
          leftMotor.setSpeed(0);
          rightMotor.setSpeed(0);
        }
      }
      
      if(sensorColor1 <= BLK_VAL && leftMotor.isMoving()/* && sensorColor2 > BLK_VAL*/) {
        leftMotorDetected = true;
        leftMotor.setSpeed(0);
        lcd.drawString("LeftMotorDetected", 0, 3);
        if(!rightMotorDetected) {
          rightMotor.setSpeed(ROTATE_SPEED/3);
          if(inReverse) {
            rightMotor.backward();
          } else {
            rightMotor.forward();
          }
        }
      }
      
      if(sensorColor2 <= BLK_VAL && rightMotor.isMoving()/* && sensorColor1 > BLK_VAL*/) {
        rightMotorDetected = true;
        rightMotor.setSpeed(0);
        lcd.drawString("RightMotorDetected", 0, 4);
        if(!leftMotorDetected) {
          leftMotor.setSpeed(ROTATE_SPEED/3);
          if(inReverse) {
            leftMotor.backward();
          } else {
            leftMotor.forward();
          }
        }
      }
    }
  }

  /*
   * Method to orient accordingly to (1,l) on the board
   */
  public static void relocalize() {
    
    double theta = odometer.getXyt()[2];
    if(theta >= 315 && theta < 45) {
      navigation.turnTo(0);
    }else if(theta >= 45 && theta < 135) {
      navigation.turnTo(90);
    }else if(theta >= 135 && theta < 225) {
      navigation.turnTo(180);
    }else if(theta >= 225 && theta < 315) {
      navigation.turnTo(270);
    }
    
    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);
    leftMotor.forward();
    rightMotor.forward();
    
    detection();
    
    leftMotor.setSpeed(FORWARD_SPEED/2);
    rightMotor.setSpeed(FORWARD_SPEED/2);

    leftMotor.rotate(Navigation.convertDistance(5.5), true);
    rightMotor.rotate(Navigation.convertDistance(5.5), false);
    
    theta = odometer.getXyt()[2];
    if(theta > 90 - error && theta < 90 + error) {
      odometer.setTheta(90);
    }else if(theta > 180 - error && theta < 180 + error) {
      odometer.setTheta(180);
    }else if(theta > 270 - error && theta < 270 + error) {
      odometer.setTheta(270);
    }else if(theta > 360 - error && theta < 0 + error) {
      odometer.setTheta(0);
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

