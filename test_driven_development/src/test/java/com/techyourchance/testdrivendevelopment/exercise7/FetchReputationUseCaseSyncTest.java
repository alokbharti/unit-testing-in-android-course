package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class FetchReputationUseCaseSyncTest {

    private final int REPUTATION = 12;

    FetchReputationUseCaseSync SUT;
    @Mock GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void fetchReputation_successful_successReturned() throws Exception {
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        assertThat(result.getStatus(), is(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS));
    }

    @Test
    public void fetchReputation_successful_reputationReturned() throws Exception {
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputation_error_failureReturned() throws Exception {
        error();
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        assertThat(result.getStatus(), is(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR));
    }

    @Test
    public void fetchReputation_error_zeroReputationReturned() throws Exception {
        error();
        GetReputationHttpEndpointSync.EndpointResult result = SUT.fetchReputation();
        assertThat(result.getReputation(), is(0));
    }

    private void error() throws Exception {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0));
    }

    private void success() throws Exception {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, REPUTATION));
    }
}