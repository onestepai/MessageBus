//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.onestep.os.client;

import com.onestep.os.error.OsError;
import com.onestep.os.exception.InternalException;
import com.onestep.os.utils.LoggerUtils;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestfulClient {
    @Autowired
    private RestTemplate restTemplate;

    public RestfulClient() {
    }

    protected <REQ, RES> RES post(String url, REQ requestMessage, Class<RES> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<REQ> requestEntity = new HttpEntity(requestMessage, headers);
            return this.restTemplate.postForObject(builder.toUriString(), requestEntity, responseType, new Object[0]);
        } catch (Exception var7) {
            String error = String.format("RestfulClient::post() Exception: Service [%s] with [%s] got error: %s", url, requestMessage.getClass().getSimpleName(), var7.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_HTTP_POST, error);
            throw new InternalException(error);
        }
    }

    protected <REQ, RES> RES get(String url, REQ requestMessage, Class<RES> responseType) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            return requestMessage == null ? this.restTemplate.getForObject(builder.toUriString(), responseType, new Object[0]) : this.restTemplate.getForObject(builder.toUriString(), responseType, new Object[]{requestMessage});
        } catch (Exception var6) {
            String error = String.format("RestfulClient::get() Exception: Service [%s] with [%s] got error: %s", url, requestMessage.getClass().getSimpleName(), var6.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_HTTP_GET, error);
            throw new InternalException(error);
        }
    }

    protected <REQ> void put(String url, REQ requestMessage) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            this.restTemplate.put(builder.toUriString(), requestMessage, new Object[0]);
        } catch (Exception var5) {
            String error = String.format("RestfulClient::put() Exception: Service [%s] with [%s] got error: %s", url, requestMessage.getClass().getSimpleName(), var5.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_HTTP_PUT, error);
            throw new InternalException(error);
        }
    }

    protected int upLoadFileByFile(String singnedurl, byte[] file) {
        boolean var3 = false;

        try {
            URL url = new URL(singnedurl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(file, 0, file.length);
            out.close();
            int responseCode = connection.getResponseCode();
            System.out.println("Service returned response code " + responseCode);
            return responseCode;
        } catch (Exception var7) {
            String error = String.format("RestfulClient::upLoadFileByFile() Exception: Singnedurl [%s] got error: %s", singnedurl, var7.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_UPLOAD_FILE_BY_FILE, error);
            throw new InternalException(error);
        }
    }

    protected int syncFile(String sourceFileUrl, String destinationFileUrl) {
        try {
            URL sourceUrl = new URL(sourceFileUrl);
            URLConnection sourceConn = sourceUrl.openConnection();
            InputStream inStream = sourceConn.getInputStream();
            URL destinationUrl = new URL(destinationFileUrl);
            HttpURLConnection destinationConn = (HttpURLConnection)destinationUrl.openConnection();
            destinationConn.setDoOutput(true);
            destinationConn.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(destinationConn.getOutputStream());
            byte[] buffer = new byte[1024];
            boolean var10 = false;

            int byteRead;
            while((byteRead = inStream.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }

            out.close();
            int responseCode = destinationConn.getResponseCode();
            return responseCode;
        } catch (Exception var12) {
            String error = String.format("RestfulClient::syncFile() Exception: Source [%s] Destination [%s] got error: %s", sourceFileUrl, destinationFileUrl.getClass().getSimpleName(), var12.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_SYNC_FILE, error);
            throw new InternalException(error);
        }
    }

    protected int syncFile(InputStream inStream, String destinationFileUrl) {
        try {
            URL destinationUrl = new URL(destinationFileUrl);
            HttpURLConnection destinationConn = (HttpURLConnection)destinationUrl.openConnection();
            destinationConn.setDoOutput(true);
            destinationConn.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(destinationConn.getOutputStream());
            byte[] buffer = new byte[1024];
            boolean var7 = false;

            int byteRead;
            while((byteRead = inStream.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }

            out.close();
            int responseCode = destinationConn.getResponseCode();
            return responseCode;
        } catch (Exception var9) {
            String error = String.format("RestfulClient::syncFile() Exception: error: %s", var9.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_SYNC_FILE, error);
            throw new InternalException(error);
        }
    }

    protected int syncContentString(String content, String destinationFileUrl) {
        try {
            URL destinationUrl = new URL(destinationFileUrl);
            HttpURLConnection destinationConn = (HttpURLConnection)destinationUrl.openConnection();
            destinationConn.setDoOutput(true);
            destinationConn.setRequestMethod("PUT");
            DataOutputStream out = new DataOutputStream(destinationConn.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            osw.write(new String(new byte[]{-17, -69, -65}));
            osw.write(content);
            osw.flush();
            osw.close();
            int responseCode = destinationConn.getResponseCode();
            return responseCode;
        } catch (Exception var8) {
            String error = String.format("RestfulClient::syncFile() Exception: error: %s", var8.getMessage());
            LoggerUtils.error(OsError.OS_FAILURE_SYNC_CONTENT_STRING, error);
            throw new InternalException(error);
        }
    }
}
