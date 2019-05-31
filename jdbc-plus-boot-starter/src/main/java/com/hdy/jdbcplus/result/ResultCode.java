package com.hdy.jdbcplus.result;

/**
 * 返回码
 *
 * @author hdy
 */
public class ResultCode {


    private ResultCode() {
    }

    /**
     * 无相关权限
     */
    public static int c_401 = 401;
    /**
     * 系统繁忙,操作失败
     */
    public static int c_100 = 100;
    /**
     * 通过、合法、允许、正常 等非错误标志
     */
    public static int c_200 = 200;
    /**
     * 请先登录
     */
    public static int c_400 = 400;
    /**
     * 系统错误
     */
    public static int c_500 = 500;
    /**
     * 账号或密码错误
     */
    public static int c_604 = 604;

    /**
     * 格式有误
     */
    public static int c_300 = 300;
    /**
     * 参数缺失
     */
    public static int c_301 = 301;
    /**
     * 文件上传失败
     */
    public static int c_311 = 311;
    /**
     * 文件类型有误
     */
    public static int c_312 = 312;
    /**
     * 文件超出限制大小
     */
    public static int c_313 = 313;
    /**
     * 上传方式有误
     */
    public static int c_314 = 314;
    /**
     * 请勿重复提交
     */
    public static int c_319 = 319;

    /**
     * 验证码已失效
     */
    public static int c_101 = 101;
    /**
     * 验证码有误
     */
    public static int c_102 = 102;
    /**
     * 没有找到相关数据
     */
    public static int c_103 = 103;
}

