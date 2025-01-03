package com.example.kotlinapplicationdelivery.fragments.restaurant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kotlinapplicationdelivery.R;


public class RestaurantPersonManagementFragment extends Fragment {



    public RestaurantPersonManagementFragment() {

    }


    public static RestaurantPersonManagementFragment newInstance(String param1, String param2) {
        return new RestaurantPersonManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_person_management, container, false);
    }
}