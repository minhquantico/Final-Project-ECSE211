package ca.mcgill.ecse211.project;
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.sensor.SensorMode;


public class LightLocalization {
  //blue room
  public static double BLK_VAL = 0.2;
  public static double TILE_VAL = 0.48;
  
  //normal room
  public static double BLK_VAL2 = 0.3;
  public static double TILE_VAL2 = 0.58;
  
  //COLOR ID
  public static int BLK_VAL_ID = 13;   // The corresponding value for black for the light sensor
  
  // Array to store the color values
  public static SensorMode color = sideColorSensor1.getRedMode();;
  public static float colorData[] = new float[color.sampleSize()];
  
  public static SensorMode color2 = sideColorSensor2.getRedMode();;
  public static float colorData2[] = new float[color2.sampleSize()];
  
  
  public static float color_indicator; // Colors indicated by a certain value from the sensor
  public static float color_indicator2;
  public static float color_indicator3;
  
  public LightLocalization() {
  }
  
  public static float get_colorVal() { // Returns the color value to be used for detection
    return color_indicator;
  }
  
  public static float get_colorVal2() { // Returns the color value to be used for detection
	return color_indicator2;
  }
  
  public static float get_colorVal3() { // Returns the color value to be used for detection
	return color_indicator3;
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
          System.out.println(color_indicator);
          
          color2.fetchSample(colorData2, 0);
          float sensorColor2 = colorData2[0];
          color_indicator2 = sensorColor2;
          System.out.println(color_indicator2);
        
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
    (new Thread() {
      public void run() {
    	while(true) {
    		
    	  if((LightLocalization.get_colorVal() <= BLK_VAL)) 
    		  leftMotor.stop(); 
    	  
    	
    	  if((LightLocalization.get_colorVal2() <= BLK_VAL))
    		  rightMotor.stop();
    	  
    	  if(LightLocalization.get_colorVal() <= BLK_VAL && LightLocalization.get_colorVal2() <= BLK_VAL) {
          leftMotor.stop();
          rightMotor.stop();
          break;
          }
    	
          if ((LightLocalization.get_colorVal() > BLK_VAL) && (LightLocalization.get_colorVal2() > BLK_VAL))// Maintain fwd motion til it reaches line
          {
            leftMotor.setSpeed(100);
            rightMotor.setSpeed(100);
            leftMotor.forward();
            rightMotor.backward();          
          }
        
          /*if(LightLocalization.get_colorVal() <= BLK_VAL && LightLocalization.get_colorVal2() <= BLK_VAL) {
          leftMotor.stop();
          rightMotor.stop();
          }*/
        
    	}
      }
    }).start();
    
  }
  
  /*
   * End of LightSensor Localization class
   */
}

