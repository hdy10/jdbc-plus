package com.github.hdy.jdbcplus.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hdy on 2017/10/17.
 */
public class Logs {
    private static final Logger logger = LogManager.getLogger(Logs.class.getName());

    public static void debug(Object msg) {
        logger.debug("\u001b[33m" + msg + "\u001b[0m");
    }

    public static void info(Object msg) {
        logger.info("\u001b[35m" + msg + "\u001b[0m");
    }

    public static void warn(Object msg) {
        logger.warn("\u001b[34m" + msg + "\u001b[0m");
    }

    public static void error(Object msg) {
        logger.error("\u001b[31m" + msg + "\u001b[0m");
    }

    public static void printHr(Object msg) {
        System.out.println("\u001b[33m" + msg + "\u001b[0m");
    }

    public static void main(String[] args) {
        debug("debug");
        info("info");
        warn("warn");
        error("error");
        printHr("printHr");
    }
}
