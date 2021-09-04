package com.example.tripawy.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tripawy.Trip;

public class HomeViewModel extends ViewModel {
   // TripRepository mRepository;

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
      //  mRepository=new TripRepository();
    }
//    public void delete(Trip trip){
//        mRepository.delete(trip);
//    }

    public LiveData<String> getText() {
        return mText;
    }
}