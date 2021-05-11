package com.example.rent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rent.model.User;
import com.example.rent.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository mRepository;

    public UserViewModel() {
        this.mRepository = new UserRepository();
    }

    public MutableLiveData<User> loadUserAccountInfo(final String userId) {
        return mRepository.getUser(userId);
    }

    public void updateUserInfo(User user) {
        mRepository.updateUser(user);
    }

    public MutableLiveData<User> loadCurrentUserAccountInfo(final String userId) {
        return mRepository.getCurrentUser(userId);
    }

    public MutableLiveData<List<User>> getAllUsers() {
        return mRepository.getAllUsers();
    }

    public void createUser(User user) {
        mRepository.createUserObject(user);
    }
}
