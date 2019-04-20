package chatty;

import sun.plugin2.message.JavaObjectOpMessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
import javax.net.ssl.*;
import javax.swing.*;

import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class HTTPURLConnector {
    static SSLContext trustAllSslContext;

    public String send(String... strings) {
        try {
            URL url = new URL(strings[1]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(strings[0]);
            int i = strings.length - 2;
            int a = 2;
            //Log.d("HTTPConnector", "DEBUG: HEADER");
            String header = "";
            while (i > 0) {

                conn.addRequestProperty(strings[a], strings[a+1]);
                header += strings[a] + " - " + strings[a+1] + "\n";
                a = a+2;
                i = i-2;
            }
            JOptionPane.showMessageDialog(null, header, "HTTP Header", 1);
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            /*
             * This is very bad practice and should NOT be used in production.
             */
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            {
                try {
                    trustAllSslContext = SSLContext.getInstance("SSL");
                    trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    throw new RuntimeException(e);
                }
            }
            SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();
            conn.setSSLSocketFactory(trustAllSslSocketFactory);
            conn.setHostnameVerifier(verifier);

            String result = null;
            StringBuffer sb = new StringBuffer();
            InputStream is = null;

            try {
                is = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                    //Log.d("HTTPConnector", "DEBUG: " + inputLine);
                }
                result = sb.toString();
                JOptionPane.showMessageDialog(null, result, "RESULT", 1);
            }
            catch (Exception e) {
                //Log.i("HTTPConnector", "DEBUG: Error reading InputStream; " + e.getMessage());
                JOptionPane.showMessageDialog(null, e.getMessage() + "\n", "Result", 0);
                result = null;
                //return "ERROR";
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        //Log.i("HTTPConnector", "DEBUG: Error closing InputStream");
                    }
                }
            }

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
