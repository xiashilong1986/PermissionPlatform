package com.permission.utils.global.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * created by xiashilong
 * 2017-11-23 15:03
 **/
public class GlobalResultUtil {
    /**
     * 成功 有返回值
     *
     * @param resultEnum resultEnum
     * @param object     返回对象
     * @return GlobalResult
     */
    public static GlobalResult success(ResultEnum resultEnum, Object object) {
        return new GlobalResult(resultEnum, object);
    }

    /**
     * 成功 无返回值
     *
     * @return GlobalResult
     */
    public static GlobalResult success(ResultEnum resultEnum) {
        return success(resultEnum, null);
    }


    public static GlobalResult success(Object object) {
        return success(ResultEnum.SUCCESS, object);
    }

    public static GlobalResult success() {
        return success(ResultEnum.SUCCESS, null);
    }

    /**
     * 失败 有返回值
     *
     * @param resultEnum resultEnum
     * @return GlobalResult
     */
    public static GlobalResult fail(ResultEnum resultEnum, Object object) {
        return new GlobalResult(resultEnum, object);
    }

    /**
     * 失败 无返回值
     *
     * @param resultEnum resultEnum
     * @return GlobalResult
     */
    public static GlobalResult fail(ResultEnum resultEnum) {
        return fail(resultEnum, null);
    }

    public static GlobalResult fail(Object object) {
        return fail(ResultEnum.FAILURE, object);
    }

    public static GlobalResult fail(Integer code, String msg) {
        return new GlobalResult(code, msg);
    }

    public static GlobalResult fail() {
        return fail(ResultEnum.FAILURE, null);
    }


    public static void out(HttpServletResponse response, GlobalResult result) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.write(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static void error(HttpServletResponse response, GlobalResult result) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setStatus(result.getCode());
            out = response.getWriter();
            out.println(JSON.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
