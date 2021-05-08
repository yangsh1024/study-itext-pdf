package ysh.com.pdf.util;

import java.io.File;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 配合pdf使用的相关文件操作类
 * 
 * @author yangsh
 * @since 2021/5/8 4:46 下午
 */
@Slf4j
public class FileUtils {

    /**
     * 保存文件
     *
     * @param bytes
     *            文件内容
     * @param destPath
     *            路径
     * @return 是否成功
     */
    public static boolean saveFile(byte[] bytes, String destPath) {
        if (bytes == null) {
            log.info("文件内容为空，无法写入");
            return false;
        } else {
            // 写入文件
            File file = FileUtil.writeBytes(bytes, destPath);
            if (file == null) {
                log.info("字节写入文件失败");
                return false;
            } else {
                return true;
            }
        }
    }

}
