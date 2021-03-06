/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5288.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team5288.robot.autocommandGroups.*;
import org.usfirst.frc.team5288.robot.autocommands.DriveStraightDistance;
import org.usfirst.frc.team5288.robot.autocommands.DriveStraightTime;
import org.usfirst.frc.team5288.robot.autocommands.SpotTurnDegrees;
import org.usfirst.frc.team5288.robot.autocommands.slowDriveStraight;
import org.usfirst.frc.team5288.robot.commands.lift.LiftToHeight;
import org.usfirst.frc.team5288.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team5288.robot.subsystems.IntakeSubsystem;
import org.usfirst.frc.team5288.robot.subsystems.LeftRampSubsystem;
import org.usfirst.frc.team5288.robot.subsystems.LeftRampSubsystem.state;
// import org.usfirst.frc.team5288.robot.commands.ExampleCommand;
// import org.usfirst.frc.team5288.robot.subsystems.ExampleSubsystem;
import org.usfirst.frc.team5288.robot.subsystems.Lift;
import org.usfirst.frc.team5288.robot.subsystems.RightRampSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	// public static final ExampleSubsystem kExampleSubsystem
	//		= new ExampleSubsystem();
	public static OI m_oi;
	public static boolean leftLimitCondition;
	public static boolean rightLimitCondition;
	public static final Lift lift = new Lift();
	public static final IntakeSubsystem intake = new IntakeSubsystem();
	public static final DrivetrainSubsystem drivetrain = new DrivetrainSubsystem();
	public static final RightRampSubsystem rightRamp = new RightRampSubsystem();
	public static final LeftRampSubsystem leftRamp = new LeftRampSubsystem();
	public static String gameData = DriverStation.getInstance().getGameSpecificMessage();
	public static int switchHei = 40;
	public static int scaleHei = 77;
	public static NetworkTable testEntry;
	
	private Integer autoSelected;
	
	Command m_autonomousCommand;
	SendableChooser<Integer> m_chooser = new SendableChooser<>();
	SendableChooser<String> m_autoCommand = new SendableChooser<>();
	String stringvar;
	//SendableChooser<AutoMaker> autoChooser = new SendableChooser<>();
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		gameData = "";
		CameraServer.getInstance().startAutomaticCapture();

		m_chooser.addObject("Spawn Middle to Switch", 0);
		m_chooser.addObject("Spawn Right to Switch", 1);
		m_chooser.addObject("Spawn Right to Scale", 2);
		m_chooser.addObject("Spawn Left to Switch", 3);
		m_chooser.addObject("Spawn Left to Scale", 4);
		m_chooser.addDefault("Drive Straight", 5);
		m_chooser.addObject("Auto Tester",6);
		m_chooser.addObject("DriveStraightIFFSwitchL", 7);
		m_chooser.addObject("DriveStraightIFFSwitchR", 8);
		SmartDashboard.putData("Auto Choice", m_chooser);
		
		
		SmartDashboard.putNumber("Straight P", RobotMap.StraightP);
		SmartDashboard.putNumber("Straight I", RobotMap.StraightI);
		SmartDashboard.putNumber("Straight D", RobotMap.StraightD);
		SmartDashboard.putNumber("Straight FF", RobotMap.StraightFF);
		
		SmartDashboard.putNumber("Distance P", RobotMap.DistanceP);
		SmartDashboard.putNumber("Distance I", RobotMap.DistanceI);
		SmartDashboard.putNumber("Distance D", RobotMap.DistanceD);
		SmartDashboard.putNumber("Distance FF", RobotMap.DistanceFF);
		
		SmartDashboard.putNumber("Lift P", RobotMap.LiftP);
		SmartDashboard.putNumber("Lift I", RobotMap.LiftI);
		SmartDashboard.putNumber("Lift D", RobotMap.LiftD);
		SmartDashboard.putNumber("Lift FF", RobotMap.LiftFF);
		SmartDashboard.putString("Game Data", gameData);
		
	}
	public static String getDashboardValue(String key) {
		return SmartDashboard.getString(key, "null");
	}
	
	public void robotPeriodic() {
		
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		updateSubsystems();
		updateSensors();
	}

	@Override
	public void disabledPeriodic() {
		gameData = "";
		updateSubsystems();
		updateSensors();
		leftLimitCondition = Robot.leftRamp.isLimitChecked();
		rightLimitCondition = Robot.rightRamp.isLimitChecked();
	//	Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	public void updateGameData() {
		while (gameData.equals("")){
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		}
	}
	@Override
	public void autonomousInit() {
		updateGameData();
		leftRamp.setState(LeftRampSubsystem.state.initial);
		rightRamp.setState(RightRampSubsystem.state.initial);

		// m_autonomousCommand = new DriveStraightDistance(10*12);
		autoSelected = m_chooser.getSelected();
//		m_autonomousCommand = new DriveStraightTime(3000);
	switch(autoSelected) {
		case 0:
			if(gameData.charAt(0) == 'L') {
				System.out.println("Auto: Middle to Left switch");
				SmartDashboard.putString("Auto:"," Middle to Left switch");
				m_autonomousCommand = new autoMiddletoLeftSwitch();
			}
			else {
				System.out.println("Auto: Middle to Right switch");
				SmartDashboard.putString("Auto:"," Middle to Right switch");
				m_autonomousCommand = new autoMiddletoRightSwitch();
			}
			
			break;
		case 1:
			if(gameData.charAt(0) == 'L') {
				System.out.println("Auto: Right to Left switch");
				SmartDashboard.putString("Auto:"," Right to Left switch");
				m_autonomousCommand = new autoRightSidetoLeftSwitch();
			}
			else {
				System.out.println("Auto: Right to Right switch");
				SmartDashboard.putString("Auto:"," Right to Right switch");
				m_autonomousCommand = new autoRightSidetoRightSwitch();
			}
			break;
		case 2:
			if(gameData.charAt(1) == 'L') {
				System.out.println("Auto: Right to Left scale");
				SmartDashboard.putString("Auto:"," Right to Left scale");
				m_autonomousCommand = new autoRightSidetoLeftScale();
			}
			else {
				System.out.println("Auto: Right to Right Scale");
//				SmartDashboard.putString("Auto:"," Right to Right Scale");
				m_autonomousCommand = new autoRightSidetoRightScale();
			}
			break;
		case 3:
			if(gameData.charAt(0) == 'L') {
				System.out.println("Auto: Left to Left switch");
				SmartDashboard.putString("Auto:"," Left to Left switch");
				m_autonomousCommand = new autoLeftSidetoLeftSwitch();
			}
			else {
				System.out.println("Auto: Left to Right Switch");
				SmartDashboard.putString("Auto:"," Left to Right Switch");
				m_autonomousCommand = new autoLeftSidetoRightSwitch();
			}
			break;
		case 4:
			if(gameData.charAt(1) == 'L') {
				System.out.println("Auto: Left to Left Scale");
				SmartDashboard.putString("Auto:"," Left to Left Scale");
				m_autonomousCommand = new autoLeftSidetoLeftScale();
			}
			else {
				System.out.println("Auto: Left to Right Scale");
				SmartDashboard.putString("Auto:"," Left to Right Scale");
				m_autonomousCommand = new autoLeftSidetoRightScale();
			}
			break;
		case 5:
			System.out.println("Auto: Drive Straight");
			SmartDashboard.putString("Auto:"," Drive Straight");
			m_autonomousCommand = new DriveStraightDistance(10*12);
			break;
		case 6:
			System.out.println("Auto Tester");
			System.out.println("I love testing autos!");
			m_autonomousCommand = new LiftToHeight(80);
		case 7:
			System.out.println("Auto: DriveStraight Iff switch = L");
			if (gameData.charAt(0) == 'L') {
				m_autonomousCommand = new  DriveStraightTime(3500);
			
			}
		case 8:
			System.out.println("Auto: DriveStraight Iff switch = R");
			if (gameData.charAt(0) == 'R') {
				m_autonomousCommand = new  DriveStraightTime(3500);
			}
		}
		SmartDashboard.putData("Auto Choice", m_chooser);

		/*
		  String autoSelected = SmartDashboard.getString("Auto Selector", "Default"); 
		  switch(autoSelected) { 
		  case "My Auto": autonomousCommand	  = new MyAutoCommand(); 
		  break; 
		  case "Default Auto": default: autonomousCommand = new ExampleCommand();
		  break; }
		*/ 

		// schedule the autonomous command (example)
		
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		updateSensors();
		updateSubsystems();
		updateSmartDashboard();
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		updateSensors();
		updateSubsystems();
		
		
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		updateSensors();
		updateSubsystems();
		updateSmartDashboard();
		//CameraServer.getInstance();
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		updateSensors();
		updateSubsystems();
		Scheduler.getInstance().run();
	}
	
	public void updateSensors(){
		
		leftLimitCondition = Robot.leftRamp.isLimitChecked();
		rightLimitCondition = Robot.rightRamp.isLimitChecked();
		leftRamp.updateSensors();
		rightRamp.updateSensors();
		intake.updateSensors();
		lift.updateSensors();
		drivetrain.updateSensors();
		
	}
	public void updateSubsystems() {
		lift.updateOutputs();
		intake.updateOutputs();
		drivetrain.updateOutputs();
		rightRamp.updateOutputs();
		leftRamp.updateOutputs();	
		
	}
	public static void putDashboardNumber(String key,double number) {
		 SmartDashboard.putNumber(key, number);

	}
	public static double getDashboardNumber(String key) {
		return SmartDashboard.getNumber(key, -10000000);
	}
	public void updateSmartDashboard() {
		SmartDashboard.putNumber("Left distance (inches)", Robot.drivetrain.getLeftDistanceInches());
		SmartDashboard.putNumber("Right distance (inches)", Robot.drivetrain.getRightDistanceInches());
		SmartDashboard.putNumber("distancetravelled", Robot.lift.getEncoderPosition());

		SmartDashboard.putNumber("LeftEncoderDistance",Robot.drivetrain.getLeftDistanceInches() );
		SmartDashboard.putNumber("RightEncoderDistance",Robot.drivetrain.getRightDistanceInches() );
		SmartDashboard.putBoolean("Right ramp limit switch", Robot.rightRamp.isLimitChecked());
		SmartDashboard.putBoolean("Left ramp limit switch", Robot.leftRamp.isLimitChecked());
		SmartDashboard.putNumber("Gyro angle", Robot.drivetrain.getGyroAngle());
		SmartDashboard.putNumber("Lift encoder position", Robot.lift.getEncoderPosition());
		SmartDashboard.putNumber("Lift Height ", Robot.lift.getLiftHeight());
		SmartDashboard.putString("Game Data", gameData);
	}
	public void updatePID() {
		/*RobotMap.DistanceP = SmartDashboard.getNumber("Distance P", 0.0);
		RobotMap.DistanceI = SmartDashboard.getNumber("Distance I", 0.0);
		RobotMap.DistanceD = SmartDashboard.getNumber("Distance D", 0.0);
		RobotMap.DistanceFF = SmartDashboard.getNumber("Distance FF", 0.0);
		
		RobotMap.StraightP = SmartDashboard.getNumber("Straight P", 0.0);
		RobotMap.StraightI = SmartDashboard.getNumber("Straight I", 0.0);
		RobotMap.StraightD = SmartDashboard.getNumber("Straight D", 0.0);
		RobotMap.StraightFF = SmartDashboard.getNumber("Straight FF", 0.0);
	
		RobotMap.LiftP = SmartDashboard.getNumber("Lift P", 0);
		RobotMap.LiftI = SmartDashboard.getNumber("Lift I", 0);
		RobotMap.LiftD = SmartDashboard.getNumber("Lift D", 0);
//		RobotMap.LiftFF = SmartDashboard.getNumber("Lift FF", 0);*/
	}
	
}
