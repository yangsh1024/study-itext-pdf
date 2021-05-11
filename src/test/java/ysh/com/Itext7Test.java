package ysh.com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cn.hutool.core.io.FileUtil;
import ysh.com.pdf.itext7.util.Itext7PdfUtils;

/**
 * @author yangsh
 * @since 2021/5/11 5:10 下午
 */
public class Itext7Test {

    @Test
    void fill() throws FileNotFoundException {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/fill.pdf";
        Map<String, Object> data = new HashMap<>();
        data.put("id", "10010");
        data.put("userName", "ysh");
        data.put("check1", "Y");
        data.put("group3", "ch2");

        byte[] bytes = Itext7PdfUtils.fillPdfEnableSign(new FileInputStream(templatePath), data);
        FileUtil.writeBytes(bytes, destPath);
    }

    @Test
    void sign2() throws Exception {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/fill.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";

        String p12Path2 =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/yangsh.p12";
        KeyStore keyStore2 = KeyStore.getInstance("PKCS12");
        char[] password2 = "123456".toCharArray();
        keyStore2.load(new FileInputStream(p12Path2), password2);
        String alias2 = keyStore2.aliases().nextElement();
        PrivateKey key2 = (PrivateKey)keyStore2.getKey(alias2, password2);
        Certificate[] certificate2 = keyStore2.getCertificateChain(alias2);

        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        byte[] images2 = FileUtil.readBytes(imagePath);
        Itext7PdfUtils.sign(new FileInputStream(templatePath), new FileOutputStream(destPath), key2, certificate2,
            "sign2", images2, "reason", "location");
    }

    @Test
    void sign() throws Exception {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign2.pdf";

        String p12Path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/cert.p12";
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] password = "123456".toCharArray();
        keyStore.load(new FileInputStream(p12Path), password);
        String alias = keyStore.aliases().nextElement();
        PrivateKey key = (PrivateKey)keyStore.getKey(alias, password);
        Certificate[] certificate = keyStore.getCertificateChain(alias);

        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/ysh.png";
        byte[] images = FileUtil.readBytes(imagePath);
        Itext7PdfUtils.sign(new FileInputStream(templatePath), new FileOutputStream(destPath), key, certificate, "sign",
            images, "reason", "location");
    }
}
