package com.card.lp_server.card.device;

import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;
import com.card.lp_server.card.HIDCommunicationUtil;
import com.card.lp_server.card.device.call.VendorDevice;
import com.card.lp_server.card.device.model.Command;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.CmdUtil;
import com.card.lp_server.card.device.util.DateTimeUtils;
import com.card.lp_server.card.device.util.HidUtils;
import com.card.lp_server.card.device.util.ThreadSleepUtil;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 龙贝设备入口
 *
 * @author SEO-Dev
 */
public class LonbestCard extends VendorDevice {
    private LonbestCard() {
    }

    private static class SingletonHelper {
        private static final LonbestCard INSTANCE = new LonbestCard();
    }

    public static LonbestCard getInstance() {
        return SingletonHelper.INSTANCE;
    }

    ExecutorService mExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private static boolean isOpen = false;
    //一次传输最大数据64K（原63K）1024 * 63
    private static final int PACKAGE_SIZE = (65488);//1024 * 63;

    /**
     * 获取唯一ID
     */
    @Override
    public String identity() {
        return "<span style='color:red;font-size:36px'>龙贝电子履历标签</span>";
    }

    @Override
    public Pair<Boolean, String> connect() {
        Pair<Boolean, String> connect = HidUtils.connect(6790, 58409);
        isOpen = connect.getFirst();
        return connect;
    }
    private boolean checkConnect() {
        return HIDCommunicationUtil.Companion.getInstance().setDevice(6790, 58409).findAndOpenHIDDevice();
    }
    public void close() {
        HIDCommunicationUtil.Companion.getInstance().closeUSBConnection();
    }

    private static final String TAG = "LonbestCard";

    /**
     * 根据文件名称读取文件内容。
     *
     * @param name 文件名
     * @return byte[] 文件内容字节数组,没有数据返回null。
     */
//    @DevService("<p>读文件，可传递1个参数</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public byte[] readFile(String name) throws Exception {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");
        if (name == null) {
            return null;
        }
        if (!getConnectStatus()) return null;
        Log.d(TAG, "readFile: " + "readFile: 读文件数据开始,请耐心等待");
        Command findFile = CmdUtil.cmdMap.get("CMD_FIND_FILE");
        assert findFile != null;
        findFile.setFileName(name);
        findFile.clearBytes();
        findFile.setLength(4);
        Pair<Boolean, byte[]> res = new CmdCallable(findFile).call();
        if (res.getFirst()) {
            System.out.println("length:" + ByteUtil.arrayToInt(res.getSecond()));
            if (null == res.getSecond() || ByteUtil.arrayToInt(res.getSecond()) == -1) {
                Log.d(TAG, "readFile: " + "没有找到对应的文件数据-->fileName:" + findFile.getFileName());
                return null;
            }
            //切分长度
            int length = ByteUtil.arrayToInt(res.getSecond());
            Pair<Boolean, byte[]> readRes;
            //多
            /*if (length > PACKAGE_SIZE) {
                List<Integer> detailLen = ByteUtil.parseLength(length, PACKAGE_SIZE);
                List<byte[]> detailData = new ArrayList<>();
                int offset = 0;
                for (int i = 0; i < detailLen.size(); i++) {
                    if (i != 0) {
                        offset += detailLen.get(i - 1);
                    }
                    readRes = signReadFile(name, offset, detailLen.get(i));
                    if (readRes.getFirst()) {
                        detailData.add(readRes.getSecond());
                    } else {
                        LoggerUtil.warning(logger, "读取文件失败-->readFile:" + findFile.getFileName());
                        return null;
                    }
                }
                return ByteUtil.concatenateByteArrays(detailData);
            } else {*/
            //单
            readRes = signReadFile(name, 0, length);
            if (readRes.getFirst()) {
                return readRes.getSecond();
            } else {
                Log.d(TAG, "readFile: " + "读取文件失败-->readFile:" + findFile.getFileName());
                return null;
            }
            //}
        }
        return null;
    }


    //    @DevService("<p>根据文件名称写入文件内容。</p><ul><li>文件名称</li><li>文件内容</li></ul><b>成功则返回True；否则返回False。</b>")
    public boolean writeFile(String name, byte[] data) throws Exception {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");
        if (name == null || null == data || data.length == 0) {
            return false;
        }
        if (!getConnectStatus()) return false;
        Log.d(TAG, "writeFile: " + "writeFile: 写文件数据开始,请耐心等待。");
        //切片
        Pair<Boolean, byte[]> result;
        //if (data.length > PACKAGE_SIZE) {
        //多 假定所有操作成功
            /*List<byte[]> dataList = ByteUtil.splitTitleData(data, PACKAGE_SIZE, PACKAGE_SIZE);
            for (int i = 0; i < dataList.size(); i++) {
                result = signWriteFile(name, dataList.get(i), i == 0 ? "CMD_WRITE" : "CMD_APPEND");
                boolean isSuccess = result.getFirst();
                if (!isSuccess) {
                    return false;
                }
            }
            return true;*/
        //} else {
        //单
        result = signWriteFile(name, data, "CMD_WRITE");
        return result.getFirst();
        //}
    }


    /**
     * @param name
     * @param data
     * @param cmd
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Pair<Boolean, byte[]> signWriteFile(String name, byte[] data, String cmd) throws InterruptedException, ExecutionException {
        Command command = CmdUtil.cmdMap.get(cmd);
        assert command != null;
        command.clearBytes();
        command.setFileName(name);
        command.setBytes(data);
        command.setLength(data.length);
        //如果执行不成功那么这个第二次写入有没有存在的必要我认为是有的哈
        Pair<Boolean, byte[]> result = mExecutor.submit(new CmdCallable(command)).get();
        //容错执行逻辑
        if (!result.getFirst()) {
            result = mExecutor.submit(new CmdCallable(command)).get();
            if (!result.getFirst()) {
                Log.d(TAG, "signWriteFile: " + "写入失败，请尝试重新插拔存储设备后再次尝试写入。", new LonbestException("据写入失败，请尝试重新插拔存储设备后再次尝试写入。"));
                return new Pair<>(false, null);
            }
            Log.d(TAG, "signWriteFile: " + "write operation success");
        }
        result.setFirst(getStatus(command.getCmdName()));
        return result;
    }


    /**
     * 文件大小不大于414900字节时可以直接写入
     *
     * @param name
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Pair<Boolean, byte[]> signReadFile(String name, int offset, int length) throws InterruptedException, ExecutionException {
        Command readCommand = CmdUtil.cmdMap.get("CMD_READ");
        assert readCommand != null;
        readCommand.setOffset(offset);
        readCommand.setLength(length);
        readCommand.setFileName(name);
        readCommand.clearBytes();
        Pair<Boolean, byte[]> result = mExecutor.submit(new CmdCallable(readCommand)).get();
        //容错执行逻辑
        if (!result.getFirst()) {
            result = mExecutor.submit(new CmdCallable(readCommand)).get();
            if (!result.getFirst()) {
                return new Pair<>(false, null);
            }
        }
        //获取通信状态值
        boolean start = getStatus(readCommand.getCmdName());
        return new Pair<>(start, result.getSecond());
    }


    /**
     * 根据图片字节更新墨水屏显示。
     *
     * @param bytes 图片字节
     * @return 更新墨水屏显示成功，则返回True；否则返回False。
     */
//    @DevService("<p>更新墨水屏显示，可传递1个参数</p><ul><li>字节数组，16800字节，每一位表示墨水屏上的一个点</li></ul><b>成功则返回True；否则返回False。</b>")
    public boolean updateEInk(byte[] bytes) throws Exception {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (bytes == null || bytes.length == 0) {
            return false;
        }


        Log.d(TAG, "updateEInk: isOpen " + isOpen);
        if (!getConnectStatus()) return false;
        Command command = CmdUtil.cmdMap.get("CMD_EINK");
        command.clearBytes();
        command.setBytes(bytes);
        Pair<Boolean, byte[]> result = mExecutor.submit(new CmdCallable(command)).get();
        //执行容错
        if (!result.getFirst()) {
            result = mExecutor.submit(new CmdCallable(command)).get();
            if (!result.getFirst()) {
                return false;
            }
        }
        boolean status = getStatus(command.getCmdName());
        Log.d(TAG, "updateEInk: " + "updateEInk: 是否刷屏成功--->" + result.getFirst());
        return status;
    }


    /**
     * 读取前次通信执行结果信息。
     * 其实也是read哈 读状态呢
     *
     * @return boolean 执行结果是否成功，则返回True；否则返回False。
     */
    private static final int MAX_SIZE = 10;

    //    @DevService("<p>读取前次通信执行结果信息。</p><b>执行结果是否成功，则返回True；否则返回False。</b>")
    public boolean getStatus(String cmdName) throws ExecutionException, InterruptedException {
        // 检查输入参数是否为空
        if (cmdName.isEmpty()) {
            return false;
        }
        if (!getConnectStatus())

            return false;
        // 创建一个命令对象
        Command command = CmdUtil.cmdMap.get("CMD_GET_STATUS");
        assert command != null;
        command.clearBytes();
        command.setOffset(0);
        command.setLength(4);
        command.setFileName(cmdName);
        // 循环尝试最多10次
//        for (int attempt = 0; attempt < MAX_SIZE; attempt++) {
        // 提交命令任务到 ExecutorService 并等待结果
        Pair<Boolean, byte[]> result = mExecutor.submit(new CmdCallable(command)).get();
        // 如果结果为true，表示成功，直接返回true
        return result.getFirst();
        // 如果结果不为true，继续下一次尝试
//        }
        // 执行了10次都没有得到true，返回false
    }


    private boolean getConnectStatus() throws ExecutionException, InterruptedException {
        // 创建一个命令对象
        Command command = CmdUtil.cmdMap.get("CMD_CONNECT");
        final String currentYear = DateTimeUtils.getCurrentYear();
        final String currentMonth = DateTimeUtils.getCurrentMonth();
        final String currentDay = DateTimeUtils.getCurrentDay();
        final String currentHour = DateTimeUtils.getCurrentHour();
        final String currentMinute = DateTimeUtils.getCurrentMinute();
        final String currentSecond = DateTimeUtils.getCurrentSecond();
        assert command != null;
        command.setYear(currentYear);
        command.setMonth(currentMonth);
        command.setDay(currentDay);
        command.setHour(currentHour);
        command.setMin(currentMinute);
        command.setSecond(currentSecond);

        Log.d(TAG, "getConnectStatus: command:" + command);
        // 循环尝试最多10次
        for (int attempt = 0; attempt < MAX_SIZE; attempt++) {
            // 提交命令任务到 ExecutorService 并等待结果
            Pair<Boolean, byte[]> result = mExecutor.submit(new CmdCallable(command)).get();
            // 如果结果为true，表示成功，直接返回true
            if (result.getFirst()) {
                Log.d(TAG, "getConnectStatus: 连接状态" + result.getFirst());
                return true;
            }
        }
        // 执行了10次都没有得到true，返回false
        return false;
    }

    public String getVersion() throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        // 创建一个命令对象
        Command command = CmdUtil.cmdMap.get("CMD_GET_VERSION");
        assert command != null;
        // 循环尝试最多10次
        Pair<Boolean, byte[]> version = mExecutor.submit(new CmdCallable(command)).get();
        // 如果结果为true，表示成功，直接返回true
//        boolean status = getStatus(command.getCmdName());
        if (version.getFirst() && version.getSecond() != null && version.getSecond().length > 0) {
            Log.d(TAG, "getVersion:" + ConvertUtils.bytes2String(version.getSecond()));
            return ConvertUtils.bytes2String(version.getSecond());
        }
        Log.d(TAG, "getVersion:" + ConvertUtils.bytes2String(version.getSecond()));

        return null;
        // 执行了10次都没有得到true，返回false
    }

    /**
     * ​ 根据文件名称删除文件内容。
     *
     * @param name 文件名称。
     * @return boolean 如果文件成功删除，则返回True；否则返回False。
     */
//    @DevService("<p>根据文件名称删除文件内容。如果文件成功删除，则返回True；否则返回False。<ul><li>文件名称</li></ul></p><b>成功返回True；否则返回False。</b>")
    public boolean deleteFile(String name) throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (name == null) {
            return false;
        }
        if (!getConnectStatus()) return false;
        Command command = CmdUtil.cmdMap.get("CMD_DELETE");
        command.setFileName(name);
        command.clearBytes();
        Pair<Boolean, byte[]> res = mExecutor.submit(new CmdCallable(command)).get();
        //执行容错
        if (!res.getFirst()) {
            ThreadSleepUtil.sleep(60);
            res = mExecutor.submit(new CmdCallable(command)).get();
            if (!res.getFirst()) {
                return false;
            }
        }
        boolean status = getStatus(command.getCmdName());
//        LoggerUtil.info(logger, );
        Log.d(TAG, "deleteFile: " + "deleteFile: 删除文件是否成功--->" + status);
        return status;
    }


    /**
     * ​ 根据文件名称追加文件内容。
     *
     * @param name 文件名称。
     * @param data 文件内容。
     * @return boolean 如果文件追加写入成功，则返回True；否则返回False。
     */
//    @DevService("<p>根据文件名称追加文件内容。</p><ul><li>文件名称</li><li>文件内容</li></ul><b>成功则返回True；否则返回False。</b>")
    public boolean appendFile(String name, byte[] data) throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (name == null || null == data || data.length == 0) {
            return false;
        }
        if (!getConnectStatus()) return false;
        //切片
        Pair<Boolean, byte[]> result;
        /*if (data.length > PACKAGE_SIZE) {
            //假定所有操作成功
            boolean allSuccess = true;
            List<byte[]> dataList = ByteUtil.splitTitleData(data, PACKAGE_SIZE, PACKAGE_SIZE);
            for (int i = 0; i < dataList.size(); i++) {
                result = signWriteFile(name, dataList.get(i), i == 0 ? "CMD_APPEND" : "CMD_APPEND");
                boolean isSuccess = result.getFirst();
                if (!isSuccess) {
                    // 如果有一个操作失败，将allSuccess设置为false
                    allSuccess = false;
                }
            }
            // 在循环结束后返回allSuccess
            return allSuccess;
        } else {*/
        result = signWriteFile(name, data, "CMD_APPEND");
        return result.getFirst();
        //}
    }


    //    @DevService("<p>获取所有文件名，所有文件名以字符串形式连续返回，以字符串结束标志（'\\0'）识别每个文件名。</p><b>所有文件名字节数组</b>")
    public byte[] listFiles() throws Exception {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (!getConnectStatus()) return null;
        Command fileCommand = CmdUtil.cmdMap.get("CMD_LIST_FILES");
        assert fileCommand != null;
        fileCommand.clearBytes();
        short maxValue = Short.MAX_VALUE;
        fileCommand.setLength((int) maxValue);
        fileCommand.setOffset(0);
        Pair<Boolean, byte[]> res = mExecutor.submit(new CmdCallable(fileCommand)).get();
        //执行容错
        if (!res.getFirst()) {
            res = mExecutor.submit(new CmdCallable(fileCommand)).get();
            if (!res.getFirst()) {
                Log.d(TAG, "listFiles: " + "listFiles: 文件列表获取失败", new LonbestException("文件列表获取失败"));
                return null;
            }
        }
        boolean start = getStatus(fileCommand.getCmdName());
        //还没有开启状态
        return start ? res.getSecond() : null;
    }


    /**
     * 出厂基本信息设置
     */
//    @DevService("<p>出厂基本信息设置。</p><b>成功则返回True；否则返回False。</b>")
//    public boolean updateSNEink(String SN, String date) throws Exception {
//        if (SN == null) {
//            return false;
//        }
//        if (date == null) {
//            return false;
//        }
//        byte[] code = SnQrUtil.createSnScreen(SN, date);
//        return updateEInk(code);
//    }
//
//
//    @DevService("<p>清屏幕设置。</p><b>成功则返回True；否则返回False。</b>")
//    public boolean clearScreen() throws Exception {
//        byte[] data = EdsScreenUtil.clearScreen();
//        return updateEInk(data);
//    }


    //--------------------------------------------------------------------------------------等待验证--------------------------------------------------------------------------------------

    /**
     * 获取存储信息，包括文件总数、存储总容量、可用存储容量、碎片值和文件名总长度
     *
     * @return byte[] 存储信息，包括文件总数、存储总容量、可用存储容量、碎片值和文件名总长度字节数组,错误返回null
     */
//    @DevService("<p>​获取存储信息，包括文件总数、存储总容量、可用存储容量、碎片值和文件名总长度。</p><b>存储信息，包括文件总数、存储总容量、可用存储容量、碎片值和文件名总长度字节数组</b>")
    public byte[] getInfo() throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (!getConnectStatus()) return null;
        Command infoCommand = CmdUtil.cmdMap.get("CMD_GET_INFO");
        assert infoCommand != null;
        infoCommand.clearBytes();
        Pair<Boolean, byte[]> res = mExecutor.submit(new CmdCallable(infoCommand)).get();
        if (!res.getFirst()) {
            ThreadSleepUtil.sleep(70);
            res = mExecutor.submit(new CmdCallable(infoCommand)).get();
            if (!res.getFirst()) {
                Log.d(TAG, "getInfo: " + "getInfo: 获取存储信息是否成功--->" + res.getFirst(), new LonbestException("获取存储信息失败"));
                return null;
            }
        }
        boolean start = getStatus(infoCommand.getCmdName());
        Log.d(TAG, "getInfo: " + "获取存储信息是否成功:" + start + "  数据长度:" + (null != res.getSecond() ? res.getSecond().length : 0));
        return start ? res.getSecond() : null;
    }


    /**
     * 格式化存储。
     *
     * @return boolean 如果存储格式化成功，则返回True；否则返回False。
     */
//    @DevService("<p>格式化存储。</p><b>成功则返回True；否则返回False。</b>")
    public boolean formatInfo() throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        if (!getConnectStatus()) return false;
//        LoggerUtil.info(logger, );
        Log.d(TAG, "formatInfo: " + "formatInfo: 格式化数据开始,执行时间15s,请耐心等待。");
        Command command = CmdUtil.cmdMap.get("CMD_FORMAT");
        assert command != null;
        command.clearBytes();
        mExecutor.submit(new CmdCallable(command)).get();
        ThreadSleepUtil.sleep(20000);
        boolean start = getStatus(command.getCmdName());
        Log.d(TAG, "formatInfo: " + "format: 格式化是否成功--->" + start);
        return start;
    }


    /**
     * 升级
     */
    public boolean upgrade(byte[] data) throws Exception {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Log.d(TAG, "updateVer: " + "开始执行写入升级文件 _SYS_FIRMWARE。");
        boolean sysFirmware = writeFile("_SYS_FIRMWARE", data);
        if (sysFirmware) {
            Log.d(TAG, "updateVer: " + "开始执行升级。");
            Command command = CmdUtil.cmdMap.get("CMD_IAP");
            assert command != null;
            command.clearBytes();
            Pair<Boolean, byte[]> booleanPair = mExecutor.submit(new CmdCallable(command)).get();
//            boolean start = getStatus(command.getCmdName());
            Log.d(TAG, "updateVer: " + "升级是否成功--->" + booleanPair.getFirst());
            return booleanPair.getFirst();
        }
        return false;
    }

    /**
     * 读:测试标签读取速率（命令第一个参数随便写个字符串，第二个参数是测试长度，字符串会重复写入pc文件，直到测试长度）
     * <p>
     * put("CMD_READ_FPS",new Command("NODATA", "read_fps","LPE370",0,0));
     */
    static int FPS_MAX_SIZE = 1048576 / 2;

//    public byte[] readFps() throws Exception {
//        String name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//        int length = (1048576 / 2);
//        //切分
//        FpsService.setTime(0);
//        FpsService.signReadFps(name, length);
//        long executionTime = FpsService.getTime();
//        double seconds = (double) executionTime / 1000;
//        double dataSizeMB = 0.5;
//        double transferRateMBPerSecond = dataSizeMB / seconds;
//        DecimalFormat df = new DecimalFormat("#.##");
//        String formattedResult = df.format(transferRateMBPerSecond);
//        return ("读传输速率：" + formattedResult + "MB/s").getBytes();
//    }


    /**
     * 测试标签写入速率（需提供一个pc文件）
     * <p> 读  写 如果不分割  命令 写0.发命令 1.数据源  1G  2.connect  1.connect  20次异常  ACK 如果是负数 是正确不超时 执行下一步
     * put("CMD_WRITE_FPS",new Command("NODATA", "write_fps","LPE370",0,0));
     */
    public byte[] writeFps() throws Exception {
        int megabytes = 1;
        int byteArraySize = megabytes * 1024 * 1024;
        byte[] data = new byte[byteArraySize];
        byte a = 1;
        Arrays.fill(data, a);
        Command command = CmdUtil.cmdMap.get("CMD_WRITE_FPS");
        assert command != null;
        command.clearBytes();
        command.setBytes(data);
        Pair<Boolean, byte[]> res = FpsService.writeFps(command);
        //LoggerUtil.info(logger, "write_fps: 写:测试标签写入速率是否成功--->" + result.getFirst());
        return res.getSecond();
    }

    /**
     * 获取文件大小
     *
     * @param fileName
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public byte[] findFileSize(String fileName) throws ExecutionException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Command fileCommand = CmdUtil.cmdMap.get("CMD_FIND_FILE");
        assert fileCommand != null;
        fileCommand.setFileName(fileName);
        fileCommand.clearBytes();
        fileCommand.setLength(4);
        Pair<Boolean, byte[]> res = mExecutor.submit(new CmdCallable(fileCommand)).get();
        if (!res.getFirst()) {
            ThreadSleepUtil.sleep(10);
            res = mExecutor.submit(new CmdCallable(fileCommand)).get();
            if (!res.getFirst()) {
                Log.d(TAG, "findFileSize: " + "getInfo: 获取存储信息是否成功--->" + res.getFirst(), new LonbestException("获取存储信息失败"));
                return null;
            }
        }
        boolean start = getStatus(fileCommand.getCmdName());
        if (start) {
            return res.getSecond();
        }
        return null;

    }




    /*-------------------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * 监听usb连接状态如果断开设置为false
     *
     * @param open
     */
    public static void setOpen(boolean open) {
        isOpen = open;
    }


    /**
     * 检查usb存储连接
     *
     * @return
     */

}
