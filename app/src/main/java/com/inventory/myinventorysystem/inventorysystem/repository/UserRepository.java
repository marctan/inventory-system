package com.inventory.myinventorysystem.inventorysystem.repository;

import android.content.Context;

import com.inventory.myinventorysystem.inventorysystem.database.InventoryDatabase;
import com.inventory.myinventorysystem.inventorysystem.database.User;
import com.inventory.myinventorysystem.inventorysystem.database.UsersDao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Marc Q. Tan on 03/03/2020.
 */
public class UserRepository {
    private UsersDao usersDao;
    private MutableLiveData<User> user;

    public UserRepository(Context ctx) {
        usersDao = InventoryDatabase.getInstance(ctx).userDao();
        user = new MutableLiveData<>();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void queryUser(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                user.postValue(usersDao.getUser(username, password));
            }
        }).start();
    }

    public Completable insert(User user) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                usersDao.insertUser(user);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<User> getUserById(int id) {
        return usersDao.getUserById(id);
    }
}
