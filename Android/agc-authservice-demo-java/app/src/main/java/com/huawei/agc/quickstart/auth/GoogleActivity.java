/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.agc.quickstart.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.GoogleAuthProvider;

public class GoogleActivity extends ThirdBaseActivity {
    private static final int SIGN_CODE = 9901;
    private static final int LINK_CODE = 9902;
    private GoogleSignInClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions options =
            new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .build();
        client = GoogleSignIn.getClient(this, options);
    }


    public void login() {
        startActivityForResult(client.getSignInIntent(), SIGN_CODE);
    }

    @Override
    public void link() {
        startActivityForResult(client.getSignInIntent(), LINK_CODE);
    }

    @Override
    public void unlink() {
        if (AGConnectAuth.getInstance().getCurrentUser() != null) {
            AGConnectAuth.getInstance().getCurrentUser().unlink(AGConnectAuthCredential.Google_Provider);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_CODE) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(googleSignInAccount -> {
                    AGConnectAuthCredential credential =
                        GoogleAuthProvider.credentialWithToken(googleSignInAccount.getIdToken());
                    auth.signIn(credential)
                        .addOnSuccessListener(signInResult -> loginSuccess())
                        .addOnFailureListener(e -> showToast(e.getMessage()));
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
        } else if (requestCode == LINK_CODE) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(googleSignInAccount -> {
                AGConnectAuthCredential credential = GoogleAuthProvider.credentialWithToken(googleSignInAccount.getIdToken());
                if (auth.getCurrentUser() != null) {
                    auth.getCurrentUser().link(credential).addOnSuccessListener(signInResult -> {
                        showToast("link success");
                    }).addOnFailureListener(e -> {
                        showToast(e.getMessage());
                    });
                }
            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
            });
        }
    }
}
