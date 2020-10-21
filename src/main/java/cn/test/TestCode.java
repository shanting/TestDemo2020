package cn.test;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class TestCode {
    private static final String[] currency = {"USD", "HKD", "CNY", "NZD", "GBP"};
    private static final String RECORD_TYPE = "record file";
    private static final String RATE_TYPE = "rate file";
    private static final String INPUT_TYPE = "input";
    private static final String QUIT = "quit";

    private static List<String> currencyList;
    private static Map<String, BigDecimal> recordMap = new HashMap<>();
    private static Map<String, BigDecimal> rateMap = new HashMap<>();

    public static void main(String[] args) {
        currencyList = Arrays.asList(currency);
        // 文件读取
        if (args!= null && args.length > 0) {
            // 付款记录
            if (args.length == 1 && "record.txt".equals(args[0])) {
                readFile(args[0], RECORD_TYPE);
            } else if (args.length == 1 && "rate.txt".equals(args[0])) {
                // 美元汇率比例 record file\rate file
                readFile(args[0], RATE_TYPE);
            } else if (args.length == 2) {
                // 如果两个文件，第一个是付款记录文件，第二个是美元汇率比例文件
                readFile(args[0], RECORD_TYPE);
                readFile(args[1], RATE_TYPE);
            }
        }

        // 加载命令行输入
        inputTask();

        // 每分钟执行一次输出
        timerTask();
    }

    /**
     * 定时任务，每分钟执行一次输出
     */
    private static void timerTask() {
        Calendar calendar = Calendar.getInstance();
        Date firstTime = calendar.getTime();
        long period = 60000; // 间隔：1分钟

        // 定时任务，从当前时间开始执行，间隔一分钟
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processBusiness();
            }
        }, firstTime, period);
    }

    /**
     * 业务处理逻辑
     */
    private static void processBusiness() {
        System.out.println();
        // 如果存在汇率信息
        if (recordMap != null && rateMap != null
                && recordMap.size() > 0 & rateMap.size() > 0) {
            Map<String, String> recordRateMap = new HashMap<>();

            // 处理USD汇率数据
            recordMap.forEach((key, value) -> {
                if (rateMap.containsKey(key)) {
                    recordRateMap.put(key, recordMap.get(key)
                            + " (USD " + rateMap.get(key).multiply(recordMap.get(key))
                            .setScale(2, BigDecimal.ROUND_HALF_UP) + ")");
                } else {
                    recordRateMap.put(key, recordMap.get(key) + "");
                }
            });

            // 控制台打印信息
            recordRateMap.forEach((key, value) -> {
                System.out.println(key + " " + recordRateMap.get(key));
            });
        } else { // 没有汇率信息，直接输出交易信息
            recordMap.forEach((key, value) -> {
                System.out.println(key + " " + recordMap.get(key));
            });
        }
    }

    /**
     * 加载控制台输入
     */
    private static void inputTask() {
        Runnable runnable = () -> {
            while (true) {
                getScannerInput();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 命令行输入
     *
     * @return 返回命令行输入内容
     */
    private static void getScannerInput() {
        Scanner scanner = new Scanner(System.in);
        String scannerContent = scanner.nextLine();
        if (QUIT.equals(scannerContent)) {
            System.exit(0);
        } else {
            while (scannerContent != null && !"".equals(scannerContent)) {
                handleContent(scannerContent, INPUT_TYPE);
                scannerContent = scanner.nextLine();
                if (QUIT.equals(scannerContent)) {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * 美元汇率/付款记录，文件读取
     *
     * @param fileName 文件名
     * @param type 文件类型
     */
    private static void readFile(String fileName, String type) {
        File file = new File(fileName);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), "utf-8"));
                // 一次读入一行
                String tempString;
                while ((tempString = reader.readLine()) != null) {
                    handleContent(tempString, type);
                    if (RECORD_TYPE.equals(type)) {
                        System.out.println(tempString);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 处理每行记录内容
     *
     * @param content 文本内容
     * @param type 日志记录类型
     */
    private static void handleContent(
            String content, String type) {
        String[] lineContent = content.split(" ");
        if (lineContent.length != 2) {
            System.out.println("There is an error in the " + type);
        } else {
            if (!currencyList.contains(lineContent[0]) || !isNumeric(lineContent[1])) {
                System.out.println("There is an error in the " + type);
            } else {
                if (RATE_TYPE.equals(type)) {
                    rateMap.put(lineContent[0], new BigDecimal(lineContent[1]));
                } else if (recordMap.containsKey(lineContent[0])) {
                    recordMap.put(lineContent[0],
                            recordMap.get(lineContent[0]).add(new BigDecimal(lineContent[1])));
                } else {
                    recordMap.put(lineContent[0], new BigDecimal(lineContent[1]));
                }
            }
        }
    }

    /**
     * 判断一个字符串是否是数字
     *
     * @param str 字符串
     * @return 返回true、false
     */
    public static boolean isNumeric(String str){
        return str.matches("-?[0-9]+.?[0-9]*");
    }
}
