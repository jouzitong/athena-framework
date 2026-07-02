package org.arthena.framework.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ID 工具类
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
public class IdUtils {

    private static final String DEFAULT_MACHINE_ID = "000000000000";

    private static final String MACHINE_ID = resolveMachineId();

    private IdUtils() {
    }

    /**
     * 生成 32 位 UUID 字符串（无中划线）
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带机器特征的 32 位随机 ID。
     */
    public static String random32() {
        String source = MACHINE_ID
            + "|"
            + resolveProcessId()
            + "|"
            + System.currentTimeMillis()
            + "|"
            + System.nanoTime()
            + "|"
            + Thread.currentThread().getId()
            + "|"
            + ThreadLocalRandom.current().nextLong();
        return md5Hex(source);
    }

    private static String resolveMachineId() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface == null || networkInterface.isLoopback() || networkInterface.isVirtual()
                    || !networkInterface.isUp()) {
                    continue;
                }
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null || hardwareAddress.length == 0) {
                    continue;
                }
                return bytesToHex(hardwareAddress);
            }
        } catch (Exception ignored) {
        }
        return DEFAULT_MACHINE_ID;
    }

    private static String resolveProcessId() {
        String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isBlank(runtimeName)) {
            return "unknown";
        }
        return runtimeName;
    }

    private static String md5Hex(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(source.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length << 1);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xf, 16));
            sb.append(Character.forDigit(b & 0xf, 16));
        }
        return sb.toString();
    }
}
