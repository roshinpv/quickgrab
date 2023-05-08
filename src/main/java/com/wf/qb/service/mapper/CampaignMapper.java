package com.wf.qb.service.mapper;

import com.wf.qb.domain.Campaign;
import com.wf.qb.service.dto.CampaignDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Campaign} and its DTO {@link CampaignDTO}.
 */
@Mapper(componentModel = "spring")
public interface CampaignMapper extends EntityMapper<CampaignDTO, Campaign> {}
