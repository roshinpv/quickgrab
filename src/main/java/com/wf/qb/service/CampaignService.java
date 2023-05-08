package com.wf.qb.service;

import com.wf.qb.domain.Campaign;
import com.wf.qb.repository.CampaignRepository;
import com.wf.qb.service.dto.CampaignDTO;
import com.wf.qb.service.mapper.CampaignMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Campaign}.
 */
@Service
@Transactional
public class CampaignService {

    private final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;

    private final CampaignMapper campaignMapper;

    public CampaignService(CampaignRepository campaignRepository, CampaignMapper campaignMapper) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
    }

    /**
     * Save a campaign.
     *
     * @param campaignDTO the entity to save.
     * @return the persisted entity.
     */
    public CampaignDTO save(CampaignDTO campaignDTO) {
        log.debug("Request to save Campaign : {}", campaignDTO);
        Campaign campaign = campaignMapper.toEntity(campaignDTO);
        campaign = campaignRepository.save(campaign);
        return campaignMapper.toDto(campaign);
    }

    /**
     * Update a campaign.
     *
     * @param campaignDTO the entity to save.
     * @return the persisted entity.
     */
    public CampaignDTO update(CampaignDTO campaignDTO) {
        log.debug("Request to update Campaign : {}", campaignDTO);
        Campaign campaign = campaignMapper.toEntity(campaignDTO);
        campaign = campaignRepository.save(campaign);
        return campaignMapper.toDto(campaign);
    }

    /**
     * Partially update a campaign.
     *
     * @param campaignDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CampaignDTO> partialUpdate(CampaignDTO campaignDTO) {
        log.debug("Request to partially update Campaign : {}", campaignDTO);

        return campaignRepository
            .findById(campaignDTO.getId())
            .map(existingCampaign -> {
                campaignMapper.partialUpdate(existingCampaign, campaignDTO);

                return existingCampaign;
            })
            .map(campaignRepository::save)
            .map(campaignMapper::toDto);
    }

    /**
     * Get all the campaigns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CampaignDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Campaigns");
        return campaignRepository.findAll(pageable).map(campaignMapper::toDto);
    }

    /**
     * Get one campaign by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CampaignDTO> findOne(Long id) {
        log.debug("Request to get Campaign : {}", id);
        return campaignRepository.findById(id).map(campaignMapper::toDto);
    }

    /**
     * Delete the campaign by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Campaign : {}", id);
        campaignRepository.deleteById(id);
    }
}
