package ysh.com.pdf.util;

import java.io.IOException;
import java.io.OutputStream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import lombok.extern.slf4j.Slf4j;

/**
 * 关闭各种pdf相关对象
 * 
 * @author yangsh
 * @since 2021/5/8 4:48 下午
 */
@Slf4j
public class IouCloseUtils {

    public static void close(PdfDocument document) {
        if (document != null) {
            document.close();
        }
    }

    public static void close(PdfReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    public static void close(com.itextpdf.kernel.pdf.PdfReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("itext PdfReader 关闭异常", e);
            }
        }
    }

    public static void close(PdfStamper stamper) {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (DocumentException | IOException e) {
                log.error("stamper关闭异常", e);
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("输出流关闭异常", e);
            }
        }
    }
}
