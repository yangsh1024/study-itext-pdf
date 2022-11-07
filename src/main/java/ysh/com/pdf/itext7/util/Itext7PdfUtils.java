package ysh.com.pdf.itext7.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Map;

import com.itextpdf.kernel.pdf.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.signatures.*;
import com.itextpdf.text.pdf.security.DigestAlgorithms;

import lombok.extern.slf4j.Slf4j;
import ysh.com.pdf.util.IouCloseUtils;

/**
 * 
 * itext7工具类
 * 
 * @author yangsh
 * @since 2021/5/11 4:50 下午
 */
@Slf4j
public class Itext7PdfUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 填充后，无法继续操作
     * 
     */
    public static byte[] fillPdfUnableSign(PdfReader pdfReader, Map<String, Object> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PdfDocument pdf = new PdfDocument(pdfReader, new PdfWriter(bos));
            PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdf, true);
            fillText(pdfAcroForm, data);
            pdfAcroForm.flattenFields();
            pdf.close();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("itext7 pdf 内容填充异常", e);
        } finally {
            IouCloseUtils.close(bos);
        }
        return null;
    }

    /**
     *
     * @param templateInputStream
     *            模板文件流
     * @param data
     *            填充文件数据
     * @return 填充后送文件
     */
    public static byte[] fillPdfEnableSign(InputStream templateInputStream, Map<String, Object> data) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(templateInputStream);
            // 填充数据
            return fillPdfEnableSign(reader, data);
        } catch (IOException e) {
            log.error("itext7 PdfReader 获取异常", e);
        } finally {
            IouCloseUtils.close(reader);
        }
        return null;

    }

    public static byte[] fillPdfEnableSign(PdfReader pdfReader, Map<String, Object> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PdfDocument pdf = new PdfDocument(pdfReader, new PdfWriter(bos));
            PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdf, true);
            fillText(pdfAcroForm, data);
            pdf.close();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("itext7 pdf 内容填充异常", e);
        } finally {
            IouCloseUtils.close(bos);
        }
        return null;
    }

    /**
     * 填充数据
     */
    private static void fillText(PdfAcroForm form, Map<String, Object> data) {
        for (String key : data.keySet()) {
            // 值
            Object value = data.get(key);
            // 文本域
            PdfFormField field = form.getField(key);
            if (field == null || value == null) {
                continue;
            }
            // true 复选框模板样式无效
            // false 文本域无法填充
            field.setValue(value.toString(), !field.getFormType().equals(PdfName.Btn));
        }

    }

    public static boolean sign(InputStream src, OutputStream dest, PrivateKey pk, Certificate[] chain,
        String signFieldName, byte[] images, String reason, String location) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(src);
            StampingProperties properties = new StampingProperties();
            // 设置可以追加，允许多次修改
            properties.useAppendMode();
            PdfSigner pdfSigner = new PdfSigner(reader, dest, properties);
            sign(pdfSigner, pk, chain, signFieldName, images, reason, location);

            return true;
        } catch (IOException | GeneralSecurityException e) {
            log.error("pdf签章异常", e);
        } finally {
            IouCloseUtils.close(reader);
        }
        return false;
    }

    public static void sign(PdfSigner pdfSigner, PrivateKey pk, Certificate[] chain, String signFieldName,
        byte[] images, String reason, String location) throws IOException, GeneralSecurityException {
        //
        pdfSigner.setFieldName(signFieldName);
        pdfSigner.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);
        PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();

        appearance.setReason(reason);
        appearance.setLocation(location);
        ImageData imageData = ImageDataFactory.create(images);
        appearance.setSignatureGraphic(imageData);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

        IExternalDigest digest = new BouncyCastleDigest();
        IExternalSignature signature =
            new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        pdfSigner.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }
}
