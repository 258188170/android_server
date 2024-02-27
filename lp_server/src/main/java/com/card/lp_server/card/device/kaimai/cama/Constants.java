package com.card.lp_server.card.device.kaimai.cama;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class Constants {

    public  static final  String  MB_COM ="COM";
    public  static final  String  MB_BAUDRATE ="BAUDRATE";
    public  static final  String  MB_IPCONFIG ="IPCONFIG";
    public static String kmData = "{\"debug\":\"true\",\"datas\":[{\"model\":\"PL10\",\"rows\":[{\"date\":\"2014-12-04 11:17:18\",\"items\":[{\"name\":\"PL－10导弹存在\",\"result\":\"√\",\"scope\":\"0.088\"},{\"name\":\"安保安全\",\"result\":\"√\",\"scope\":\"1.106\"},{\"name\":\"PL－10识别\",\"result\":\"√\",\"scope\":\"100.000\"},{\"name\":\"导弹电源＋27V\",\"result\":\"√\",\"scope\":\"27.549\"},{\"name\":\"导弹电源＋20V\",\"result\":\"√\",\"scope\":\"19.785\"},{\"name\":\"导弹电源－20V\",\"result\":\"√\",\"scope\":\"-20.020\"},{\"name\":\"导弹电源＋12V\",\"result\":\"√\",\"scope\":\"12.064\"},{\"name\":\"导弹电源－12V\",\"result\":\"√\",\"scope\":\"-11.970\"},{\"name\":\"导弹电源＋5V\",\"result\":\"√\",\"scope\":\"5.047\"},{\"name\":\"导弹电源－5V\",\"result\":\"√\",\"scope\":\"-5.029\"},{\"name\":\"导弹致冷时间\",\"result\":\"√\",\"scope\":\"168.000\"},{\"name\":\"导弹致冷\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹自检正常时间\",\"result\":\"√\",\"scope\":\"178.000\"},{\"name\":\"导弹自检正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹耗气量\",\"result\":\"√\",\"scope\":\"0.492\"},{\"name\":\"导弹截获\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹随动\",\"result\":\"√\",\"scope\":\"0.000\"},{\"name\":\"正舵机电池\",\"result\":\"√\",\"scope\":\"44.909\"},{\"name\":\"负舵机电池\",\"result\":\"√\",\"scope\":\"-44.464\"},{\"name\":\"电池电压正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"电池电压合格\",\"result\":\"√\",\"scope\":\"23.628\"},{\"name\":\"舵机自检正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"电气分离\",\"result\":\"√\",\"scope\":\"20.679\"},{\"name\":\"引信启动信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信UCLK1幅值\",\"result\":\"√\",\"scope\":\"4.480\"},{\"name\":\"引信UCLK1频率\",\"result\":\"√\",\"scope\":\"333.320\"},{\"name\":\"导航位置误差△XR\",\"result\":\"√\",\"scope\":\"6.875\"},{\"name\":\"导航位置误差△YR\",\"result\":\"√\",\"scope\":\"9.375\"},{\"name\":\"导航位置误差△ZR\",\"result\":\"√\",\"scope\":\"6.250\"},{\"name\":\"导航姿态误差△PSI\",\"result\":\"√\",\"scope\":\"0.073\"},{\"name\":\"导航姿态误差△TETA\",\"result\":\"√\",\"scope\":\"0.028\"},{\"name\":\"导航姿态误差△GAMMA\",\"result\":\"√\",\"scope\":\"0.073\"},{\"name\":\"引信自炸时间\",\"result\":\"√\",\"scope\":\"31.562\"},{\"name\":\"舵控舵反馈一致性\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"蓄冷时间\",\"result\":\"√\",\"scope\":\"40.000\"},{\"name\":\"引信近炸信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信目标识别信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信触发信号\",\"result\":\"√\",\"scope\":\"0.008\"},{\"name\":\"引信延迟时间\",\"result\":\"√\",\"scope\":\"**\"}],\"num\":\"306\",\"optor\":\"WJL\",\"prjID\":\"S8.P100141401008\"},{\"date\":\"2014-12-04 10:52:47\",\"items\":[{\"name\":\"PL－10导弹存在\",\"result\":\"√\",\"scope\":\"0.075\"},{\"name\":\"安保安全\",\"result\":\"√\",\"scope\":\"1.269\"},{\"name\":\"PL－10识别\",\"result\":\"√\",\"scope\":\"100.000\"},{\"name\":\"导弹电源＋27V\",\"result\":\"√\",\"scope\":\"27.542\"},{\"name\":\"导弹电源＋20V\",\"result\":\"√\",\"scope\":\"19.794\"},{\"name\":\"导弹电源－20V\",\"result\":\"√\",\"scope\":\"-20.017\"},{\"name\":\"导弹电源＋12V\",\"result\":\"√\",\"scope\":\"12.054\"},{\"name\":\"导弹电源－12V\",\"result\":\"√\",\"scope\":\"-11.977\"},{\"name\":\"导弹电源＋5V\",\"result\":\"√\",\"scope\":\"5.047\"},{\"name\":\"导弹电源－5V\",\"result\":\"√\",\"scope\":\"-5.026\"},{\"name\":\"导弹致冷时间\",\"result\":\"√\",\"scope\":\"168.000\"},{\"name\":\"导弹致冷\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹自检正常时间\",\"result\":\"√\",\"scope\":\"178.000\"},{\"name\":\"导弹自检正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹耗气量\",\"result\":\"√\",\"scope\":\"1.056\"},{\"name\":\"导弹截获\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹随动\",\"result\":\"√\",\"scope\":\"0.000\"},{\"name\":\"正舵机电池\",\"result\":\"√\",\"scope\":\"44.835\"},{\"name\":\"负舵机电池\",\"result\":\"√\",\"scope\":\"-43.751\"},{\"name\":\"电池00\",\"result\":\"正舵机电池\",\"scope\":\"√\"},{\"name\":\"44.835\",\"result\":\"负舵机电池\",\"scope\":\"√\"},{\"name\":\"-43.751\",\"result\":\"电池电压正常\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"电池电压合格\",\"scope\":\"√\"},{\"name\":\"23.612\",\"result\":\"舵机自检正常\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"电气分离\",\"scope\":\"√\"},{\"name\":\"20.661\",\"result\":\"引信启动信号\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"引信UCLK1幅值\",\"scope\":\"√\"},{\"name\":\"4.520\",\"result\":\"引信UCLK1频率\",\"scope\":\"√\"},{\"name\":\"333.350\",\"result\":\"导航位置误差△XR\",\"scope\":\"√\"},{\"name\":\"10.000\",\"result\":\"导航位置误差△YR\",\"scope\":\"√\"},{\"name\":\"3.125\",\"result\":\"导航位置误差△ZR\",\"scope\":\"√\"},{\"name\":\"3.125\",\"result\":\"导航姿态误差△PSI\",\"scope\":\"√\"},{\"name\":\"0.146\",\"result\":\"导航姿态误差△TETA\",\"scope\":\"√\"},{\"name\":\"0.059\",\"result\":\"导航姿态误差△GAMMA\",\"scope\":\"√\"},{\"name\":\"0.000\",\"result\":\"引信自炸时间\",\"scope\":\"√\"},{\"name\":\"31.562\",\"result\":\"舵控舵反馈一致性\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"蓄冷时间\",\"scope\":\"√\"},{\"name\":\"40.000\",\"result\":\"引信近炸信号\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"引信目标识别信号\",\"scope\":\"√\"},{\"name\":\"**\",\"result\":\"引信触发信号\",\"scope\":\"√\"},{\"name\":\"0.005\",\"result\":\"引信延迟时间\",\"scope\":\"√\"}],\"num\":\"306\",\"optor\":\"WJL\",\"prjID\":\"S8.P100141401005\"}]},{\"model\":\"PL10\",\"rows\":[{\"date\":\"2012-04-18 11:44:37\",\"items\":[{\"name\":\"PL－10导弹存在\",\"result\":\"√\",\"scope\":\"0.074\"},{\"name\":\"安保安全\",\"result\":\"√\",\"scope\":\"1.216\"},{\"name\":\"PL－10识别\",\"result\":\"√\",\"scope\":\"100.000\"},{\"name\":\"导弹电源＋27V\",\"result\":\"√\",\"scope\":\"27.770\"},{\"name\":\"导弹电源＋20V\",\"result\":\"√\",\"scope\":\"19.985\"},{\"name\":\"导弹电源－20V\",\"result\":\"√\",\"scope\":\"-20.014\"},{\"name\":\"导弹电源＋12V\",\"result\":\"√\",\"scope\":\"11.994\"},{\"name\":\"导弹电源－12V\",\"result\":\"√\",\"scope\":\"-12.010\"},{\"name\":\"导弹电源＋5V\",\"result\":\"√\",\"scope\":\"5.076\"},{\"name\":\"导弹电源－5V\",\"result\":\"√\",\"scope\":\"-5.069\"},{\"name\":\"导弹致冷时间\",\"result\":\"√\",\"scope\":\"168.000\"},{\"name\":\"导弹致冷\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹自检正常时间\",\"result\":\"√\",\"scope\":\"178.000\"},{\"name\":\"导弹自检正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹耗气量\",\"result\":\"√\",\"scope\":\"0.774\"},{\"name\":\"导弹截获\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"导弹随动\",\"result\":\"√\",\"scope\":\"0.000\"},{\"name\":\"正舵机电池\",\"result\":\"√\",\"scope\":\"44.951\"},{\"name\":\"负舵机电池\",\"result\":\"√\",\"scope\":\"-44.728\"},{\"name\":\"电池电压正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"电池电压合格\",\"result\":\"√\",\"scope\":\"24.054\"},{\"name\":\"舵机自检正常\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"电气分离\",\"result\":\"√\",\"scope\":\"22.402\"},{\"name\":\"引信启动信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信UCLK1幅值\",\"result\":\"√\",\"scope\":\"4.520\"},{\"name\":\"引信UCLK1频率\",\"result\":\"√\",\"scope\":\"333.329\"},{\"name\":\"导航位置误差△XR\",\"result\":\"√\",\"scope\":\"0.625\"},{\"name\":\"导航位置误差△YR\",\"result\":\"√\",\"scope\":\"3.125\"},{\"name\":\"导航位置误差△ZR\",\"result\":\"√\",\"scope\":\"6.250\"},{\"name\":\"导航姿态误差△PSI\",\"result\":\"√\",\"scope\":\"0.089\"},{\"name\":\"导航姿态误差△TETA\",\"result\":\"√\",\"scope\":\"0.204\"},{\"name\":\"导航姿态误差△GAMMA\",\"result\":\"√\",\"scope\":\"0.000\"},{\"name\":\"引信自炸时间\",\"result\":\"√\",\"scope\":\"31.547\"},{\"name\":\"舵控舵反馈一致性\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"蓄冷时间\",\"result\":\"√\",\"scope\":\"34.000\"},{\"name\":\"引信近炸信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信目标识别信号\",\"result\":\"√\",\"scope\":\"**\"},{\"name\":\"引信触发信号\",\"result\":\"√\",\"scope\":\"0.000\"}],\"num\":\"286\",\"optor\":\"wsm\",\"prjID\":\"P100141100006\"}]}]}";
    public static String gc014Data = "{\"debug\":\"true\",\"ztdsjs\":10,\"dybh\":\"P12A014112233444\",\"ztdsjf\":49,\"tdcs\":98,\"bctdsj\":181,\"ztdsjm\":54,\"dyt\":\"63-7028\"}";

    public static String ERRMSG = "{\"success\":false,\"message\":\"系统处理异常\"}";
    public static String SBCS_014 = "01";
    public static String SBCS_014_JSQ2 = "04";
    public static String SBCS_014_JSQ3 = "05";
    public static String SBCS_KAIMAI = "02";
    public static String SBCS_LONGBEI = "03";
    public static String SBCS_U2S = "06";
    public static String SERIAL_PORT = "/dev/ttyS2";


    public static String GLOBAL_DYBH = "1101"; //全局编号

    public static int   BAUDRATE_PRINTER  = 9600; //打印机波特率
    public static Map<String, String> hidMap = new HashMap<>();
    public static Map<String, Thread> GLOBAL_THREAD_MAP =  Collections.synchronizedMap(new LinkedHashMap<>());

    public static Queue<String> GLOABL_ERR_MESSAGE = new LinkedBlockingQueue<>();
    public static int GLOABL_BAUDRATE = 115200 ;//默认波特率
    public static String GLOABL_IPCONFIG = "" ;
    static {
        hidMap.put(SBCS_LONGBEI, "58409-6790");
        hidMap.put(SBCS_014, "22352-1155");  //0x0483, 0x5750
        hidMap.put(SBCS_KAIMAI, "22352-23112"); //0x5A48, 0x5750
        hidMap.put(SBCS_014_JSQ2, "22336-1155");
        hidMap.put(SBCS_014_JSQ3, "22320-1155");
        hidMap.put(SBCS_014_JSQ3, "22320-1155");
        hidMap.put(SBCS_U2S, "29987-6790");
    }


    public static void addDebugMsg(String msg){
        GLOABL_ERR_MESSAGE.add(msg);
    }

    public static String print(byte[] state, int len) {
        StringBuffer sb = new StringBuffer();
        int i;
        for (i = 0; i < len; i++) {
            if (i % 16 == 0)
                System.out.println();
            System.out.printf("%02x ", state[i] & 0xff);
            sb.append(String.format("%02x", state[i] & 0xff));
        }
        return sb.toString();
    }


    /**
     * 描述：打印错误信息
     * @param e
     * @param tip
     * @return
     */
   public static String printError(Exception e,String tip){
       StringWriter sw = new StringWriter();
       PrintWriter pw = new PrintWriter(sw);
       e.printStackTrace(pw);
       String errorMsg = sw.toString();
       return  tip+"("+ errorMsg + ")";
   }

}
