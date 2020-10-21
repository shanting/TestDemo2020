1、文件参数输入
    1）如果只有一个参数（交易记录文件：record.txt、汇率记录文件：rate.txt）
    2）有两个参数（交易记录文件、汇率记录文件），参数顺序不能修改
    3）参数配置：Edit Configurations -> Program arguments
     （参数示例：/Users/shanting/Program/record.txt /Users/shanting/Program/rate.txt）

2、错误信息提示
    1）读取文件/控制台输入，每行空格分割长度不为2、币种如果不在定义的数组、金额不是数字，会输出提示信息
        There is an error in the input
        There is an error in the record file
        There is an error in the rate file

3、关于控制台输入
    1）它支持连续输入多行，以空行结束
    2）输入quit，并以空行结束，即退出程序