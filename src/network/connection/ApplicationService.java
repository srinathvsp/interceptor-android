import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;
public class ApplicationService extends ApplicationServiceBase {

    private static final int MAX_AGE = 60; //1 minute
    private static final int MAX_STALE = 60 * 60 * 24 * 28; //tolerate 4-weeks
    private static final int CACHE_DIR_SIZE = 10 * 1024 * 1024; //

    private final Retrofit.Builder retrofitBuilder;
    private final ConnectionManager connectionManager;

    public ApplicationService(Context context){
        retrofitBuilder = getRetrofitBuilder();
        connectionManager = new ConnectionManager(context);
    }


    protected Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder()
                .client(myHttpClient())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public Retrofit buildRetrofit(String host) {
        return retrofitBuilder.baseUrl(host).build();
    }


    protected OkHttpClient myHttpClient(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
        builder.readTimeout(30,TimeUnit.SECONDS);
        builder.connectTimeout(10,TimeUnit.SECONDS);
        builder.addInterceptor(getMainInterceptor());

        return builder.build();
    }


    protected Interceptor getMainInterceptor() {
        return new GeneralInterceptor();
    }


    public class GeneralInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {

            boolean isOnline = connectionManager.isOnline();
            Request request = chain.request();
            String host = request.url().host();

            Request.Builder mRequestBuilder = request.newBuilder();
            mRequestBuilder.addHeader("x-api-key",API_KEY);
            mRequestBuilder.method(request.method(), request.body());

            if(!isOnline){
                connectionManager.showConnectionStatus(false);
                throw new NoConnectivityException();
            }

           /* try {
                Response response = chain.proceed(chain.request());
                String content = UtilityMethods.convertResponseToString(response);
                //Log.d(TAG, lastCalledMethodName + " - " + content);
                return response.newBuilder().body(ResponseBody.create(response.body().contentType(), content)).build();
            }*/
            /*catch (SocketTimeoutException exception) {
                exception.printStackTrace();
            }*/

            Request mRequest = mRequestBuilder.build();
            return chain.proceed(mRequest);

        }



    }

    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "NCP NO INTERNET CONNECTION";
        }
    }



}
