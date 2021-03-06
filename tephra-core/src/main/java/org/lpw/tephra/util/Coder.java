package org.lpw.tephra.util;

/**
 * 编码/解码器。
 *
 * @author lpw
 */
public interface Coder {
    /**
     * 将字符串进行URL编码转换。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String encodeUrl(String string, String charset);

    /**
     * 将字符串进行URL解码。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String decodeUrl(String string, String charset);

    /**
     * 将数据进行BASE64编码。
     *
     * @param bytes 要编码的数据。
     * @return 编码后的字符串。
     */
    String encodeBase64(byte[] bytes);

    /**
     * 将数据进行BASE64解码。
     *
     * @param string 要解码的数据。
     * @return 解码后的数据。
     */
    byte[] decodeBase64(String string);
}
