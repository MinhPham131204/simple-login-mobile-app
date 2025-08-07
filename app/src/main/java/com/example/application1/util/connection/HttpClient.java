package com.example.application1.util.connection;

import java.io.IOException;

public interface HttpClient {
    boolean request(String url, String jsonBody) throws IOException;
}