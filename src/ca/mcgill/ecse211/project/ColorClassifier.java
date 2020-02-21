package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.SensorMode;

public class ColorClassifier {
  
  public static SensorMode color = frontColorSensor.getRGBMode();;
  public static float[] colorSample = new float[color.sampleSize()];
  
  public enum RingColor {
    GREEN, BLUE, YELLOW, ORANGE, UNKNOWN
  }

  private static final int BLUE_R_MEAN = 0;
  private static final int BLUE_R_STD = 0;
  private static final int GREEN_R_MEAN = 0;
  private static final int GREEN_R_STD = 0;
  private static final int YELLOW_R_MEAN = 0;
  private static final int YELLOW_R_STD = 0;
  private static final int ORANGE_R_MEAN = 0;
  private static final int ORANGE_R_STD = 0;
  
  private static final int DEMO_LIMIT = 5;
  
  public static void colorTest() {
    lcd.clear();
    int objectCount = 0;
    while (true) {

      lcd.clear();
      
      color.fetchSample(colorSample, 0); 
      
      RingColor ringColor = classifyColor();
      if (ringColor != RingColor.UNKNOWN) {
        Sound.playTone(400, 300, 35);
        Sound.playTone(500, 300, 45);
        lcd.drawString("Object Detected", 0, 0);
        objectCount++;
        
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
      }
      
      if (objectCount == DEMO_LIMIT) {
        lcd.drawString("Repeat Demo?", 0, 5);
        lcd.drawString("Left  - Yes", 0, 6);
        lcd.drawString("Right - No", 0, 7);
        int buttonChoice;
        do {
          buttonChoice = Button.waitForAnyPress(); // left or right press
        } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
        if (buttonChoice == Button.ID_LEFT) {
          objectCount = 0;
        } else {
          return;
        }
      }
    }
  }
  
  public float[] getColorSample() {
    color.fetchSample(colorSample, 0);
    return colorSample;
  }
  
  public static RingColor classifyColor() {
    double red = colorSample[0] / (colorSample[0] + colorSample[1] + colorSample[2]);
 //   double green = colorSample[1] / (colorSample[0] + colorSample[1] + colorSample[2]);
 //   double blue = colorSample[2] / (colorSample[0] + colorSample[1] + colorSample[2]);
    
    if (red >= GREEN_R_MEAN - GREEN_R_STD && red <= GREEN_R_MEAN + GREEN_R_STD) {
      return RingColor.GREEN;
    } else if (red >= BLUE_R_MEAN - BLUE_R_STD && red <= BLUE_R_MEAN + BLUE_R_STD) {
      return RingColor.BLUE;
    } else if (red >= YELLOW_R_MEAN - YELLOW_R_STD && red <= YELLOW_R_MEAN + YELLOW_R_STD) {
      return RingColor.YELLOW;
    } else if (red >= ORANGE_R_MEAN - ORANGE_R_STD && red <= ORANGE_R_MEAN + ORANGE_R_STD) {
      return RingColor.ORANGE;
    } else {
      return RingColor.UNKNOWN;
    }
  }
}
