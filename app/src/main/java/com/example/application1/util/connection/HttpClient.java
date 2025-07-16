package com.example.application1.util.connection;

import java.io.IOException;

public interface HttpClient {
    String request(String url, String jsonBody) throws IOException;
}