package com.fallwater.androidutils2017.share;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.fallwater.androidutils2017.R;
import com.fallwater.androidutils2017.constant.RefferalConstant;
import com.fallwater.androidutils2017.eventbus.ReferralEvent;
import com.fallwater.utilslibrary.common.BaseActivity;
import com.fallwater.utilslibrary.utils.LoggerUtils;
import com.fallwater.utilslibrary.view.LineTitleView;
import com.fallwater.utilslibrary.view.PopupWindowDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author cy
 *         time 2017/11/03
 *         邀请码推广活动页面
 */
public class ReferralCodeActivity extends BaseActivity {

    private static final String TAG = "ReferralCodeActivity";

    @BindView(R.id.invitation_code_title)
    LineTitleView mInvitationCode;

    @BindView(R.id.promotion_tips_share_for_reward)
    TextView mTVTips;

    @BindView(R.id.coupon_image)
    ImageView mIVCoupon;

    @BindView(R.id.text_invitation_apply_reward)
    TextView mTVApplyReferralCode;

    CallbackManager mCallbackManager;

    /**
     * 分享中的优惠额度，从后台获取
     */
    private String mStrBonus;

    /**
     * 分享到Facebook等社交平台所用图片的url地址,从后台获取
     */
    private String mStrImageUrl;

    /**
     * 本人所对应的邀请码
     */
    private String referrerCode;

    private boolean isNeedUpload;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_referral_code;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitleNormal(R.string.title_referral_code);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initDataOnCreate() {
        mCallbackManager = CallbackManager.Factory.create();
        initReferrerCode();
        prepareShareInfo(false, 0);
        loadUserFriendStatus();
    }

    @Override
    protected void initDataOnStart() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() == requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                // facebook  请求权限回调
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @OnClick({R.id.share_other, R.id.text_invitation_apply_reward, R.id.share_facebook,
            R.id.share_messenger, R.id.share_whatsapp})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.share_other:
                prepareToShare(R.id.share_other);
                break;
            case R.id.text_invitation_apply_reward:
                break;
            case R.id.share_facebook:
                clickShareFaceBook(R.id.share_facebook);
                break;
            case R.id.share_messenger:
                prepareToShare(R.id.share_messenger);
                break;
            case R.id.share_whatsapp:
                prepareToShare(R.id.share_whatsapp);
                break;
            default:
                break;
        }
    }

    /**
     * //TODO:是否加载用户的好友列表状态,是否需要上传FB好友列表，目前不上传
     */
    private void loadUserFriendStatus() {
        isNeedUpload = false;
    }

    /**
     * 点击分享到facebook
     */
    private void clickShareFaceBook(final int id) {
        if (isNeedUpload) {
            // 还没有获取相关的权限信息，则请求获取
            LoginManager.getInstance()
                    .registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            //登录授权成功
                            LoggerUtils.d(TAG,
                                    "onVerifyComplete,loginResult:" + loginResult.toString());
                            requestUploadFriendList();
                            prepareToShare(id);
                        }

                        @Override
                        public void onCancel() {
                            LoggerUtils.d(TAG, "onCancel");
                            LoginManager.getInstance().logOut();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            LoggerUtils.d(TAG, "onError = " + error.toString());
                        }
                    });
            go2FaceBookAuth();
        } else {
            prepareToShare(id);
        }
    }

    /**
     * 请求上传好友列表
     */
    private void requestUploadFriendList() {
    }


    private void prepareToShare(int id) {
        if (!TextUtils.isEmpty(mStrBonus)) {
            showShareInfo(id);
        } else {
            prepareShareInfo(true, id);
        }
    }

    private void showShareInfo(int id) {
        LoggerUtils.d(TAG, "showShareInfo");
        switch (id) {
            case R.id.share_facebook:
                shareToFaceBook();
                break;
            case R.id.share_messenger:
                shareToMessenger();
                break;
            case R.id.share_whatsapp:
                shareToWhatsApp();
                break;
            case R.id.share_other:
                showShareMoreDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 去facebook授权
     */
    private void go2FaceBookAuth() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        List<String> permissionNeeds = Arrays.asList("public_profile",
                "user_friends");
        LoginManager.getInstance().logInWithReadPermissions(this, permissionNeeds);
    }

    private void initReferrerCode() {
    }

    private void showShareMoreDialog() {
        final PopupWindowDialog dialog = new PopupWindowDialog(this, R.style.alert_dialog);
        View view = getLayoutInflater().inflate(R.layout.view_popwindow_share, null);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.9f;
        lp.alpha = 0.95f;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(view);
        dialog.show();

        view.findViewById(R.id.share_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSms();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToEmail();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.image_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * type 代表类型比如facebook ，短信等
     * facebook = 1, messenger = 2, whatsapp = 3, email = 4, sms = 5;
     */
    public String getContentUrl(int type) {
        return "";
    }

    /**
     * 分享到Messenger
     */
    private void shareToMessenger() {
        shareLinkContent(this, getShareTitle(),
                getShareText(RefferalConstant.UTM_MESSENGER),
                getContentUrl(RefferalConstant.UTM_MESSENGER), mStrImageUrl);
    }

    /**
     * 分享到facebook
     * 安装应用直接跳转到应用
     * 未安装跳转到googleplay
     * pc上调转到asetku官网
     */
    private void shareToFaceBook() {
        LoggerUtils.d(TAG, "shareToFaceBook");
        try {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareDialog shareDialog = new ShareDialog(this);
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(
                                Uri.parse(getContentUrl(RefferalConstant.UTM_FACEBOOK)))
                        .setContentTitle(getShareTitle())
                        .setContentDescription(
                                getShareText(RefferalConstant.UTM_MESSENGER))
                        .setImageUrl(Uri.parse(mStrImageUrl))
                        .build();

                shareDialog.show(linkContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享图片到FB
     */
    private void shareToFBWithPic() {
        new DownloadImgTask().execute(mStrImageUrl);
    }

    private void postFB(Bitmap bm) {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bm).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareDialog dialog = new ShareDialog(this);
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            dialog.show(content);
        } else {
            Log.d("Activity", "you cannot share photos :(");
        }

    }

    private class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bm = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            postFB(result);
        }
    }

    /**
     * 分享到SMS
     */
    private void shareToSms() {
        String smsBody = getShareText(RefferalConstant.UTM_SMS);

        Uri uri = Uri.parse("sms:");
        Intent intent = new Intent();
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_TEXT, smsBody);
        intent.putExtra("sms_body", smsBody);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_SENDTO);
            String defaultSmsPackageName = Telephony.Sms
                    .getDefaultSmsPackage(getApplicationContext());
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 分享到Email
     */
    private void shareToEmail() {
        // 发送一封的email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType("text/html");
        //邮件主题
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getShareTitle());
        //邮件内容
        String emailBody = getShareText(RefferalConstant.UTM_EMAIL);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        startActivity(Intent.createChooser(emailIntent, null));
    }

    private void shareToWhatsApp() {
        shareByPackageName(this, getShareText(RefferalConstant.UTM_WHATSAPP),
                "com.whatsapp");
    }

    private void shareByPackageName(Context context, String shareStr, String packageName) {
        boolean isInstalledApp = isInstalledByPackageName(context, packageName);
        if (isInstalledApp) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getShareTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, shareStr);
            intent.setPackage(packageName);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, getString(R.string.share_app_not_install),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 分享链接内容
     */
    public void shareLinkContent(Activity activity, String title, String description, String uri,
            String imageUrl) {
        if (!isInstalledByPackageName(activity, "com.facebook.orca")) {
            Toast.makeText(activity, activity.getString(R.string.share_app_not_install),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (MessageDialog.canShow(ShareLinkContent.class)) {
            ShareContent shareContent = defaultLinkContent(title, description, uri, imageUrl)
                    .build();
            MessageDialog.show(activity, shareContent);
        }
    }

    /**
     * 默认的ShareLinkContent builder
     */
    private ShareLinkContent.Builder defaultLinkContent(String title, String description,
            String uri, String imageUrl) {
        ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
        builder.setContentDescription(description)
                .setContentTitle(title)
                .setContentUrl(Uri.parse(uri))
                .setImageUrl(Uri.parse(imageUrl));
        return builder;
    }

    public boolean isInstalledByPackageName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Context outContext = context
                    .createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            return outContext != null;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    private String getShareTitle() {
        String title = "";
        return title;
    }

    private String getShareText(int type) {
        String content = "";
        return content;
    }

    /**
     * 从后台获取奖励额度，然后分享
     *
     * @param immediatelyShare 是不是获取奖励信息后立即分享
     */
    private void prepareShareInfo(final boolean immediatelyShare, final int id) {
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ReferralCodeActivity.class);
        activity.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReferralEvent referralEvent) {
        if (referralEvent.isUsed) {
            mTVApplyReferralCode.setVisibility(View.GONE);
        }
    }
}