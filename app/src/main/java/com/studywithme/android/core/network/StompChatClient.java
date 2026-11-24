package com.studywithme.android.core.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.studywithme.android.BuildConfig;
import com.studywithme.android.core.storage.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Lightweight STOMP over WebSocket client for connecting to Spring Boot /ws-chat endpoint.
 */
public class StompChatClient {

    public interface Listener {
        void onConnected();
        void onMessage(@NonNull String destination, @NonNull String body);
        void onError(@NonNull Throwable throwable);
        void onDisconnected();
    }

    private final OkHttpClient okHttpClient;
    private final TokenManager tokenManager;
    @Nullable
    private WebSocket webSocket;

    public StompChatClient(@NonNull OkHttpClient okHttpClient, @NonNull TokenManager tokenManager) {
        this.okHttpClient = okHttpClient;
        this.tokenManager = tokenManager;
    }

    public void connect(@NonNull Listener listener) {
        disconnect();

        Request.Builder requestBuilder = new Request.Builder().url(BuildConfig.WS_CHAT_URL);
        String token = tokenManager.getAccessToken();
        if (token != null && !token.trim().isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        webSocket = okHttpClient.newWebSocket(requestBuilder.build(), new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                StringBuilder connectFrame = new StringBuilder("CONNECT\naccept-version:1.2\n");
                if (token != null && !token.trim().isEmpty()) {
                    connectFrame.append("Authorization:Bearer ").append(token).append("\n");
                }
                connectFrame.append("\n\u0000");
                ws.send(connectFrame.toString());
                listener.onConnected();
            }

            @Override
            public void onMessage(@NonNull WebSocket ws, @NonNull String text) {
                listener.onMessage("/ws-chat", text);
            }

            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                listener.onError(t);
            }

            @Override
            public void onClosed(@NonNull WebSocket ws, int code, @NonNull String reason) {
                listener.onDisconnected();
            }
        });
    }

    public void subscribe(@NonNull String id, @NonNull String destination) {
        if (webSocket == null) {
            return;
        }
        String frame = "SUBSCRIBE\nid:" + id + "\ndestination:" + destination + "\n\n\u0000";
        webSocket.send(frame);
    }

    public void send(@NonNull String destination, @NonNull String jsonPayload) {
        if (webSocket == null) {
            return;
        }
        String frame = "SEND\ndestination:" + destination
                + "\ncontent-type:application/json\n\n"
                + jsonPayload + "\u0000";
        webSocket.send(frame);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.send("DISCONNECT\n\n\u0000");
            webSocket.close(1000, "Client disconnected");
            webSocket = null;
        }
    }
}
