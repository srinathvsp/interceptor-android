public interface AuthServiceProvider {

    @POST("/login")
    Call<UserToken> login(@Body UserCredentials userCredentials);
}
