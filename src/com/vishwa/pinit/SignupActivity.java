/*
 * Copyright 2013 Vishwa Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in the 'assets' directory of this 
 * application or at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vishwa.pinit;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignupActivity extends Activity {

    public static final int REQUEST_CODE_PHOTO_SELECT = 101;

    private ImageView mPhotoImageView;
    private Button mProfilePhotoButton;
    private EditText mUsernameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private Button mConfirmSignupButton;
    private Button mCancelButton;
    private ProgressBar mProgressBar;

    private boolean mIsDefaultPhoto = true;
    private String mUsername;

    private Bitmap mProfilePhotoThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_signup);

        mPhotoImageView = (ImageView) findViewById(R.id.signup_photo);
        mProfilePhotoButton = (Button) findViewById(R.id.signup_photo_button);
        mUsernameField = (EditText) findViewById(R.id.signup_username_field);
        mEmailField = (EditText) findViewById(R.id.signup_email_field);
        mPasswordField = (EditText) findViewById(R.id.signup_password_field);
        mConfirmPasswordField = (EditText) findViewById(R.id.signup_confirm_password_field);
        mConfirmSignupButton = (Button) findViewById(R.id.signup_confirm_signup_button);
        mCancelButton = (Button) findViewById(R.id.signup_cancel_button);
        mProgressBar = (ProgressBar) findViewById(R.id.signup_progressBar);

        mProgressBar.setVisibility(View.INVISIBLE);
        
        mProfilePhotoThumbnail = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeResource(getResources(), R.drawable.default_image), 100, 100);

        mProfilePhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECT);
            }
        });

        mConfirmSignupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(mUsernameField.getText().toString());
                Pattern emailPattern = Patterns.EMAIL_ADDRESS;
                if(isEmpty(mUsernameField) 
                        || isEmpty(mEmailField)
                        || isEmpty(mPasswordField) 
                        || isEmpty(mConfirmPasswordField)) {
                    PinItUtils.createAlert("You've missed something!", 
                            "You've left one of the fields empty.", 
                            SignupActivity.this);
                }
                else if(matcher.find()) {
                    PinItUtils.createAlert("Username is invalid", 
                            "Usernames cannot contain spaces", 
                            SignupActivity.this);
                }
                else if(!emailPattern.matcher(mEmailField.getText().toString()).matches()) {
                    PinItUtils.createAlert("The email is invalid", 
                            "Please enter a correctly formatted email address", 
                            SignupActivity.this);
                }
                else if(mPasswordField.getText().length() < 6) {
                    PinItUtils.createAlert("Your password is too short!", 
                            "The password must be 6 characters or more", 
                            SignupActivity.this);
                }
                else if(!mPasswordField.getText().toString().equals(
                        mConfirmPasswordField.getText().toString())) {
                    PinItUtils.createAlert("Your passwords don't match!", 
                            "Please re-type the passwords", 
                            SignupActivity.this);
                }
                else {

                    mProgressBar.setVisibility(View.VISIBLE);

                    mUsername = mUsernameField.getText().toString();

                    final ParseUser user = new ParseUser();
                    user.setUsername(mUsernameField.getText().toString());
                    user.setEmail(mEmailField.getText().toString());
                    user.setPassword(mPasswordField.getText().toString());
                    user.put("isDefaultPhoto", mIsDefaultPhoto);

                    if(mIsDefaultPhoto) {
                        signupNewUser(user);
                    }
                    else {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        mProfilePhotoThumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                        byte[] photoBytes = byteStream.toByteArray();

                        CachePhotoTask cachePhotoTask = new CachePhotoTask();
                        cachePhotoTask.execute();

                        final ParseFile userPhotoThumbnail = 
                                new ParseFile("photoThumbnail.png", photoBytes);

                        userPhotoThumbnail.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    user.put("profilePhotoThumbnail", userPhotoThumbnail);
                                    signupNewUser(user);
                                }
                                else {
                                    String error = e.getMessage().substring(0, 1).toUpperCase( )+ 
                                            e.getMessage().substring(1);
                                    PinItUtils.createAlert("Sorry, we couldn't save this photo", 
                                            error, SignupActivity.this);
                                }
                            }
                        });
                    }

                }

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private boolean isEmpty(EditText textField) {
        return textField.getText().toString().trim().isEmpty();
    }

    private void signupNewUser(ParseUser user) {

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {

                mProgressBar.setVisibility(View.INVISIBLE);

                if(e == null) {
                    ((PinItApplication) getApplication()).setHasUserLoggedInSuccesfully(true);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    String error = e.getMessage().substring(0, 1).toUpperCase()+ 
                            e.getMessage().substring(1);

                    PinItUtils.createAlert("Sign up failed", error, SignupActivity.this);
                }
            }
        });
    }

    class CachePhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String filename = mUsername + ".png";

            if( mProfilePhotoThumbnail != null) {
                try {
                    FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    mProfilePhotoThumbnail.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                }
                catch (IOException e) {
                    //We can fail silently here because this was simply a cache update, the app is
                    //built to be resilient to cache misses and fetch the data from Parse when that happens
                }
            }
            return null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        super.onActivityResult(requestCode, resultCode, data);

        Uri photoUri;

        switch (requestCode) {
        case REQUEST_CODE_PHOTO_SELECT:
            if (resultCode == Activity.RESULT_OK) {
                mIsDefaultPhoto = false;
                photoUri = data.getData();

                String absolutePath = 
                        PinItUtils.getAbsolutePathFromUri(getApplicationContext(), photoUri);
                mProfilePhotoThumbnail = 
                        PinItUtils.decodeSampledBitmapFromFilePath(absolutePath, 100, 100);
                mProfilePhotoThumbnail = ThumbnailUtils.extractThumbnail(mProfilePhotoThumbnail, 100, 100);
                Matrix matrix = 
                        PinItUtils.getRotationMatrixForImage(getApplicationContext(), photoUri);

                mPhotoImageView.setAdjustViewBounds(true);

                int profilePhotoThumbnailWidth = mProfilePhotoThumbnail.getWidth();
                int profilePhotoThumbnailHeight = mProfilePhotoThumbnail.getHeight();
                mProfilePhotoThumbnail = Bitmap.createBitmap(mProfilePhotoThumbnail, 0, 0, 
                        profilePhotoThumbnailWidth, profilePhotoThumbnailHeight, matrix, true);

                mPhotoImageView.setImageBitmap(mProfilePhotoThumbnail);
            }
            break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mProfilePhotoThumbnail != null) {
            mProfilePhotoThumbnail.recycle();
            mProfilePhotoThumbnail = null;  
        }
        super.onDestroy();
    }


}
