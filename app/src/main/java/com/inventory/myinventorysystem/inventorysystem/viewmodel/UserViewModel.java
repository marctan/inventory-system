package com.inventory.myinventorysystem.inventorysystem.viewmodel;

import android.app.Application;

import com.inventory.myinventorysystem.inventorysystem.database.User;
import com.inventory.myinventorysystem.inventorysystem.repository.UserRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Marc Q. Tan on 03/03/2020.
 */
public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<Boolean> insertUserStatus;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Boolean> getInsertUserStatus(){
        if(insertUserStatus == null) {
            insertUserStatus = new MutableLiveData<>();
        }
        return insertUserStatus;
    }

    public LiveData<User> getUser() {
        return userRepository.getUser();
    }

    public void queryUser(String username, String password) {
        userRepository.queryUser(username, password);
    }

    public void insert(User user) {
        disposable.add(userRepository.insert(user).subscribe(() -> insertUserStatus.setValue(true)));
    }

    public LiveData<User> getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
