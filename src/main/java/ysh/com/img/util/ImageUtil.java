package ysh.com.img.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {


    public static byte[] strToImg(String content) {
        Font font = new Font(null, Font.PLAIN, 60);
        BufferedImage image = ImgUtil.createImage(content, font, null, null, BufferedImage.TYPE_INT_ARGB);
        return ImgUtil.toBytes(image, "PNG");

    }


    public static void main(String[] args) {
        byte[] s = strToImg("仰守浩");
   String destPath =
                "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/s.png";
        FileUtil.writeBytes(s,destPath);
    }
}
