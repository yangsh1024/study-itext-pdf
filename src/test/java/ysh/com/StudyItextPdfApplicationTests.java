package ysh.com;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;

import cn.hutool.core.io.FileUtil;
import ysh.com.pdf.itext5.util.Itext5PdfUtils;

@SpringBootTest
class StudyItextPdfApplicationTests {

    @Test
    void fillAndSign() throws IOException, GeneralSecurityException, DocumentException {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/over.pdf";
        Map<String, Object> data = new HashMap<>();
        data.put("id", "10010");
        data.put("userName", "ysh");
        data.put("check1", "Y");
        data.put("check2", "Y");
        data.put("group3", "ch1");
        byte[] bytes = Itext5PdfUtils.fillTextEnableSign(new PdfReader(templatePath), data);
        String p12Path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/cert.p12";
        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        char[] password = "123456".toCharArray();
    }

    @Test
    void fill() {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/temp.pdf";
        Map<String, Object> data = new HashMap<>();
        data.put("id", "10010");
        data.put("userName", "ysh");
        data.put("check1", "Y");
        data.put("check2", "Y");
        data.put("group3", "ch1");

        boolean b = Itext5PdfUtils.generatePdfUnableSign(templatePath, destPath, data);
        System.out.println(b);
    }

    @Test
    void itext5Sign() throws Exception {

        String p12Path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/cert.p12";
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] password = "123456".toCharArray();
        keyStore.load(new FileInputStream(p12Path), password);
        String alias = keyStore.aliases().nextElement();
        PrivateKey key = (PrivateKey)keyStore.getKey(alias, password);
        Certificate[] certificate = keyStore.getCertificateChain(alias);

        String p12Path2 =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/yangsh.p12";
        KeyStore keyStore2 = KeyStore.getInstance("PKCS12");
        char[] password2 = "123456".toCharArray();
        keyStore2.load(new FileInputStream(p12Path2), password2);
        String alias2 = keyStore.aliases().nextElement();
        PrivateKey key2 = (PrivateKey)keyStore.getKey(alias2, password);
        Certificate[] certificate2 = keyStore.getCertificateChain(alias2);

        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";

        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        byte[] images = FileUtil.readBytes(imagePath);

        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        Itext5PdfUtils.sign(new FileInputStream(templatePath), temp, key2, certificate2, "sign2", images, "reason",
            "location");

        byte[] bytes = temp.toByteArray();
        Itext5PdfUtils.sign(IoUtil.toStream(bytes), new FileOutputStream(destPath), key, certificate, "sign", images,
            "reason", "location");
    }

    @Test
    void sign2() throws IOException, DocumentException, GeneralSecurityException {
        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";
        String p12Path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/cert.p12";
        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        char[] password = "123456".toCharArray();

    }

}
