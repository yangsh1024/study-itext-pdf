package ysh.com.pdf.itext5.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;

import lombok.extern.slf4j.Slf4j;
import ysh.com.pdf.util.FileUtils;
import ysh.com.pdf.util.IouCloseUtils;

/**
 * Itext5实现工具类
 *
 * 存在问题：
 * 
 * pdfStamper.setFormFlattening(true);导致签名域消失，填充后无法继续签署
 * 
 * 不使用这个，MAC 预览工具查看pdf，复选框可勾选修改（建议使用文本域，填充打钩字样'√'）
 * 
 * （MAC预览工具，可随意移动签名图片）
 * 
 * @author yangsh
 * @since 2021/5/6 6:13 下午
 */
@Slf4j
public class Itext5PdfUtils {

    public static boolean generatePdfUnableSign(String templatePath, String destPath, Map<String, Object> data) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(templatePath);
            // 填充数据
            byte[] bytes = fillPdfUnableSign(reader, data);
            return FileUtils.saveFile(bytes, destPath);
        } catch (IOException e) {
            log.error("itext5 PdfReader 获取异常", e);
        } finally {
            IouCloseUtils.close(reader);
        }
        return false;

    }

    /**
     * 填充pdf文本域内容。填充后，不支持签名
     * 
     * 
     * @param reader
     *            pdfReader
     * @param data
     *            填充数据
     * @return 填充后字节
     */
    public static byte[] fillPdfUnableSign(PdfReader reader, Map<String, Object> data) {
        ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
        try {
            // 非增量添加
            PdfStamper pdfStamper = new PdfStamper(reader, tempOutput);
            // 填充数据
            fillText(pdfStamper, data);
            // 如果为false那么生成的PDF文件还能编辑
            // （设置后文本域签名域消失，无法继续签名）
            pdfStamper.setFormFlattening(true);
            pdfStamper.flush();
            // close必须在tempOutput使用前，否则tempOutput中未写入数据
            IouCloseUtils.close(pdfStamper);
            return tempOutput.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("itext5 pdf 内容填充异常", e);
        } finally {
            IouCloseUtils.close(tempOutput);
        }
        return null;
    }

    /**
     * 填充pdf文本域内容
     * 
     * 填充后支持继续更新及签名
     *
     *
     * @param reader
     *            pdfReader
     * @param data
     *            填充数据
     * @return 填充后字节
     */
    public static byte[] fillTextEnableSign(PdfReader reader, Map<String, Object> data) {
        ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
        try {
            // 增量添加，可以继续填充文本域和修改
            PdfStamper pdfStamper = new PdfStamper(reader, tempOutput, '\0', true);

            fillText(pdfStamper, data);
            pdfStamper.flush();
            // close必须在tempOutput使用前，否则tempOutput中未写入数据
            IouCloseUtils.close(pdfStamper);
            return tempOutput.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("itext5 pdf 内容填充异常", e);
        } finally {

            IouCloseUtils.close(tempOutput);
        }
        return null;
    }

    /**
     * 单纯的添加文本域数据
     * 
     * @param pdfStamper
     *            pdf打印器
     * @param data
     *            待填充数据
     */
    private static void fillText(PdfStamper pdfStamper, Map<String, Object> data)
        throws IOException, DocumentException {
        AcroFields acroFields = pdfStamper.getAcroFields();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value != null) {
                String valueStr = value.toString();
                // 填充数据,保留模板样式
                acroFields.setField(key, valueStr, true);
            }
        }
    }

    public static boolean sign(InputStream src, OutputStream dest, PrivateKey pk, Certificate[] chain,
        String signFieldName, byte[] images, String reason, String location) {

        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(src);
            stamper = PdfStamper.createSignature(reader, dest, '\0', null, true);
            sign(stamper, pk, chain, signFieldName, images, reason, location);
            return true;
        } catch (IOException | DocumentException | GeneralSecurityException e) {
            log.error("pdf签章异常", e);
        } finally {
            IouCloseUtils.close(stamper);
            IouCloseUtils.close(reader);
        }
        return false;
    }

    /**
     * 签署文件
     * 
     * @param stamper
     *            pdf打印器
     * @param pk
     *            签名私钥
     * @param chain
     *            证书
     * @param signFieldName
     *            签名域
     * @param images
     *            签名图片
     * @param reason
     *            原因
     * @param location
     *            地址
     */
    private static void sign(PdfStamper stamper, PrivateKey pk, Certificate[] chain, String signFieldName,
        byte[] images, String reason, String location) throws IOException, DocumentException, GeneralSecurityException {
        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        // 设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
        // 签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        // 四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
        appearance.setVisibleSignature(signFieldName);
        // 读取图章图片，这个image是itext包的image
        Image image = Image.getInstance(images);
        appearance.setSignatureGraphic(image);
        appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
        // 设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);


        // 这里的itext提供了2个用于签名的接口，可以自己实现
        // 摘要算法
        ExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
        // 调用itext签名方法完成pdf签章CryptoStandard.CMS 签名方式，建议采用这种
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0,
            MakeSignature.CryptoStandard.CMS);
    }

}
