package org.athena.framework.web.util;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Response工具类
 * @author liao
 */
@Slf4j
public class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * response返回Object
     */
    public static void writeObject(HttpServletResponse response, Object object) {
        writeJson(response, JSON.toJSONString(object));
    }

    /**
     * response返回json
     */
    public static void writeJson(HttpServletResponse response, String jsonStr) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonStr);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("response输出json响应失败", e);
        }
    }

    /**
     * response写回数据
     */
    public static void write(HttpServletResponse response, String data) {
        try (PrintWriter writer = response.getWriter()) {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("response输出响应失败", e);
        }
    }

}
