package com.example.kotlinapplicationdelivery.activities.client.payments.zalo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kotlinapplicationdelivery.R;
import com.example.kotlinapplicationdelivery.activities.client.home.ClientHomeActivity;
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Api.CreateOrder;
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Constant.AppInfo;
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Helper.HMac.HMacUtil;
import com.example.kotlinapplicationdelivery.activities.client.payments.zalo.zalo_util.Helper.Helpers;
import com.example.kotlinapplicationdelivery.models.Address;
import com.example.kotlinapplicationdelivery.models.Order;
import com.example.kotlinapplicationdelivery.models.Product;
import com.example.kotlinapplicationdelivery.models.ResponseHttp;
import com.example.kotlinapplicationdelivery.models.User;
import com.example.kotlinapplicationdelivery.providers.OrdersProvider;
import com.example.kotlinapplicationdelivery.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ClientPaymentZaloFormActivity extends AppCompatActivity {

    TextView lblZpTransToken, txtToken;
    Button btnCreateOrder, btnPay;
    TextView txtAmount;
    CreateOrder orderApi;
    SharedPref sharedPref;
    Gson gson = new Gson();
    User user;
    OrdersProvider ordersProvider;
    ArrayList<Product> selectedProducts = new ArrayList<>();
    Address address;
    String note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_orders_zalopay_form);
        BindView();
        txtAmount.setText(getIntent().getStringExtra("total_price"));
        sharedPref = new SharedPref(this);
        getUserFromSession();
        getAddressFromSession();
        if (user != null && user.getSessionToken() != null) {
            ordersProvider = new OrdersProvider(user.getSessionToken());
        } else {
            throw new NullPointerException("Session token is null");
        }
        note = getIntent().getStringExtra("note");
        getProductsFromSharedPref();
        StrictMode.ThreadPolicy policy;
        policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        orderApi = new CreateOrder();
        // ZaloPay SDK Init
        ZaloPaySDK.init(2554, Environment.SANDBOX);
        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                JSONObject data = null;
                try {
                    data = orderApi.createOrder(txtAmount.getText().toString());
                    Log.d("Amount", txtAmount.getText().toString());
                    lblZpTransToken.setVisibility(View.VISIBLE);
                    String code = data.getString("return_code");
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
                        //lblZpTransToken.setText("zptranstoken");
                        txtToken.setText(data.getString("zp_trans_token"));
                        IsDone();
                        String token = txtToken.getText().toString();
                        //showPaymentSuccessDialog();
                        createOrder();
                        ZaloPaySDK.getInstance().payOrder(ClientPaymentZaloFormActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                Log.e("this is callback success", "onPaymentSucceeded: ");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        createOrder();
                                        showPaymentSuccessDialog();
                                    }
                                });
                                IsLoading();
                            }

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(ClientPaymentZaloFormActivity.this)
                                        .setTitle("User Cancel Payment")
                                        .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(ClientPaymentZaloFormActivity.this)
                                        .setTitle("Payment Fail")
                                        .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }

                        });
                        Log.e("tridoan", "onClick1: " + data.getString("app_trans_id"));
                        Log.e("tridoan", "user note: " + getIntent().getStringExtra("note"));
//                        showPaymentSuccessDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert data != null;
                        checkAndHandleOrderStatus(data.getString("app_trans_id"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void IsLoading() {
        lblZpTransToken.setVisibility(View.INVISIBLE);
        txtToken.setVisibility(View.INVISIBLE);
        btnPay.setVisibility(View.INVISIBLE);
    }

    private void IsDone() {
//        lblZpTransToken.setVisibility(View.VISIBLE);
        txtToken.setVisibility(View.VISIBLE);
        //btnPay.setVisibility(View.VISIBLE);
    }
    private void BindView() {
        txtToken = findViewById(R.id.txtToken);
        lblZpTransToken = findViewById(R.id.lblZpTransToken);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        txtAmount = findViewById(R.id.txtAmount);
        btnPay = findViewById(R.id.btnPay);
        IsLoading();
    }

    private void showPaymentSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_payment_zalo_status);
        Button buttonHome = dialog.findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            dialog.dismiss();
            goToHomePage();
        });
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }


    private void goToHomePage() {
        Intent intent = new Intent(this, ClientHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    public void handleQueryOrderResponse(JSONObject response) throws JSONException {
        int returnCode = response.getInt("return_code");
        Log.e("tridoan", "handleQueryOrderResponse: " +  response.getInt("return_code"));
        String returnMessage = response.getString("return_message");

        if (returnCode == 1) {
            // API truy vấn thành công
            String subReturnCode = response.getString("sub_return_code");
            String subReturnMessage = response.getString("sub_return_message");
            String appTransId = response.getString("app_trans_id");

            switch (subReturnCode) {
                case "01": // Giao dịch thành công
                    System.out.println("Transaction Success: " + subReturnMessage);
                    updateOrderStatus(appTransId, "SUCCESS");
                    showPaymentSuccessDialog();
                    break;

                case "02": // Giao dịch thất bại
                    System.out.println("Transaction Failed: " + subReturnMessage);
                    updateOrderStatus(appTransId, "FAILED");
                    break;

                case "03": // Giao dịch đang xử lý
                    System.out.println("Transaction Pending: " + subReturnMessage);
                    updateOrderStatus(appTransId, "PENDING");
                    break;

                case "04": // Giao dịch bị hủy
                    System.out.println("Transaction Cancelled: " + subReturnMessage);
                    updateOrderStatus(appTransId, "CANCELLED");
                    break;

                case "05": // Giao dịch không tìm thấy
                    System.out.println("Transaction Not Found: " + subReturnMessage);
                    updateOrderStatus(appTransId, "NOT_FOUND");
                    break;

                default: // Mã trạng thái không xác định
                    System.out.println("Unknown Transaction Status: " + subReturnMessage);
                    updateOrderStatus(appTransId, "UNKNOWN");
                    break;
            }
        } else {
            // API truy vấn thất bại
            System.out.println("Query API Failed: " + returnMessage);
            handleApiError(returnMessage);
        }
    }

    // Hàm giả lập cập nhật trạng thái đơn hàng trong hệ thống
    private void updateOrderStatus(String appTransId, String status) {
        // TODO: Thực hiện logic cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
        System.out.println("Updating order " + appTransId + " to status: " + status);
    }

    // Hàm giả lập xử lý lỗi API
    private void handleApiError(String errorMessage) {
        // TODO: Thực hiện xử lý khi API thất bại
        System.out.println("API Error: " + errorMessage);
    }
    public void checkAndHandleOrderStatus(String appTransId) {
        Log.e("tridoan", "checkAndHandleOrderStatus: ");
        try {
            // Gửi API request để truy vấn trạng thái
            JSONObject response = orderApi.queryOrderStatus(appTransId);

            // Gọi hàm xử lý logic dựa trên response từ ZaloPay
            handleQueryOrderResponse(response);

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error during order status query: " + e.getMessage());
            handleApiError(e.getMessage());
        }
    }
    private void getUserFromSession() {
        Gson gson = new Gson();
        if (sharedPref != null && sharedPref.getData("user") != null && !Objects.requireNonNull(sharedPref.getData("user")).isEmpty()) {
            user = gson.fromJson(sharedPref.getData("user"), User.class);
        }
    }
    private void getProductsFromSharedPref() {
        if (sharedPref != null && sharedPref.getData("order") != null && !Objects.requireNonNull(sharedPref.getData("order")).isEmpty()) {
            Type type = new TypeToken<ArrayList<Product>>() {}.getType();
            selectedProducts = gson.fromJson(sharedPref.getData("order"), type);
        }
    }
    private void getAddressFromSession() {
        if (sharedPref != null && sharedPref.getData("address") != null && !Objects.requireNonNull(sharedPref.getData("address")).isEmpty()) {
            address = gson.fromJson(sharedPref.getData("address"), Address.class);
        } else {
            Toast.makeText(this, "Chọn một địa chỉ để tiếp tục", Toast.LENGTH_LONG).show();
        }
    }
    private void createOrder() {
        Log.e("tridoan", "createOrder: ");
        if (user != null && user.getId() != null) {
            Order order = new Order(
                    null,
                    Objects.requireNonNull(user.getId()),
                    null,
                    Objects.requireNonNull(address.getId()),
                    Objects.requireNonNull(getIntent().getStringExtra("id_restaurant")),
                    null,
                    null,
                    selectedProducts,
                    note,
                    "Thanh toán online",
                    null,
                    null,
                    null,
                    null,
                    null
            );
            Log.e("tridoan", "createOrder: 2");
            if (ordersProvider != null) {
                Log.e("tridoan", "createOrder: 3");
                Objects.requireNonNull(ordersProvider.create(order)).enqueue(new Callback<ResponseHttp>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseHttp> call, @NonNull Response<ResponseHttp> response) {
                        if (response.body() != null && response.body().isSuccess()) {
                            if (sharedPref != null) {
                                sharedPref.remove("order");
                                showPaymentSuccessDialog();
                            }
                            //Toast.makeText(ClientPaymentZaloFormActivity.this, "${response.body()?.message}", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(ClientPaymentZaloFormActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseHttp> call, @NonNull Throwable t) {
                        //Toast.makeText(ClientAddressListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


}