
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LoginActivity extends BaseActivity implements LoginHelper,TextWatcher,View.OnClickListener {

    private EditText email;
    private EditText password;
    private Button email_sign_in_button;
    // private ProgressDialog mProgressDialog;
    private LoginController loginController;
    private SwitchCompat rememberPassword;
    private AlertDialog.Builder builder;
    private TextView forgotten_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLoginViews();

        App.get(this).getPreferenceManager().clearUserData(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initController() {
        loginController = new LoginController(this,this);
    }

    private void initializeLoginViews() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);
        rememberPassword = (SwitchCompat) findViewById(R.id.switch_remember_password);
        forgotten_view = (TextView)findViewById(R.id.forgotten_view);
        forgotten_view.setOnClickListener(this);
        email.addTextChangedListener(this);
        password.addTextChangedListener(this);
        setIntermediateProgress();
        builder = new AlertDialog.Builder(LoginActivity.this);
        email_sign_in_button.setOnClickListener(this);
        onInputValidation(false);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 106) {
                      email_sign_in_button.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void showErrorDialog(String message) {
        showMDialog(R.drawable.error_icon, "Oops",message, getResources().getString(R.string.try_again), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingIndicator(false);
            }
        },false);
    }

    public void setLoadingIndicator(boolean active) {
        if(active){
            email_sign_in_button.setClickable(false);
            rememberPassword.setClickable(false);
            showLoading("Just a moment..",true);
        }else if(!active){
            email_sign_in_button.setClickable(true);
            rememberPassword.setClickable(true);
            hideProgress();
        }
    }


    public void inValidEmail() {
        showMDialog(R.drawable.error_icon, "Oops", getString(R.string.error_invalid_email), getResources().getString(R.string.try_again), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingIndicator(false);
            }
        },false);
    }

    public void emptyEmail() {
        showMDialog(R.drawable.error_icon, "Oops", getString(R.string.error_invalid_email), getResources().getString(R.string.try_again), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingIndicator(false);
            }
        },false);
    }

    public void inValidPassword() {
        showMDialog(R.drawable.error_icon, "Oops", getString(R.string.error_invalid_password), getResources().getString(R.string.try_again), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingIndicator(false);
            }
        },false);
    }

    @Override
    public void noInternetConnection() {
        showMDialog(R.drawable.error_icon, "Oops", getString(R.string.no_connection), "Try Again", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_sign_in_button.setClickable(true);
                rememberPassword.setClickable(true);
                hideProgress();
            }
        },false);

    }
    public void emptyPassword() {
        showMDialog(R.drawable.error_icon, "Oops", getString(R.string.error_invalid_password), getResources().getString(R.string.try_again), "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingIndicator(false);
            }
        },false);
    }

    public void loginSucess(UserToken userToken,Parker parker) {
        setLoadingIndicator(false);
        if(parker != null && !parker.getParkerStatus().equals("ENABLED")){
            showMDialog(R.drawable.error_icon, "Oops",parker.getParkerStatus(), getResources().getString(R.string.try_again), "", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLoadingIndicator(false);
                }
            },false);
            return;
        }

       if(userToken.getUser().getForceChangePassword()){
            Intent intent = new Intent(getContext(),ForceChangePassActivity.class);
            if(rememberPassword.isChecked()){
                intent.putExtra(Constants.REMEMBERME,true);
            }else {
                intent.putExtra(Constants.REMEMBERME,false);
            }
            intent.putExtra(Constants.EMAILID,email.getText().toString());
            intent.putExtra(Constants.PASSWORD,password.getText().toString());
            intent.putExtra(Constants.TOKENS,userToken.getToken());
            intent.putExtra(Constants.PARKERCODE,userToken.getUser().getUserTypeCode());
            openIntent(intent,0,0,true);
            return;
        }

        if(rememberPassword.isChecked()){
            Intent intent = new Intent(getContext(),PinSetupActivity.class);
            intent.putExtra(Constants.EMAILID,email.getText().toString());
            intent.putExtra(Constants.PASSWORD,password.getText().toString());
            intent.putExtra(Constants.TOKENS,userToken.getToken());
            intent.putExtra(Constants.PARKERCODE,userToken.getUser().getUserTypeCode());
            intent.putExtra(Constants.USERNAME,parker.getContact().getFirstName());
            openIntent(intent,0,0,true);
            return;
        }

         App.get(this).getPreferenceManager().disablePinSetUp();
         App.get(this).getPreferenceManager().storeValue(Constants.TOKENS,userToken.getToken());
         App.get(this).getPreferenceManager().storeValue(Constants.PARKERCODE,userToken.getUser().getUserTypeCode());
         App.get(this).getPreferenceManager().storeValue(Constants.USERNAME,parker.getContact().getFirstName());

         Intent intent = new Intent(getContext(), MainActivity.class);
         openIntent(intent,0,0,false);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        loginController.validateUserInputs(email.getText().toString().trim(),password.getText().toString().trim());
    }

    @Override
    public void onInputValidation(boolean valid){

        if(valid){
            email_sign_in_button.setBackgroundResource(R.drawable.button_style);
            email_sign_in_button.setOnClickListener(this);
        }else{
            email_sign_in_button.setBackgroundColor(Color.parseColor("#F7E27D"));
            email_sign_in_button.setOnClickListener(null);
        }

    }

    @Override
    public void onPasswordReset(User user) {
        if(user.getForceChangePassword()){
            showMDialog(R.drawable.thanks_icon,"Hi "+user.getUsername(),getResources().getString(R.string.forgot_success_message),null,null,null,false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   email_sign_in_button.setClickable(true);
                   rememberPassword.setClickable(true);
                   hideProgress();
                }

            },getResources().getInteger(R.integer.thanks_dialog_active_interval));
            return;
        }
        hideProgress();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.email_sign_in_button){
            hidekeyboard(view);
            setLoadingIndicator(true);
            loginController.doLogin(email.getText().toString(), password.getText().toString());
        }else if(view.getId() == R.id.forgotten_view){
            boolean validmail = loginController.isEmailValid(email.getText().toString(),false);
            if(!validmail){
                inValidEmail();
                return;
            }
            loginController.resetPassword(email.getText().toString());
        }
    }




}

