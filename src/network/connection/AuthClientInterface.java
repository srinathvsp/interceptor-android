import retrofit2.Call;
public interface AuthClientInterface {


    Call<UserToken> login(UserCredentials userCredentials);
}
