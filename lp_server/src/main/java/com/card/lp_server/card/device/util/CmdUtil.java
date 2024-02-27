package com.card.lp_server.card.device.util;


import com.card.lp_server.card.device.model.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaowei
 * 命令集
 */
public class CmdUtil {

    public static final String WRITE = "WRITE";
    public static final String READ = "READ";
    public static final String DATA_NONE = "DATA_NONE";
    public static final Map<String, Command> cmdMap = new HashMap<String, Command>() {
        {
            /** 按文件名向指定文件重新写入数据，即清空文件原有数据重新写入数据，无返回。 */
            put("CMD_WRITE", new Command("WRITE", "writefile", "TEST", 0, 0));
            /** 按文件名向指定文件追加数据，即从文件末尾继续写入数据，无返回。 */
            put("CMD_APPEND", new Command("WRITE", "appendfile", "TEST", 0, 0));
            /** 按文件名读取指定文件内容，读取前可以用findfile指令获取文件大小。 */
            put("CMD_READ", new Command("READ", "readfile", "TEST", 0, 0));
            /** 获取前次通信事务的命令执行结果信息，如果前次通信事务的命令为getstatus，则再向前顺延一次。
             * 1 int 型 小于0是错  等于0或者大于  readfile writefile listfile deletefile 返回值 负数就是错*/
            put("CMD_GET_STATUS", new Command("READ", "getstatus", "readfile", 0, 4));
            /** 获取存储信息，包括文件总数、存储总容量、可用存储容量、碎片值和文件名总长度。*/
            put("CMD_GET_INFO", new Command("READ", "getinfo", "LPE370", 0, 24));
            //put("CMD_GET_SIZE", new Command("NODATA", "getdisksize", "LPE370", 0, 0));
            /** 按文件名查找指定文件并获取文件大小。 */
            put("CMD_FIND_FILE", new Command("READ", "findfile", "LPE370", 0, 4));
            /** 更新显示设备墨水屏，无返回 */
            put("CMD_EINK", new Command("WRITE", "updatedisplay", "EINK", 0, 16800));
            /**
             * 获取所有文件名，所有文件名以字符串形式连续返回，以字符串结束标志（'\0'）识别每个文件名。执行 本指令前，应用getinfo指令获取文件名总长度。 getstatus
             */
            put("CMD_LIST_FILES", new Command("READ", "listfiles", "LPE370", 0, 0));
            /** 格式化存储空间，清空全部文件，无返回。 */
            put("CMD_FORMAT", new Command("NODATA", "formatdisk", "lpcode-lpe370-format", 12345, 54321));
            /** 存储碎片整理，无返回。 */
            put("CMD_DEFRAG", new Command("NODATA", "defrag", "LPE370", 0, 0));
            /** 按文件名删除指定文件，无返回。 */
            put("CMD_DELETE", new Command("NODATA", "deletefile", "LPE370", 12345, 54321));

            /** 测试标签读取速率（命令第一个参数随便写个字符串，第二个参数是测试长度，字符串会重复写入pc文件，直到测试长度） */
            put("CMD_READ_FPS", new Command("NODATA", "get_read_fps", "LPE370", 0, 0));
            /** 测试标签写入速率（需提供一个pc文件）*/
            put("CMD_WRITE_FPS", new Command("NODATA", "write_fps", "LPE370", 0, 0));

            put("CMD_IAP", new Command("NODATA", "iap", "lpcode-lpe370-iap", 0, 0));

            put("CMD_CONNECT", new Command("NODATA", "connect", null, 0, 0));

            put("CMD_GET_VERSION", new Command("READ", "getversion", "LPE370", 0, 8));

        }
    };

    public static final Map<String, String> MODE = new HashMap<String, String>() {
        {
            put("NODATA", "NODATA");
            put("WRITE", "WRITE");
            put("READ", "READ");
        }
    };

    public static final HashMap<String, String> MODE_DIR = new HashMap<String, String>() {
        {
            put("NODATA", "DATA_NONE");
            put("WRITE", "DATA_IN");
            put("READ", "DATA_OUT");
            put("CTL", "DATA_CTL");
        }
    };

    // 数据帧：类型
    public static final Map<String, Integer> TYPE_VALUE = new HashMap<String, Integer>() {
        {
            put("ENQ", 0x05);
            put("SOH", 0x01);
            put("STX", 0x02);
            put("CAN", 0x18);
        }
    };
    public static final Map<Integer, String> VALUE_TYPE = new HashMap<Integer, String>();

    static {
        for (Map.Entry<String, Integer> entry : TYPE_VALUE.entrySet()) {
            VALUE_TYPE.put(entry.getValue(), entry.getKey());
        }
    }

    // 数据帧：数据方向
    public static final Map<String, Integer> DIR_VALUE = new HashMap<String, Integer>() {
        {
            put("DATA_NONE", 0x24);
            put("DATA_IN", 0x0F);
            put("DATA_OUT", 0x0E);
            put("DATA_CTL", 0x11);
        }
    };

    //结束标志
    public static final Map<String, Integer> END_VALUE = new HashMap<String, Integer>() {
        {
            put("ETX", 0x03);
            put("EOT", 0x24);
            put("CTN", 0x00);
        }
    };

    public static final Map<Integer, String> VALUE_DIR = new HashMap<Integer, String>();

    static {
        for (Map.Entry<String, Integer> entry : DIR_VALUE.entrySet()) {
            VALUE_DIR.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 修改SOH包长为1022，修改STX包长为8176（1022*8）
     */
    public static final Map<String, Integer> FRAME_SIZE = new HashMap<String, Integer>() {
        {
            //put("S", 63);
            //put("L", 945);
            put("S", 1022);
            put("L", 8176);

//            put("S", 62);
//            put("L", 62*8);
        }
    };
    public static final int HEAD_SIZE = 8;
    public static final Map<String, Integer> DATA_SIZE = new HashMap<String, Integer>() {
        {
            put("S", FRAME_SIZE.get("S") - HEAD_SIZE);
            put("L", FRAME_SIZE.get("L") - HEAD_SIZE);
        }
    };
    // 数据帧：数据长度
    public static final Map<String, Integer> TYPE_FSIZE = new HashMap<String, Integer>() {
        {
            put("ENQ", FRAME_SIZE.get("S"));
            put("SOH", FRAME_SIZE.get("S"));
            put("CAN", FRAME_SIZE.get("S"));
            put("STX", FRAME_SIZE.get("L"));
        }
    };
    public static final Map<String, Integer> TYPE_DSIZE = new HashMap<String, Integer>() {
        {
            put("ENQ", DATA_SIZE.get("S"));
            put("SOH", DATA_SIZE.get("S"));
            put("CAN", DATA_SIZE.get("S"));
            put("STX", DATA_SIZE.get("L"));
        }
    };

    // 应答帧 10次
    public static final String ACK = "ACK";
    public static final String NAK = "NAK";
    public static final Map<String, Integer> RSP_VALUE = new HashMap<String, Integer>() {
        {
            put("NAK", 0x15);
            put("ACK", 0x06);
            put("BEL", 0x07);
            put("HOLD", 0x19);
        }
    };
    public static final Map<Integer, String> VALUE_RSP = new HashMap<Integer, String>();

    static {
        for (Map.Entry<String, Integer> entry : RSP_VALUE.entrySet()) {
            VALUE_RSP.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 将命令转成byte数组
     *
     * @param command
     * @return
     */
    public static List<Byte> cmdToByte(Command command) {
        List<Byte> cmd = new ArrayList<Byte>();
        if (command.getCmdName() != null) {
            byte[] byteArray = command.getCmdName().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
        }
        cmd.add((byte) 0);
        if (command.getFileName() != null) {
            byte[] byteArray = command.getFileName().getBytes();
            for (byte b : byteArray) {
                cmd.add(b);
            }
            cmd.add((byte) 0);
        }

        byte[] offset = String.valueOf(command.getOffset()).getBytes();
        for (byte b : offset) {
            cmd.add(b);
        }
        cmd.add((byte) 0);

        byte[] length = String.valueOf(command.getLength()).getBytes();
        for (byte b : length) {
            cmd.add(b);
        }
        cmd.add((byte) 0);
        //System.out.println("0.命令原始数据：  cmdName:" + command.getCmdName() + "   fileName:" + command.getFileName() + "   offset:" + command.getOffset() + "   length:" + command.getLength());
        return cmd;
    }
}
