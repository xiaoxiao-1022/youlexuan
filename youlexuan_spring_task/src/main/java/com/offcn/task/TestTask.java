package com.offcn.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestTask {

        /*
        *     第一位      第二位    第三位          第四位       第五位         第六位
        *     秒(0-59)  分(0-59)    小时 (0-23)    天 (1-31)    月(1-12)      星期(MON-SUN)
        *     *  任意单位
        *     ,  多个值
        *     /  间隔单位
        *     -  范围区间
        *     ？ 占位符 一定会有个值
        * */

        //@Scheduled(cron = "5,10,25,45 * 9 13 12 ? ")
        //@Scheduled(cron = "15/5 * 9 13 12 ? ") //每分钟的15秒开始 每隔5秒执行一次
        //@Scheduled(cron = "25-45,15-20 30-59 9 13 12 ? ")
        public void testRun(){
            System.out.println(new Date());
        }
}
