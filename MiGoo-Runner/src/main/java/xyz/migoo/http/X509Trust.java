package xyz.migoo.http;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 搬运 http://blog.csdn.net/just_young/article/details/47664315
* @author xiaomi
 */
public class X509Trust implements X509TrustManager {

    private Certificate serverCer = null;
    private Certificate clientCer = null;

    public X509Trust(File serverCert, File clientCert) {
        if (null != serverCert){
            this.serverCer = set(serverCert);
        }
        if (null != clientCert){
            this.clientCer = set(clientCert);
        }
    }

    private Certificate set(File certFile){
        Certificate serverOrClient = null;
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(certFile))){
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                serverOrClient = cf.generateCertificate(bis);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return serverOrClient;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (clientCer == null ){
            return;
        }
        for (X509Certificate cert : chain) {
            if (cert.toString().equals(this.clientCer.toString())) {
                return;
            }
        }
        throw new CertificateException("certificate is illegal");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (serverCer == null ){
            return;
        }
        for (X509Certificate cert : chain) {
            if (cert.toString().equals(this.serverCer.toString())) {
                return;
            }
        }
        throw new CertificateException("certificate is illegal");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (serverCer == null && clientCer != null){
            return new X509Certificate[] {(X509Certificate) clientCer};
        }
        if (serverCer != null && clientCer == null){
            return new X509Certificate[] { (X509Certificate) serverCer};
        }
        return new X509Certificate[] {};
    }

}