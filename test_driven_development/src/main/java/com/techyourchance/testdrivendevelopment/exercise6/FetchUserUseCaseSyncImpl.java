package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync{

    FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    UsersCache usersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache){
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User user = usersCache.getUser(userId);
        if (user == null){
            try {
                FetchUserHttpEndpointSync.EndpointResult result = fetchUserHttpEndpointSync.fetchUserSync(userId);
                if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS){
                    usersCache.cacheUser(new User(result.getUserId(), result.getUsername()));
                    return new UseCaseResult(Status.SUCCESS, new User(result.getUserId(), result.getUsername()));
                } else /*if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR)*/ {
                    return new UseCaseResult(Status.FAILURE, null);
                }
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        } else {
            return new UseCaseResult(Status.SUCCESS, user);
        }
    }
}
