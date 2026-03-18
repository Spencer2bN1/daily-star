package com.dailystar.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityUserPageResponse {

    private Integer page;
    private Integer pageSize;
    private boolean hasMore;

    @Builder.Default
    private List<CommunityUserProfileResponse> items = new ArrayList<CommunityUserProfileResponse>();
}
