---
name: retrofit-networking
description: "Expert guidance on setting up and using Retrofit by new-silvermoon for type-safe HTTP networking in Android. Covers service definitions, OkHttp configuration, converters, error handling, and both Java (Call, Callback, ExecutorService) and Kotlin integration."
license: Apache-2.0
metadata:
  author: new-silvermoon
  source: https://github.com/new-silvermoon/awesome-android-agent-skills
  category: concurrency-and-networking
---

# Retrofit Networking Skill (by new-silvermoon)

Expert guidance on setting up and using **Retrofit** and **OkHttp** for robust, type-safe HTTP networking in Android applications. Supports both **Java** (`Call<T>`, asynchronous `enqueue()`, `execute()` with background executors) and **Kotlin** (`suspend fun`, Coroutines, Flow).

> [!IMPORTANT]
> **Java + XML Project Directive:**
> When working in a Java project, define service endpoints returning `Call<T>`, use `GsonConverterFactory` or `MoshiConverterFactory`, and execute calls asynchronously via `Call.enqueue(new Callback<T>() { ... })` or synchronously via `execute()` inside background `ExecutorService` / Worker threads.

---

### 1. URL Manipulation
Retrofit allows dynamic URL updates through replacement blocks and query parameters.

* **Dynamic Paths**: Use `{name}` in relative URL with `@Path("name")`.
* **Query Parameters**: Use `@Query("key")` for individual parameters.
* **Dynamic Queries**: Use `@QueryMap Map<String, String>` for dynamic sets of parameters.

#### Java:
```java
public interface ApiService {
    @GET("group/{id}/users")
    Call<List<UserDto>> getGroupUsers(
        @Path("id") int groupId,
        @Query("sort") String sort,
        @QueryMap Map<String, String> options
    );
}
```

#### Kotlin:
```kotlin
interface ApiService {
    @GET("group/{id}/users")
    suspend fun getGroupUsers(
        @Path("id") groupId: Int,
        @Query("sort") sort: String?,
        @QueryMap options: Map<String, String> = emptyMap()
    ): List<UserDto>
}
```

---

### 2. Request Body & Form Data
* **@Body**: Serializes an object using the configured converter (JSON/Gson/Moshi).
* **@FormUrlEncoded**: Sends data as `application/x-www-form-urlencoded`. Use `@Field`.
* **@Multipart**: Sends data as `multipart/form-data`. Use `@Part` or `MultipartBody.Part`.

#### Java:
```java
public interface UserService {
    @POST("users/new")
    Call<UserDto> createUser(@Body UserDto user);

    @FormUrlEncoded
    @POST("user/edit")
    Call<UserDto> updateUser(
        @Field("first_name") String firstName,
        @Field("last_name") String lastName
    );

    @Multipart
    @PUT("user/photo")
    Call<UserDto> uploadPhoto(
        @Part("description") RequestBody description,
        @Part MultipartBody.Part photo
    );
}
```

---

### 3. Header Manipulation
* **Static Headers**: Use `@Headers("Header-Name: Value")`.
* **Dynamic Headers**: Use `@Header("Header-Name") String value`.
* **Header Maps**: Use `@HeaderMap Map<String, String> headers`.
* **Global Headers**: Use OkHttp `Interceptor`.

#### Java Auth Interceptor:
```java
public class AuthInterceptor implements Interceptor {
    private final TokenProvider tokenProvider;

    public AuthInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = tokenProvider.getToken();
        Request.Builder builder = chain.request().newBuilder();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
```

---

### 4. Client Setup (OkHttpClient + Retrofit)

#### Java ApiClient:
```java
public class ApiClient {
    private static final String BASE_URL = "https://api.example.com/";
    private static volatile ApiService apiService;

    public static ApiService getService(Context context) {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(BuildConfig.DEBUG 
                        ? HttpLoggingInterceptor.Level.BODY 
                        : HttpLoggingInterceptor.Level.NONE);

                    OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(new AuthInterceptor(TokenStorage.getInstance(context)))
                        .addInterceptor(logging)
                        .build();

                    Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }
}
```

---

### 5. Repository Error Handling & Execution

#### Java Asynchronous (`enqueue`):
```java
public void fetchItems(Callback<List<Item>> callback) {
    apiService.getItems().enqueue(new retrofit2.Callback<List<ItemDto>>() {
        @Override
        public void onResponse(@NonNull Call<List<ItemDto>> call, 
                               @NonNull retrofit2.Response<List<ItemDto>> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<Item> items = StreamSupport.stream(response.body().spliterator(), false)
                    .map(ItemMapper::toDomain)
                    .collect(Collectors.toList());
                callback.onSuccess(items);
            } else {
                callback.onError(new HttpException(response));
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<ItemDto>> call, @NonNull Throwable t) {
            callback.onError(new IOException("Network failure", t));
        }
    });
}
```

#### Java Synchronous (Off-Main-Thread with `ExecutorService`):
```java
public List<Item> getItemsSync() throws IOException {
    Response<List<ItemDto>> response = apiService.getItems().execute();
    if (response.isSuccessful() && response.body() != null) {
        return response.body().stream()
            .map(ItemMapper::toDomain)
            .collect(Collectors.toList());
    } else {
        throw new IOException("HTTP error code: " + response.code());
    }
}
```

---

### 6. Best Practices Checklist
- [ ] Define endpoints with `Call<T>` for Java or `suspend fun` for Kotlin.
- [ ] Never run network calls on the main/UI thread (`android.os.NetworkOnMainThreadException`).
- [ ] Configure OkHttpClient with sensible timeouts (connect, read, write).
- [ ] Add `HttpLoggingInterceptor` (enabled only in `BuildConfig.DEBUG`).
- [ ] Always validate `response.isSuccessful()` and handle null `response.body()`.
- [ ] Map API DTOs to Domain models in Repository layer to isolate changes in API response models from UI.
- [ ] Provide retry policies or meaningful error messages when handling `SocketTimeoutException` or `UnknownHostException`.
