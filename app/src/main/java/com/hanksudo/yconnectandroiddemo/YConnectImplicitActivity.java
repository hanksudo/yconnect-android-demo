package com.hanksudo.yconnectandroiddemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import jp.co.yahoo.yconnect.YConnectImplicit;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;

public class YConnectImplicitActivity extends Activity {

    private final static String TAG = YConnectImplicitActivity.class.getSimpleName();

    // Client ID
    public final static String clientId = "YOUR_APPLICATION_ID";

    //1を指定した場合、同意キャンセル時にredirect_uri設定先へ遷移する
    public final static String BAIL = "1";

    //最大認証経過時間
    public final static String MAX_AGE = "3600";

    // カスタムURIスキーム
    public final static String customUriScheme = "yj-xxxxx://cb";

    public final static String YCONNECT_PREFERENCE_NAME = "yconnect";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.implicit);
        SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // YConnectインスタンス取得
        YConnectImplicit yconnect = YConnectImplicit.getInstance();

        // ログレベル設定（必要に応じてレベルを設定してください）
        //YConnectLogger.setLogLevel(YConnectLogger.DEBUG);

        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            /*************************************************
             Parse the Response Url and Save the Access Token.
             *************************************************/

            try {

                Log.i(TAG, "Get Response Url and parse it.");

                // stateの読み込み
                String state = sharedPreferences.getString("state", null);

                // response Url(Authorizationエンドポイントより受け取ったコールバックUrl)から各パラメータを抽出
                Uri uri = intent.getData();
                yconnect.parseAuthorizationResponse(uri, customUriScheme, state);
                // Access Token、ID Tokenを取得
                String accessTokenString = yconnect.getAccessToken();
                long expiration = yconnect.getAccessTokenExpiration();
                String idTokenString = yconnect.getIdToken();


                TextView accessToeknTV = (TextView) findViewById(R.id.access_token);
                accessToeknTV.setText(accessTokenString);
                TextView expirationTV = (TextView) findViewById(R.id.expiration);
                expirationTV.setText(Long.toString(expiration));
                TextView idTokenTV = (TextView) findViewById(R.id.id_token);
                idTokenTV.setText(idTokenString);

                // Access Tokenを保存
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("access_token", accessTokenString);
                editor.commit();

                // 別スレッド(AsynckTask)でID Tokenの検証、UserInfoエンドポイントにリクエスト
//                YConnectImplicitAsyncTask asyncTask = new YConnectImplicitAsyncTask(this, idTokenString);
//                asyncTask.execute("Verify ID Token and Request UserInfo.");

            } catch (AuthorizationException e) {
                Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
            } catch (Exception e) {
                Log.e(TAG, "error=" + e.getMessage());
            }

        } else {

            /********************************************************
             Request Authorization Endpoint for getting Access Token.
             ********************************************************/

            Log.i(TAG, "Request authorization.");

            // 各パラメーター初期化
            // リクエストとコールバック間の検証用のランダムな文字列を指定してください
            String state = "44GC44Ga44GrWeOCk+ODmuODreODmuODrShez4leKQ==";
            // リプレイアタック対策のランダムな文字列を指定してください
            String nonce = "KOOAjeODu8+J44O7KeOAjVlhaG9vISAo77yP44O7z4njg7sp77yPSkFQQU4=";
            String display = OIDCDisplay.TOUCH;
            String[] prompt = {OIDCPrompt.DEFAULT};
            String[] scope = {OIDCScope.OPENID, OIDCScope.PROFILE, OIDCScope.EMAIL, OIDCScope.ADDRESS};

            try {
                // state、nonceを保存
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("state", state);
                editor.putString("nonce", nonce);
                editor.commit();

            } catch (Exception e) {
                Log.e(TAG, "error=" + e.getMessage());
            }

            // 各パラメーターを設定
            yconnect.init(clientId, customUriScheme, state, display, prompt, scope, nonce, BAIL, MAX_AGE);
            // Authorizationエンドポイントにリクエスト
            // (ブラウザーを起動して同意画面を表示)
            yconnect.requestAuthorization(this);

        }

    }

}
