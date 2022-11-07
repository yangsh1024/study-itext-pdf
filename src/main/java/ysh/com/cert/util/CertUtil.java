package ysh.com.cert.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

public class CertUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成密钥对
     * @return
     * @throws NoSuchAlgorithmException
     */

    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        rsa.initialize(1024);
        return rsa.generateKeyPair();
    }

    /**
     * 生成证书
     * @param userName
     * @param publicKey
     * @param privKey
     * @return
     * @throws OperatorCreationException
     * @throws CertificateException
     * @throws IOException
     */

    public static Certificate generateV3(String userName, PublicKey publicKey, PrivateKey privKey)
            throws OperatorCreationException, CertificateException, IOException {
        // 随便写的证书内容
        String issuer = "C=CN,ST=SH,L=SH,O=Skybility,OU=Cloudbility,CN=" + userName + ",E=email@e.com";
        Date notBefore = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        Date notAfter = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32);
        BigInteger serial = BigInteger.probablePrime(256, new Random());
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), serial, notBefore, notAfter, new X500Name(issuer), publicKey);
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA")
                .setProvider("BC").build(privKey);
        X509CertificateHolder holder = builder.build(sigGen);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
        is1.close();
        return theCert;
    }

}
