package com.hzrobot.aoputv.model.event;

/**
 * Created by shijiwei on 2018/1/10.
 *
 * @VERSION 1.0
 */

public class RobotCommand {

    /**
     * 奥普TV action
     */
    /* 相册上一页 */
    public static final int ACTION_PREVIOUS_ONE = 100;
    /* 相册下一页 */
    public static final int ACTION_NEXT_ONE = 101;
    /* 指定图片 */
    public static final int ACTION_SPECIFIC_ONE = 104;
    /* 指定命令 */
    public static final int ACTION_SPECIFIC_COMMAND = 106;


    /* 视频上一部 */
    public static final int ACTION_PREVIOUS_VIDIO = 102;
    /* 视频下一部 */
    public static final int ACTION_NEXT_VIDIO = 103;
    /* 指定视频 */
    public static final int ACTION_SPECIFIC_VIDIO = 105;

    /**
     * 奥普TV segment key
     */
    public static final String KEY_ACTION = "action";
    public static final String KEY_STEP = "step";
    public static final String KEY_COMMAND = "command";

    private int action;
    private int step;

    private String command;

    public RobotCommand() {
    }

    public RobotCommand(int action, int step, String command) {
        this.action = action;
        this.step = step;
        this.command = command;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
