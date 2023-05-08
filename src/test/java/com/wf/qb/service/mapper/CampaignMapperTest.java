package com.wf.qb.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CampaignMapperTest {

    private CampaignMapper campaignMapper;

    @BeforeEach
    public void setUp() {
        campaignMapper = new CampaignMapperImpl();
    }
}
