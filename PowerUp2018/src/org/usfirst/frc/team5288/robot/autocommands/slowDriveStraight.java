package org.usfirst.frc.team5288.robot.autocommands;

import org.usfirst.frc.team5288.robot.Robot;
import org.usfirst.frc.team5288.robot.RobotMap;

import accessories.SpartanPID;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class slowDriveStraight extends Command {

    double error;
	double speed;
    private double startingDistance = 0;
    private double inWantedDistance = 0;
    private long deltaTime = 0;
    private long startTime = 0;
    private long currentTime = 0;
    private SpartanPID PID = new SpartanPID(RobotMap.StraightP, RobotMap.StraightI, RobotMap.StraightD, RobotMap.StraightFF);
    private SpartanPID distancePID = new SpartanPID(RobotMap.DistanceD,RobotMap.DistanceD,RobotMap.DistanceD,RobotMap.DistanceFF);

    public slowDriveStraight(double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drivetrain);
    	inWantedDistance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.resetEncoders();
    	startingDistance = getCurrentDistance();
    	PID.setTarget(0);//PID in error
    	distancePID.setTarget(inWantedDistance);
    	startTime = System.currentTimeMillis();
    	currentTime = startTime;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	currentTime = System.currentTimeMillis();
    	deltaTime = currentTime - startTime;
       	distancePID.setTarget(inWantedDistance);

    	Robot.drivetrain.PIDInput = "" + (getCurrentDistance() - startingDistance);
    	distancePID.update(getCurrentDistance() - startingDistance);
    	PID.update(Robot.drivetrain.getLeftDistanceInches() - Robot.drivetrain.getRightDistanceInches());
    	speed = distancePID.getOutput()*0.5;
    	error = PID.getOutput()*0.5;
    	if(inWantedDistance  -  getCurrentDistance()  >= 36)
    	{
    		speed = 0.5;
    	}
    	Robot.drivetrain.PIDOutput = "DistancePID : " +  distancePID.getOutput();
    	System.out.println("PID INPUT(distance): " +  getCurrentDistance());
    	//Output using the distanceScalar*(Maxspeed + error), effectively scaling down both the speed and the error.
    	Robot.drivetrain.setLPower(-speed- error);
    	Robot.drivetrain.setRPower(-speed+ error);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean rv = false;
    	
    	if(inWantedDistance > 0)
    	{
    		rv = getCurrentDistance() - startingDistance >=  inWantedDistance - .25;
    	}
    	else{
    		rv = getCurrentDistance() - startingDistance <= inWantedDistance + .25;
    	}
    	if( deltaTime >= 8000) {
    		System.out.println("The Command *DriveStraight* cancelled due to timeout.");
    		return true;
    	}
    	if(rv)
    	{

        	Robot.drivetrain.PIDInput = "command done";
    		Robot.drivetrain.setLPower(0);
    		Robot.drivetrain.setRPower(0);
    	}return rv;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.setLPower(0);
    	Robot.drivetrain.setRPower(0);
    }
    
    private double getCurrentDistance() {
        return (Robot.drivetrain.getLeftDistanceInches()+ Robot.drivetrain.getRightDistanceInches()) / 2;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
