package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";

    UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSyncTd;
    UsersCacheTd mUsersCacheTd;

    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        mUserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd);
    }

    // 1. userId successfully passed to endpoint
    @Test
    public void userProfileSync_success_passedToEndpoint() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    // 2. user fetched successfully and cached
    @Test
    public void userProfileSync_success_userCached() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID).getUserId(), is(USER_ID));
        assertThat(mUsersCacheTd.getUser(USER_ID).getFullName(), is(FULL_NAME));
        assertThat(mUsersCacheTd.getUser(USER_ID).getImageUrl(), is(IMAGE_URL));
    }

    // 3. user fetch general error and not cached
    @Test
    public void userProfileSync_generalError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    // 4. user fetch server error and not cached
    @Test
    public void userProfileSync_serverError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    // 5. user fetch network error and not cached
    @Test
    public void userProfileSync_networkError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void userProfileSync_authError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    // 6. user fetched successfully and success returned
    @Test
    public void userProfileSync_success_successReturned() throws Exception {
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));
    }

    // 7. user fetch general error and failure returned
    @Test
    public void userProfileSync_generalError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void userProfileSync_authError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // 8. user fetch server error and failure returned
    @Test
    public void userProfileSync_serverError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    // 9. user fetch network error and network error return
    @Test
    public void userProfileSync_networkError_networkErrorReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync{
        public String mUserId;
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;
        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;

            if (mIsGeneralError){
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsServerError){
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError){
                throw new NetworkErrorException();
            } else if (mIsAuthError){
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {

        private final List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUsers.remove(existingUser);
            }
            mUsers.add(user);
        }

        @Override
        @Nullable
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }
}