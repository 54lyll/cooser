package com.wonderzh.cooser.common.constarnt;

import java.util.HashSet;
import java.util.Set;

/**
 * 操作码
 * @Author: wonderzh
 * @Date: 2020/4/23
 * @Version: 1.0
 */

public interface FunctionCode {

    FunctionCode CONTAINER = new FunctionCodeContainer();

    /**
     * 命令：设定值
     */
    int COMMAND_SET = 0;

    /**
     * 规则：设定值
     */
    int RULE_SET = 10;

    /**
     * 中止
     */
    int BREAK_DOWN = 99;

    boolean contains(int code);


    class FunctionCodeContainer implements FunctionCode{

        private static Set<Integer> all = new HashSet<>();

        static {
            all.add(FunctionCode.COMMAND_SET);
            all.add(FunctionCode.RULE_SET);
            all.add(FunctionCode.BREAK_DOWN);
        }

        @Override
        public boolean contains(int code) {
            return FunctionCodeContainer.all.contains(code);
        }
    }
}

