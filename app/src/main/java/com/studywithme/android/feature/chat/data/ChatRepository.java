package com.studywithme.android.feature.chat.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.core.network.StompChatClient;
import com.studywithme.android.di.NetworkModule;

public class ChatRepository {

    private final ChatApi chatApi;
    private final StompChatClient stompChatClient;

    public ChatRepository(@NonNull Context context) {
        this.chatApi = NetworkModule.createService(context, ChatApi.class);
        this.stompChatClient = NetworkModule.provideStompChatClient(context);
    }

    @NonNull
    public ChatApi getApi() {
        return chatApi;
    }

    @NonNull
    public StompChatClient getStompChatClient() {
        return stompChatClient;
    }
}
