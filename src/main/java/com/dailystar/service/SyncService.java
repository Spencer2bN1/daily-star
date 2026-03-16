package com.dailystar.service;

import com.dailystar.dto.SyncProfileRequest;
import com.dailystar.dto.SyncProfileResponse;
import com.dailystar.dto.SyncDeltaRequest;
import com.dailystar.dto.SyncDeltaResponse;

public interface SyncService {

    SyncDeltaResponse syncDelta(Long accountId, SyncDeltaRequest request);

    SyncProfileResponse syncProfile(Long accountId, SyncProfileRequest request);

    void clearProfile(Long accountId);
}
