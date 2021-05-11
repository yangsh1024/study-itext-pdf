package ysh.com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.hutool.core.io.FileUtil;
import ysh.com.pdf.util.StrToPngUtils;

@SpringBootTest
class UtilTests {

    @Test
    public void strToPic() {
        String path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/ysh.png";
        byte[] bytes = StrToPngUtils.strToPng("仰守浩");
        FileUtil.writeBytes(bytes, path);
    }
}
