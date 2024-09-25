package com.abcd.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.CallbackManager;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;


import org.json.JSONException;
import org.json.JSONObject;


public class Main2Activity extends AppCompatActivity {
    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView txt_name, txt_email;
    private CallbackManager callBackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);
        loginButton = findViewById(R.id.login_button);
        txt_name = findViewById(R.id.profile_name);
        txt_email = findViewById(R.id.profile_email);
        circleImageView = findViewById(R.id.profile_pic);
        callBackManager = CallbackManager.Factory.create();
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));
        checkLoginStatus();



        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        callBackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                txt_name.setText("");
                txt_email.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(Main2Activity.this,"User Logged Out",Toast.LENGTH_LONG).show();
            }
            else
                loaduserProfile(currentAccessToken);
        }
    };
    private void loaduserProfile(AccessToken newAccessToken){
        GraphRequest request=GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    String first_name= jsonObject.getString("first_name");
                    String last_name= jsonObject.getString("last_name");
                    String email= jsonObject.getString("email");
                    String id= jsonObject.getString("id");

                    String image_url="https://graph.facebook.com/"+id+"/picture?type=normal";

                    txt_email.setText(email);
                    txt_name.setText(first_name+" "+last_name);
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(Main2Activity.this).load(image_url).into(circleImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }
    private void checkLoginStatus(){
        if(AccessToken.getCurrentAccessToken()!=null){
            loaduserProfile(AccessToken.getCurrentAccessToken());
        }
    }}

