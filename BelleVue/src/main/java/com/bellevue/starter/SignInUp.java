/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.bellevue.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseAnalytics;


public class SignInUp extends AppCompatActivity {

    private ViewGroup mContainerView;
    private ViewGroup newView;

    private Button parseLoginButton;
    private Button parseSignupButton;
    private Button parseSignupConfirmButton;
    private Button parseForgotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mContainerView              = (ViewGroup) findViewById(R.id.scene_container);

        parseLoginButton            = (Button) findViewById(R.id.parse_login_button);
        parseSignupButton           = (Button) findViewById(R.id.parse_signup_button);
        parseSignupConfirmButton    = (Button) findViewById(R.id.create_account);
        parseForgotButton           = (Button) findViewById(R.id.parse_login_help);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        /* ParseObject testObject = new ParseObject("AllezStp");
        testObject.put("login", "login_bar");
        testObject.saveInBackground(); */

        TextView logo = (TextView) findViewById(R.id.app_logo);
        logo.setTypeface(com.bellevue.starter.CustomFontsLoader.getTypeface(this, com.bellevue.starter.CustomFontsLoader.Allura));

        parseLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bellevue = new Intent(getApplicationContext(), BelleVue.class);
                startActivity(bellevue);
                finish();
            }
        });

        parseSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parseLoginButton.setVisibility(View.GONE);
                parseSignupButton.setVisibility(View.GONE);
                parseForgotButton.setVisibility(View.GONE);

                newView = (ViewGroup) LayoutInflater.from(SignInUp.this).inflate(
                        R.layout.signup_content_form, mContainerView, false);

                // Because mContainerView has android:animateLayoutChanges set to true,
                // adding this view is automatically animated.
                mContainerView.addView(newView, 0);
            }
        });

    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        ((ViewGroup) newView.getParent()).removeView(newView);

        parseLoginButton.setVisibility(View.VISIBLE);
        parseSignupButton.setVisibility(View.VISIBLE);
        parseForgotButton.setVisibility(View.VISIBLE);

    }
}
