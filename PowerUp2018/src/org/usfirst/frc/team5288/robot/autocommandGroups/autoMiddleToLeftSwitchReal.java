package org.usfirst.frc.team5288.robot.autocommandGroups;

import org.usfirst.frc.team5288.robot.Robot;
import org.usfirst.frc.team5288.robot.autocommands.*;
import org.usfirst.frc.team5288.robot.commands.intake.LoadCubeTime;
import org.usfirst.frc.team5288.robot.commands.intake.UnloadCubeTime;
import org.usfirst.frc.team5288.robot.commands.lift.LiftToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class autoMiddleToLeftSwitchReal extends CommandGroup {

    public autoMiddleToLeftSwitchReal() {
    	
    	addSequential(new DriveStraightDistance(10));
    	addSequential(new SpotTurnDegrees(-45));
    	addParallel(new LiftToHeight(Robot.switchHei));
    	addSequential(new DriveStraightDistance(155));
    	addSequential(new SpotTurnDegrees(45));
    	addSequential(new DriveStraightDistance(65));
    	addSequential(new SpotTurnDegrees(90));
    	addSequential(new DriveStraightDistance(47));
    	addSequential(new UnloadCubeTime());
    	addSequential(new DriveStraightDistance(-30));
    	addSequential(new SpotTurnDegrees(-90));
    	addParallel(new LiftToHeight(0));
    	addSequential(new DriveStraightDistance(17));
    	addSequential(new SpotTurnDegrees(90));
    	addParallel(new LoadCubeTime());
    	addSequential(new DriveStraightDistance(33));
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
}
