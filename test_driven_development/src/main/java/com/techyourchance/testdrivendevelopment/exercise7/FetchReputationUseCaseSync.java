package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock) {
        this.getReputationHttpEndpointSyncMock = getReputationHttpEndpointSyncMock;
    }

    public GetReputationHttpEndpointSync.EndpointResult fetchReputation() {
        GetReputationHttpEndpointSync.EndpointResult result = getReputationHttpEndpointSyncMock.getReputationSync();
        if (result.getStatus() == GetReputationHttpEndpointSync.EndpointStatus.SUCCESS)
            return new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, result.getReputation());
        else
            return new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0);
    }
}
