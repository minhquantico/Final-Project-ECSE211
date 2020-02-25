package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import ca.mcgill.ecse211.project.Map.Waypoint;
import lejos.hardware.Sound;

public class Navigation implements Runnable {
  
  /**
   * The map that will contain all the waypoints.
   */
  public Map map;
private float[] usData = new float[usSensor.sampleSize()];
  
  /**
   * This method directs the robot to travel to each waypoint in the map.
   * It also relocalizes the robot every second waypoint.
   */
  public void run() {
    usLocalizer.localize();
    LightLocalization.localize();
    
    int counter = 0;
    // Iterate through each waypoint in the map.
    for (Waypoint pt : map.getMap()) {
      // Travel to each waypoint.
      travelTo(pt.getX(), pt.getY());
      while(ColorClassifier.objectDetected) {
    	  Navigation.sleepFor(10000);
    	  ColorClassifier.objectDetected = false;
    	  travelTo(pt.getX(), pt.getY());
      }
      counter++;
      // Relocalize every second waypoint.
      if (counter % 2 == 0) {
       LightLocalization.relocalize();
       usLocalizer.relocalize();
       travelTo(pt.getX(), pt.getY());
      }
    }
    
    // Rotate to 0 degrees.
    turnTo(0);
    
    Sound.beep();
    Sound.beep();
    Sound.beep();
    
    lcd.clear();
    lcd.drawString("Nb. of donuts: " + ColorClassifier.listOfDonuts.size(), 0, 0);
    for(int i = 0; i < ColorClassifier.listOfDonuts.size(); i++) {
    lcd.drawString(ColorClassifier.listOfDonuts.get(i), 0, i + 1);
    }
  }
  
  /**
   * This method is a setter for the map global variable.
   * @param map the map containing waypoints to travel to.
   */
  public void setMap(Map map) {
    this.map = map;
  }
  
  /**
   * Navigate to a specified location on the board. Should be used post-coordinates localization.
   * 
   * @param x horizontal coordinates in tile size.
   * @param y vertical coordinates in tile size.
   */
  public void travelTo(int x, int y) {
    lcd.clear();
    // Compute required changes in x and y.
    double deltaX = TILE_SIZE * x - odometer.getXyt()[0];
    double deltaY = TILE_SIZE * y - odometer.getXyt()[1];
    
    double xi = odometer.getXyt()[0];
    double yi = odometer.getXyt()[1];
    
    double finalTheta = Math.toDegrees(Math.atan2(deltaX, deltaY));
    
    // Turn to required heading.
    turnTo(finalTheta);
    
    // Compute required distance to travel.
   double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    
    // Move forward toward destination.
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);    
    
    //leftMotor.rotate(convertDistance(distance), true);
    //rightMotor.rotate(convertDistance(distance), false);
    
    leftMotor.forward();
    rightMotor.forward();
    double distanceTraveled = 0;
    //lcd.drawString("Is moving: "+leftMotor.isMoving(), 0, 4);
    lcd.clear();
    while((leftMotor.isMoving() || rightMotor.isMoving()) && distanceTraveled < distance) {
    	
    	
        double objectDistance = usLocalizer.getFilteredDistance();
        double deltaXf = odometer.getXyt()[0]-xi;
        double deltaYf = odometer.getXyt()[1]-yi;
        distanceTraveled = Math.sqrt(deltaXf*deltaXf + deltaYf*deltaYf);
        //lcd.drawString("distancet" + distanceTraveled, 0, 1);
        //lcd.drawString("distance " + distance, 0, 2);
    	if(objectDistance <= 3*OBJECT_DETECTION_THRESHOLD) {
    		if(objectDistance <= OBJECT_DETECTION_THRESHOLD) {
            	leftMotor.setSpeed(0);
            	rightMotor.setSpeed(0);
        		ColorClassifier.colorDetection();
        		break;
    		}
//    		else {
//    			leftMotor.setSpeed(FORWARD_SPEED/2);
//    			rightMotor.setSpeed(FORWARD_SPEED/2);
//    		}
    	}
    }
    
    leftMotor.setSpeed(0);
	rightMotor.setSpeed(0);
    
    
  }
  
  /**
   * Rotates the robot to the specified heading. Should be used post-heading localization.
   * 
   * @param absTheta specified heading.
   */
  public void turnTo(double absTheta) {

    // Get current heading.
    double currTheta = odometer.getXyt()[2];

    // Set angle displacement.
    double deltaTheta = absTheta - currTheta;

    // Set motor speeds to rotational speed.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    // Compute the smallest angle to target.
    if (deltaTheta > 180) {
      deltaTheta -= 360;
    } else if (deltaTheta <= -180) {
      deltaTheta += 360;
    }
    
    // Rotate appropriate amount to align with target.
    leftMotor.rotate(convertAngle(deltaTheta), true);
    rightMotor.rotate(-convertAngle(deltaTheta), false);
    
    // Stop the motors.
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0); 
  }
  
  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * 
   * @param angle the input angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public int convertAngle(double angle) {
    return convertDistance(Math.PI * BASE_WIDTH * angle / 360.0);
  }
  
  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance the input distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
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
