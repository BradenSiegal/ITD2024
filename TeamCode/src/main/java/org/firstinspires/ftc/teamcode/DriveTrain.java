package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.MotorPulsesCalculator.calculatePulses;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class DriveTrain {
    private DcMotor FrontLM = null;
    private DcMotor FrontRM = null;
    private DcMotor BackLM = null;
    private DcMotor BackRM = null;

    IMU imu;

    private LinearOpMode opmode = null;

    public DriveTrain() {
    }

    public void init(LinearOpMode opMode) {
        HardwareMap hwMap;

        opmode = opMode;
        hwMap = opMode.hardwareMap;

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;

        imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu.initialize(new IMU.Parameters(orientationOnRobot));

        FrontRM = hwMap.dcMotor.get("FrontRM");
        FrontLM = hwMap.dcMotor.get("FrontLM");
        BackRM = hwMap.dcMotor.get("BackRM");
        BackLM = hwMap.dcMotor.get("BackLM");

        FrontLM.setDirection(REVERSE);
        FrontRM.setDirection(REVERSE);
        BackLM.setDirection(REVERSE);
        BackRM.setDirection(FORWARD);

        FrontRM.setPower(0);
        FrontLM.setPower(0);
        BackRM.setPower(0);
        BackLM.setPower(0);
    }

    public void forward(double speed) {
        FrontRM.setPower(speed);
        FrontLM.setPower(speed);
        BackLM.setPower(speed);
        BackRM.setPower(speed);
    }

    public void backward(double speed) {
        FrontRM.setPower(-speed);
        FrontLM.setPower(-speed);
        BackLM.setPower(-speed);
        BackRM.setPower(-speed);
    }

    public void turnLeft(double speed) {
        FrontRM.setPower(speed);
        FrontLM.setPower(-speed);
        BackLM.setPower(-speed);
        BackRM.setPower(speed);
    }

    public void turnRight(double speed) {
        FrontRM.setPower(-speed);
        FrontLM.setPower(speed);
        BackLM.setPower(speed);
        BackRM.setPower(-speed);
    }

    public void strafeRight(double speed) {
        FrontRM.setPower(-speed);
        FrontLM.setPower(speed);
        BackRM.setPower(speed);
        BackLM.setPower(-speed);
    }

    public void strafeLeft(double speed) {
        FrontRM.setPower(speed);
        FrontLM.setPower(-speed);
        BackLM.setPower(speed);
        BackRM.setPower(-speed);
    }
//the ones with distance parameter- telescoping functions

    //
    public void forwardDistance(double speed, int distance) {
        int pulses = calculatePulses(distance);
        BackLM.setTargetPosition(pulses);
        BackRM.setTargetPosition(pulses);
        FrontLM.setTargetPosition(pulses);
        FrontRM.setTargetPosition(pulses);

        prepareEncoders();

        BackLM.setPower(speed);
        BackRM.setPower(speed);
        FrontLM.setPower(speed);
        FrontRM.setPower(speed);

        while (BackLM.isBusy()) {
        }
        stop();
    }

    public void stop() {
        FrontRM.setPower(0);
        FrontLM.setPower(0);
        BackLM.setPower(0);
        BackRM.setPower(0);
    }

    public void prepareEncoders() {
        BackLM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontRM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontLM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontLM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRM.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void turn(double angle, double speed, boolean isLeft) {
        imu.resetYaw();
        Boolean isDone = false;
        while (!isDone) {
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            opmode.telemetry.addData("Yaw", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
            opmode.telemetry.update();
            if (Math.abs(orientation.getYaw(AngleUnit.DEGREES)) < Math.abs(angle)) {
                if (isLeft) {
                    turnLeft(speed);
                }
                if (!isLeft) {
                    turnRight(speed);
                }
            } else {
                imu.resetYaw();
                stop();
                isDone = true;
                break;
            }
        }
    }

    public void turnMath(double angle, double kP) {
        imu.resetYaw();
        Boolean isDone = false;
        double speed=0;
        while (!isDone) {
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            opmode.telemetry.addData("Yaw", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
            opmode.telemetry.update();
            if (Math.abs(orientation.getYaw(AngleUnit.DEGREES)) < Math.abs(angle)) {
                speed = (angle - orientation.getYaw(AngleUnit.DEGREES)) * kP;

                if (angle < 0) {
                    turnLeft(speed);
                }
                if (angle > 0) {
                    turnRight(speed);
                }
            } else {
                imu.resetYaw();
                stop();
                isDone = true;
                break;
            }
        }
    }
}