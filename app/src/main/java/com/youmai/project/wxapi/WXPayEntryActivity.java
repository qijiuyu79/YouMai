package com.youmai.project.wxapi;


import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.http.HttpConstant;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	api = WXAPIFactory.createWXAPI(this, HttpConstant.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX ) {
			final Intent intent = new Intent("PAY_ACTION");
			if (resp.errCode==0) {
				intent.putExtra("type",1);
				showMsg(getString(R.string.payment_success));
			}else if(resp.errCode==-1) {
				intent.putExtra("type",0);
				showMsg(getString(R.string.payment_failed));
			}else{
				showMsg(getString(R.string.payment_has_been_cancelled));
			}
			sendBroadcast(intent);
			finish();
		}
	}
}