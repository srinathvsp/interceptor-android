public final class ApiDataSource implements IDataSource {

    private static ApiDataSource remoteDataSource = null;
    private AuthService authService = null;

    private Call<UserToken> loginService = null;

    private ApiDataSource(Context context) {
        retrofitInitialization(context);
    }
     public static ApiDataSource getInstance(Context context) {

        if (remoteDataSource == null) {
            remoteDataSource = new ApiDataSource(context);
        }
        return remoteDataSource;

    }
     private void retrofitInitialization(Context context) {
        authService = new AuthService(context);
    }

     @Override
    public void doLogin(@NonNull UserCredentials userCredentials, @NonNull final DataOnLoadCallback<UserToken> callback) {

        if(loginService != null && !loginService.isExecuted()){
            loginService.cancel();
        }
        loginService = authService.login(userCredentials);
        loginService.enqueue(new Callback<UserToken>() {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response) {
                if (response.isSuccessful()) {
                    //Log.e("response",".."+response.body());
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });

    }

}
