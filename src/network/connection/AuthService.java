import android.content.Context;

import retrofit2.Call;
import retrofit2.http.Path;
public class AuthService implements AuthClientInterface {

    private final AuthServiceProvider provider;

    public AuthService(Context context){
        super(context);

        provider = buildRetrofit(ADVAM_HOST_ABSOLUTE).create(AuthServiceProvider.class);

    }

    @Override
    public Call<UserToken> login(UserCredentials userCredentials) {
        return provider.login(userCredentials);
    }
}
