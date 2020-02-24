package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;


import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.SensorMode;

public class ColorClassifier implements Runnable {
  
  public static SensorMode color = frontColorSensor.getRGBMode();;
  public static float[] colorSample = new float[color.sampleSize()];
  
  private static double objectDistance = usLocalizer.getFilteredDistance();
  
  
  public enum RingColor {
    GREEN, BLUE, YELLOW, ORANGE, UNKNOWN
  }

  private static final double BLUE_R_MEAN = 0.03;//0.028267970;
  private static final double BLUE_R_STD = 0.01;//2*0.00081847;
  private static final double GREEN_R_MEAN = 0.055;//0.062581700;
  private static final double GREEN_R_STD = 0.01;//2*0.00143804;
  private static final double YELLOW_R_MEAN = 0.110;//0.09812091683;
  private static final double YELLOW_R_STD = 0.015;//2*0.00265903;
  private static final double ORANGE_R_MEAN = 0.09;//0.116992463;
  private static final double ORANGE_R_STD = 0.01;//2*0.002519;
  private static final double BLUE_B_MEAN = 0.117320263;
  private static final double BLUE_B_STD = 0.00144681;
  private static final double GREEN_B_MEAN =0.0214052293;
  private static final double GREEN_B_STD = 0.0008184477;
  
  private static final int DEMO_LIMIT = 5;
  
  public static void colorTest() {
    lcd.clear();
    int objectCount = 0;
    while (true) {

      //lcd.clear();
      
      color.fetchSample(colorSample, 0);      
      objectDistance = usLocalizer.getFilteredDistance();

      RingColor ringColor = classifyColor();
      if (/*ringColor != RingColor.UNKNOWN &&*/ objectDistance <= OBJECT_DETECTION_THRESHOLD) {
        lcd.clear();
       // Sound.playTone(400, 300, 35);
       Sound.playTone(500, 300, 5);
        lcd.drawString("Object Detected", 0, 0);
        lcd.drawString(""+colorSample[0], 0, 5);
        objectCount++;
        
        switch(ringColor) {
          case GREEN:
            lcd.drawString("GREEN", 0, 1);
            break;
          case BLUE:
            lcd.drawString("BLUE", 0, 1);
            break;
          case ORANGE:
            lcd.drawString("ORANGE", 0, 1);
            break;
          case YELLOW:
            lcd.drawString("YELLOW", 0, 1);
            break;
          case UNKNOWN://check whether can be identified
        	  lcd.drawString("Unknown", 0, 1);
          default:
            break;
          }
          while (objectDistance <= 2*OBJECT_DETECTION_THRESHOLD) {
            objectDistance = usLocalizer.getFilteredDistance();
          }
      }
      
//      if (objectCount == DEMO_LIMIT) {
//        lcd.drawString("Repeat Demo?", 0, 5);
//        lcd.drawString("Left  - Yes", 0, 6);
//        lcd.drawString("Right - No", 0, 7);
//        int buttonChoice;
//        do {
//          buttonChoice = Button.waitForAnyPress(); // left or right press
//        } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
//        if (buttonChoice == Button.ID_LEFT) {
//          objectCount = 0;
//          lcd.clear();
//          //continue;
//        } else {
//          return;
//        }
//      }
    }
  }
  
  public float[] getColorSample() {
    color.fetchSample(colorSample, 0);
    return colorSample;
  }
  
  public static RingColor classifyColor() {
    double red = colorSample[0];
    double blue=colorSample[2];
 //   double green = colorSample[1] / (colorSample[0] + colorSample[1] + colorSample[2]);
 //   double blue = colorSample[2] / (colorSample[0] + colorSample[1] + colorSample[2]);
    if ((red >= GREEN_R_MEAN - GREEN_R_STD && red <= GREEN_R_MEAN + GREEN_R_STD )|| (blue>=GREEN_B_MEAN - GREEN_B_STD && blue<=GREEN_B_MEAN - GREEN_B_STD)) {
      return RingColor.GREEN;
    } 
    else if ((red >= BLUE_R_MEAN - BLUE_R_STD && red <= BLUE_R_MEAN + BLUE_R_STD )|| (blue>=BLUE_B_MEAN - BLUE_B_STD && blue <=BLUE_B_MEAN + BLUE_B_STD)) {
      return RingColor.BLUE;
    } 
    else if (red >= YELLOW_R_MEAN - YELLOW_R_STD && red <= YELLOW_R_MEAN + YELLOW_R_STD) {
      return RingColor.YELLOW;
    } else if (red >= ORANGE_R_MEAN - ORANGE_R_STD && red <= ORANGE_R_MEAN + ORANGE_R_STD) {
      return RingColor.ORANGE;
    } else {
      return RingColor.UNKNOWN;
    }
  }
  
  public void run() {
    objectDistance = usLocalizer.getFilteredDistance();
    
    if (objectDistance <= OBJECT_DETECTION_THRESHOLD) {
      lcd.clear();
      
      color.fetchSample(colorSample, 0);      
      RingColor ringColor = classifyColor();
      
      Sound.playTone(400, 300, 35);
      Sound.playTone(500, 300, 45);
      lcd.drawString("Object Detected", 0, 0);
     // objectCount++;
      
      switch(ringColor) {
        case GREEN:
          lcd.drawString("GREEN", 0, 1);
        case BLUE:
          lcd.drawString("BLUE", 0, 1);
        case YELLOW:
          lcd.drawString("YELLOW", 0, 1);
        case ORANGE:
          lcd.drawString("ORANGE", 0, 1);
        default:
          // Nothing
      }
      //navigation.sleepFor(10000);
    }
  }
  
  /**
   * Sleeps for the specified duration.
   * @param millis the duration in milliseconds
   */
  public static void sleepFor(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // Nothing to do here
    }
  }
}
