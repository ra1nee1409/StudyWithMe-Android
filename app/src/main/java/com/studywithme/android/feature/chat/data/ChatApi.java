package com.studywithme.android.feature.chat.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit endpoints for /api/chat/** (+ STOMP /ws-chat)
 */
public interface ChatApi {

    @GET("api/chat/conversations")
    Call<ApiResponse<List<Map<String, Object>>>> getConversations();

    @GET("api/chat/conversations/{conversationId}/messages")
    Call<ApiResponse<List<Map<String, Object>>>> getMessages(
            @Path("conversationId") String conversationId
    );
}
