import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
public class LoginController extends Controllers {

    private LoginHelper mLoginActivity;
    private Context mContext;

    public LoginController(LoginHelper loginActivity,Context context) {
        super(context);
        mContext = context;
        this.mLoginActivity = loginActivity;
    }

    public void doLogin(@NonNull String email, @NonNull final String password) {

        boolean email_valid = isEmailValid(email,true);
        boolean password_valid = false;

        if(email_valid)
         password_valid = isPasswordValid(password,true);

        if (email_valid && password_valid) {
            //mLoginActivity.setLoadingIndicator(true);
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUsername(email);
            userCredentials.setPassword(password);
            getRemoteInstance().doLogin(userCredentials, new IDataSource.DataOnLoadCallback<UserToken>() {
                @Override
                public void onSuccess(UserToken userToken) {
                    //for testing parking session
                    //mLoginActivity.setLoadingIndicator(false);
                    if(userToken.getUser().getForceChangePassword())
                        mLoginActivity.loginSucess(userToken,null);
                     else
                          getParkerDetails(userToken);
                }

                @Override
                public void onDataNotAvailable() {
                    mLoginActivity.setLoadingIndicator(false);
                }

                @Override
                public void onError(String message) {
                    try {
                        if(message.equals(mContext.getResources().getString(R.string.internet_exception))){
                            mLoginActivity.noInternetConnection();
                            return;
                        }
                        JSONObject jsonErrorObject = new JSONObject(message);
                        String msg = jsonErrorObject.getString("message");
                        if(msg.equals(mContext.getResources().getString(R.string.message_incorrect_username))){
                            mLoginActivity.showErrorDialog(mContext.getString(R.string.no_user));
                        }else if(msg.equals(mContext.getResources().getString(R.string.message_incorrect_password))){
                            mLoginActivity.showErrorDialog(mContext.getString(R.string.incorrect_password));
                        }

                    } catch (JSONException e) {
                        mLoginActivity.showErrorDialog(message);
                        Log.e("message",message);
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
