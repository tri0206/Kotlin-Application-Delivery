package com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Api;



import android.util.Log;

import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Constant.AppInfo;
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Helper.Helpers;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateOrder {

    private static class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;

        private CreateOrderData(String amount) throws Exception {
            long appTime = new Date().getTime();
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = "Android_Demo";
            AppTime = String.valueOf(appTime);
            Amount = amount;
            AppTransId = Helpers.getAppTransId();
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
            Description = "Merchant pay for order #" + Helpers.getAppTransId();
            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId,
                    this.AppTransId,
                    this.AppUser,
                    this.Amount,
                    this.AppTime,
                    this.EmbedData,
                    this.Items);

            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);
        }
        public String getAppTransId() {
            return this.AppTransId;
        }
    }

     public JSONObject createOrder(String amount) throws Exception {
        CreateOrderData input = new CreateOrderData(amount);
        String appTransId = input.getAppTransId();
        RequestBody formBody = new FormBody.Builder()
                .add("app_id", input.AppId)
                .add("app_user", input.AppUser)
                .add("app_time", input.AppTime)
                .add("amount", input.Amount)
                .add("app_trans_id", input.AppTransId)
                .add("embed_data", input.EmbedData)
                .add("item", input.Items)
                .add("bank_code", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .build();

         //return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
         JSONObject apiResponse = HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);

         // Thêm appTransId vào JSONObject trả về
         apiResponse.put("app_trans_id", input.AppTransId);

         return apiResponse;
    }
    public JSONObject queryOrderStatus(String appTransId) throws Exception {
        // Tạo thông tin cần thiết
        String appId = String.valueOf(AppInfo.APP_ID);
        String macKey = AppInfo.MAC_KEY;
        String inputMac = String.format("%s|%s", appId, appTransId);
        String mac = Helpers.getMac(macKey, inputMac);

        Log.e("tridoan", "queryOrderStatus: " + mac);
        Log.e("tridoan", "queryOrderStatus: " + appTransId);
        // Tạo Request Body
        JSONObject requestBody = new JSONObject();
        requestBody.put("app_id", appId);
        requestBody.put("app_trans_id", appTransId);
        requestBody.put("mac", mac);

        // Gửi Request
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://openapi.zalopay.vn/v2/query") // Sandbox URL
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            assert response.body() != null;
            return new JSONObject(response.body().string());
        } else {
            throw new Exception("Query Order Status Failed: " + response.message());
        }
    }

}