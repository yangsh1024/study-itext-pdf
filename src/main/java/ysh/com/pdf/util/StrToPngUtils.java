package ysh.com.pdf.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangsh
 * @since 2021/5/11 2:56 下午
 */
@Slf4j
public class StrToPngUtils {

    /**
     * 文字转对应签章图片
     * 
     * 默认红色
     * 
     */
    public static byte[] strToPng(String str) {
        Font defaultFont = new Font("宋体", Font.PLAIN, 120);
        return strToPng(str, Color.RED, defaultFont);
    }

    public static byte[] strToPng(String str, Color color, Font font) {
        if (StrUtil.isBlank(str)) {
            log.warn("strToImg-字符为空");
            return null;
        }
        if (color == null) {
            log.warn("strToImg-color is null");
            return null;
        }
        if (font == null) {
            log.warn("strToImg-font is null");
            return null;
        }

        // ARGB - A透明；RGB不存在透明
        BufferedImage image = ImgUtil.createImage(str, font, null, color, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream bot = new ByteArrayOutputStream();
        ImgUtil.writePng(image, bot);
        return bot.toByteArray();
    }
}
