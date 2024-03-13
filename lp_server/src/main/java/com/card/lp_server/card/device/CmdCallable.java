package com.card.lp_server.card.device;


import android.util.Log;

import com.card.lp_server.card.device.model.Command;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.model.XferCommand;

import java.util.concurrent.Callable;


class CmdCallable implements Callable<Pair<Boolean, byte[]>> {

    private final Command command;

    private static final String TAG = "CmdCallable";

    public CmdCallable(Command command) {
        this.command = command;
    }

    /**
     * 命令执行方法
     *
     * @return
     */
    @Override
    public Pair<Boolean, byte[]> call() {

        XferCommand xferCommand = new XferCommand();
        xferCommand.setCmd(command);
        Log.d(TAG, "call: command : " + command);
        LPE370Xfer lpe370Xfer = new LPE370Xfer();
        boolean isSuccess = lpe370Xfer.run(xferCommand);
        if (!command.isCmdStates() || !isSuccess) {
            Log.d(TAG, "命令执行失败-->cmdName:" + command.getCmdName() + "---fileName-->" + command.getFileName());
//            return new Pair<Boolean, byte[]>(false, null);
//            return new Pair<Boolean, byte[]>(false, null);
            throw new RuntimeException("命令执行失败-->cmdName:" + command.getCmdName() + "---fileName-->" + command.getFileName());
        } else {
            Log.d(TAG, "命令执行成功-> cmdName:" + command.getCmdName() + "----fileName-->" + command.getFileName() + "  字节数组长度:" + (null != command.getBytes() ? command.getBytes().length : 0));
            return new Pair<Boolean, byte[]>(true, command.getBytes());
        }
    }
}
