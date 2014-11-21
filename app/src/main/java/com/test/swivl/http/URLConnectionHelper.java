package com.test.swivl.http;

import android.util.Log;

import com.test.swivl.main.BeanCursorAdapter;
import com.test.swivl.main.MainActivity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class URLConnectionHelper {
    public static final int CONNECTION_TIME_OUT = 10000;
    public static final int SOCKET_TIME_OUT = 5000;
    public static final int END_OF_STREAM = -1;
    public static final int DEFAULT_BUFFER_CAPACITY = 1024;
    public static final String ACCEPT_CHARSET_HEADER = "Accept-Charset";
    public static final String GITHUB_API_URL = "https://api.github.com/users";
    public static final String AVATARS_GITHUB_URL = "https://avatars.githubusercontent.com/u/";


    public static ServerResponse executeRequestToAPI() throws IOException {
        HttpsURLConnection httpsURLConnection = establishConnection(GITHUB_API_URL);
        ServerResponse response = new ServerResponse();
        if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            response.setInputStream(convertStreamToString(httpsURLConnection.getInputStream()));
        } else {
            response.setErrorStream(convertStreamToString(httpsURLConnection.getErrorStream()));
        }
        return response;
    }

    private static HttpsURLConnection establishConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestMethod(HttpGet.METHOD_NAME);
        httpsURLConnection.setRequestProperty(ACCEPT_CHARSET_HEADER, HTTP.UTF_8);
        httpsURLConnection.setConnectTimeout(CONNECTION_TIME_OUT);
        httpsURLConnection.setReadTimeout(SOCKET_TIME_OUT);
        httpsURLConnection.connect();

        return httpsURLConnection;
    }

    public static String convertStreamToString(InputStream inputStream) {
        if (inputStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[DEFAULT_BUFFER_CAPACITY];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, HTTP.UTF_8));
                int n;
                while ((n = reader.read(buffer)) != END_OF_STREAM) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                Log.w(URLConnectionHelper.class.getSimpleName(), "Unsupported encoding for parsing stream");
            } catch (IOException e) {
                Log.w(URLConnectionHelper.class.getSimpleName(), "I/O issue during parsing stream");
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.w(URLConnectionHelper.class.getSimpleName(), "I/O issue during closing stream");
                }
            }

            return writer.toString();
        } else {
            return "";
        }
    }

    public static byte[] downloadAvatarById(int id) {
        byte[] response;

        try {
            InputStream inputStream = establishConnection(AVATARS_GITHUB_URL + id).getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(DEFAULT_BUFFER_CAPACITY);
            int currentByte;
            while ((currentByte = bufferedInputStream.read()) != END_OF_STREAM) {
                byteArrayBuffer.append((byte) currentByte);
            }
            response = byteArrayBuffer.toByteArray();
        } catch (Exception e) {
            Log.w(BeanCursorAdapter.class.getSimpleName(), "Error while loading photo", e);
            response = null;
        }

        return response;
    }
}