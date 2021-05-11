package ysh.com;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import ysh.com.pdf.itext5.util.Itext5PdfUtils;

@SpringBootTest
class Itext5Test {

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
        byte[] bytes = Itext5PdfUtils.fillPdfEnableSign(new PdfReader(templatePath), data);
        String p12Path =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/cert.p12";
        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        char[] password = "123456".toCharArray();
    }

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
        data.put("check2", "Y");
        data.put("group3", "ch1");

        byte[] bytes = Itext5PdfUtils.fillPdfEnableSign(new FileInputStream(templatePath), data);
        FileUtil.writeBytes(bytes, destPath);
    }

    @Test
    void sign1() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException,
        CertificateException {
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
        Itext5PdfUtils.sign(new FileInputStream(templatePath), new FileOutputStream(destPath), key, certificate, "sign",
            images, "reason", "location");
    }

    @Test
    void sign2() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException,
        CertificateException {
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
        Itext5PdfUtils.sign(new FileInputStream(templatePath), new FileOutputStream(destPath), key2, certificate2,
            "sign2", images2, "reason", "location");
    }

    @Test
    void itext5Sign() throws Exception {

        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";
        Map<String, Object> data = new HashMap<>();
        data.put("id", "10010");
        data.put("userName", "ysh");
        data.put("check1", "Y");
        data.put("check2", "Y");
        data.put("group3", "ch1");
        byte[] contentBytes = Itext5PdfUtils.fillPdfEnableSign(new PdfReader(templatePath), data);

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
        String alias2 = keyStore2.aliases().nextElement();
        PrivateKey key2 = (PrivateKey)keyStore2.getKey(alias2, password2);
        Certificate[] certificate2 = keyStore2.getCertificateChain(alias2);

        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";

        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        byte[] images = FileUtil.readBytes(imagePath);
        byte[] images2 = FileUtil.readBytes(imagePath);

        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        Itext5PdfUtils.sign(IoUtil.toStream(contentBytes), temp, key2, certificate2, "sign2", images2, "reason",
            "location");

        byte[] bytes = temp.toByteArray();
        Itext5PdfUtils.sign(IoUtil.toStream(bytes), new FileOutputStream(destPath), key, certificate, "sign", images,
            "reason", "location");
    }

    @Test
    void itext5Sign2() throws Exception {

        String templatePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/template_谈话记录.pdf";

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
        String alias2 = keyStore2.aliases().nextElement();
        PrivateKey key2 = (PrivateKey)keyStore2.getKey(alias2, password2);
        Certificate[] certificate2 = keyStore2.getCertificateChain(alias2);

        String destPath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign.pdf";

        String imagePath =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/template/signer.png";
        byte[] images = FileUtil.readBytes(imagePath);
        byte[] images2 = FileUtil.readBytes(imagePath);

        // ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // Itext5PdfUtils.sign(new FileInputStream(templatePath), new FileOutputStream(destPath), key2, certificate2,
        // "sign2", images2, "reason", "location");

        // byte[] bytes = temp.toByteArray();
        String destPath2 =
            "/Users/yangsh/Documents/workspace_my/my-github-study/study-itext-pdf/src/test/resources/dest/sign2.pdf";
        Itext5PdfUtils.sign(new FileInputStream(destPath), new FileOutputStream(destPath2), key, certificate, "sign",
            images, "reason", "location");
    }

}
