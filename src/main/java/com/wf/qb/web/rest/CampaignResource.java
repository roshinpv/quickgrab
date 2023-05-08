package com.wf.qb.web.rest;

import com.wf.qb.repository.CampaignRepository;
import com.wf.qb.service.CampaignQueryService;
import com.wf.qb.service.CampaignService;
import com.wf.qb.service.criteria.CampaignCriteria;
import com.wf.qb.service.dto.CampaignDTO;
import com.wf.qb.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.wf.qb.domain.Campaign}.
 */
@RestController
@RequestMapping("/api")
public class CampaignResource {

    private final Logger log = LoggerFactory.getLogger(CampaignResource.class);

    private static final String ENTITY_NAME = "campaign";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CampaignService campaignService;

    private final CampaignRepository campaignRepository;

    private final CampaignQueryService campaignQueryService;

    public CampaignResource(
        CampaignService campaignService,
        CampaignRepository campaignRepository,
        CampaignQueryService campaignQueryService
    ) {
        this.campaignService = campaignService;
        this.campaignRepository = campaignRepository;
        this.campaignQueryService = campaignQueryService;
    }

    /**
     * {@code POST  /campaigns} : Create a new campaign.
     *
     * @param campaignDTO the campaignDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new campaignDTO, or with status {@code 400 (Bad Request)} if the campaign has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/campaigns")
    public ResponseEntity<CampaignDTO> createCampaign(@RequestBody CampaignDTO campaignDTO) throws URISyntaxException {
        log.debug("REST request to save Campaign : {}", campaignDTO);
        if (campaignDTO.getId() != null) {
            throw new BadRequestAlertException("A new campaign cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CampaignDTO result = campaignService.save(campaignDTO);
        return ResponseEntity
            .created(new URI("/api/campaigns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /campaigns/:id} : Updates an existing campaign.
     *
     * @param id the id of the campaignDTO to save.
     * @param campaignDTO the campaignDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated campaignDTO,
     * or with status {@code 400 (Bad Request)} if the campaignDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the campaignDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/campaigns/{id}")
    public ResponseEntity<CampaignDTO> updateCampaign(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CampaignDTO campaignDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Campaign : {}, {}", id, campaignDTO);
        if (campaignDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, campaignDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!campaignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CampaignDTO result = campaignService.update(campaignDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, campaignDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /campaigns/:id} : Partial updates given fields of an existing campaign, field will ignore if it is null
     *
     * @param id the id of the campaignDTO to save.
     * @param campaignDTO the campaignDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated campaignDTO,
     * or with status {@code 400 (Bad Request)} if the campaignDTO is not valid,
     * or with status {@code 404 (Not Found)} if the campaignDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the campaignDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/campaigns/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CampaignDTO> partialUpdateCampaign(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CampaignDTO campaignDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Campaign partially : {}, {}", id, campaignDTO);
        if (campaignDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, campaignDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!campaignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CampaignDTO> result = campaignService.partialUpdate(campaignDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, campaignDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /campaigns} : get all the campaigns.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of campaigns in body.
     */
    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignDTO>> getAllCampaigns(
        CampaignCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Campaigns by criteria: {}", criteria);
        Page<CampaignDTO> page = campaignQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /campaigns/count} : count all the campaigns.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/campaigns/count")
    public ResponseEntity<Long> countCampaigns(CampaignCriteria criteria) {
        log.debug("REST request to count Campaigns by criteria: {}", criteria);
        return ResponseEntity.ok().body(campaignQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /campaigns/:id} : get the "id" campaign.
     *
     * @param id the id of the campaignDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the campaignDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<CampaignDTO> getCampaign(@PathVariable Long id) {
        log.debug("REST request to get Campaign : {}", id);
        Optional<CampaignDTO> campaignDTO = campaignService.findOne(id);
        return ResponseUtil.wrapOrNotFound(campaignDTO);
    }

    /**
     * {@code DELETE  /campaigns/:id} : delete the "id" campaign.
     *
     * @param id the id of the campaignDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        log.debug("REST request to delete Campaign : {}", id);
        campaignService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
