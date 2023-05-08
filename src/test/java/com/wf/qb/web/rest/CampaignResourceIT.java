package com.wf.qb.web.rest;

import static com.wf.qb.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.wf.qb.IntegrationTest;
import com.wf.qb.domain.Campaign;
import com.wf.qb.repository.CampaignRepository;
import com.wf.qb.service.criteria.CampaignCriteria;
import com.wf.qb.service.dto.CampaignDTO;
import com.wf.qb.service.mapper.CampaignMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CampaignResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CampaignResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Double DEFAULT_DISCOUNT = 1D;
    private static final Double UPDATED_DISCOUNT = 2D;
    private static final Double SMALLER_DISCOUNT = 1D - 1D;

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/campaigns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCampaignMockMvc;

    private Campaign campaign;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Campaign createEntity(EntityManager em) {
        Campaign campaign = new Campaign().name(DEFAULT_NAME).type(DEFAULT_TYPE).discount(DEFAULT_DISCOUNT).start(DEFAULT_START);
        return campaign;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Campaign createUpdatedEntity(EntityManager em) {
        Campaign campaign = new Campaign().name(UPDATED_NAME).type(UPDATED_TYPE).discount(UPDATED_DISCOUNT).start(UPDATED_START);
        return campaign;
    }

    @BeforeEach
    public void initTest() {
        campaign = createEntity(em);
    }

    @Test
    @Transactional
    void createCampaign() throws Exception {
        int databaseSizeBeforeCreate = campaignRepository.findAll().size();
        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);
        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(campaignDTO)))
            .andExpect(status().isCreated());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeCreate + 1);
        Campaign testCampaign = campaignList.get(campaignList.size() - 1);
        assertThat(testCampaign.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCampaign.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCampaign.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
        assertThat(testCampaign.getStart()).isEqualTo(DEFAULT_START);
    }

    @Test
    @Transactional
    void createCampaignWithExistingId() throws Exception {
        // Create the Campaign with an existing ID
        campaign.setId(1L);
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        int databaseSizeBeforeCreate = campaignRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCampaignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(campaignDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCampaigns() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(campaign.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))));
    }

    @Test
    @Transactional
    void getCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get the campaign
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL_ID, campaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(campaign.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.discount").value(DEFAULT_DISCOUNT.doubleValue()))
            .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)));
    }

    @Test
    @Transactional
    void getCampaignsByIdFiltering() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        Long id = campaign.getId();

        defaultCampaignShouldBeFound("id.equals=" + id);
        defaultCampaignShouldNotBeFound("id.notEquals=" + id);

        defaultCampaignShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCampaignShouldNotBeFound("id.greaterThan=" + id);

        defaultCampaignShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCampaignShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCampaignsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where name equals to DEFAULT_NAME
        defaultCampaignShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the campaignList where name equals to UPDATED_NAME
        defaultCampaignShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCampaignsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCampaignShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the campaignList where name equals to UPDATED_NAME
        defaultCampaignShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCampaignsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where name is not null
        defaultCampaignShouldBeFound("name.specified=true");

        // Get all the campaignList where name is null
        defaultCampaignShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCampaignsByNameContainsSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where name contains DEFAULT_NAME
        defaultCampaignShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the campaignList where name contains UPDATED_NAME
        defaultCampaignShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCampaignsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where name does not contain DEFAULT_NAME
        defaultCampaignShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the campaignList where name does not contain UPDATED_NAME
        defaultCampaignShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCampaignsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where type equals to DEFAULT_TYPE
        defaultCampaignShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the campaignList where type equals to UPDATED_TYPE
        defaultCampaignShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCampaignsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultCampaignShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the campaignList where type equals to UPDATED_TYPE
        defaultCampaignShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCampaignsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where type is not null
        defaultCampaignShouldBeFound("type.specified=true");

        // Get all the campaignList where type is null
        defaultCampaignShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllCampaignsByTypeContainsSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where type contains DEFAULT_TYPE
        defaultCampaignShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the campaignList where type contains UPDATED_TYPE
        defaultCampaignShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCampaignsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where type does not contain DEFAULT_TYPE
        defaultCampaignShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the campaignList where type does not contain UPDATED_TYPE
        defaultCampaignShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount equals to DEFAULT_DISCOUNT
        defaultCampaignShouldBeFound("discount.equals=" + DEFAULT_DISCOUNT);

        // Get all the campaignList where discount equals to UPDATED_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount in DEFAULT_DISCOUNT or UPDATED_DISCOUNT
        defaultCampaignShouldBeFound("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT);

        // Get all the campaignList where discount equals to UPDATED_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount is not null
        defaultCampaignShouldBeFound("discount.specified=true");

        // Get all the campaignList where discount is null
        defaultCampaignShouldNotBeFound("discount.specified=false");
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount is greater than or equal to DEFAULT_DISCOUNT
        defaultCampaignShouldBeFound("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the campaignList where discount is greater than or equal to UPDATED_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount is less than or equal to DEFAULT_DISCOUNT
        defaultCampaignShouldBeFound("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the campaignList where discount is less than or equal to SMALLER_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount is less than DEFAULT_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.lessThan=" + DEFAULT_DISCOUNT);

        // Get all the campaignList where discount is less than UPDATED_DISCOUNT
        defaultCampaignShouldBeFound("discount.lessThan=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where discount is greater than DEFAULT_DISCOUNT
        defaultCampaignShouldNotBeFound("discount.greaterThan=" + DEFAULT_DISCOUNT);

        // Get all the campaignList where discount is greater than SMALLER_DISCOUNT
        defaultCampaignShouldBeFound("discount.greaterThan=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start equals to DEFAULT_START
        defaultCampaignShouldBeFound("start.equals=" + DEFAULT_START);

        // Get all the campaignList where start equals to UPDATED_START
        defaultCampaignShouldNotBeFound("start.equals=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsInShouldWork() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start in DEFAULT_START or UPDATED_START
        defaultCampaignShouldBeFound("start.in=" + DEFAULT_START + "," + UPDATED_START);

        // Get all the campaignList where start equals to UPDATED_START
        defaultCampaignShouldNotBeFound("start.in=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start is not null
        defaultCampaignShouldBeFound("start.specified=true");

        // Get all the campaignList where start is null
        defaultCampaignShouldNotBeFound("start.specified=false");
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start is greater than or equal to DEFAULT_START
        defaultCampaignShouldBeFound("start.greaterThanOrEqual=" + DEFAULT_START);

        // Get all the campaignList where start is greater than or equal to UPDATED_START
        defaultCampaignShouldNotBeFound("start.greaterThanOrEqual=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start is less than or equal to DEFAULT_START
        defaultCampaignShouldBeFound("start.lessThanOrEqual=" + DEFAULT_START);

        // Get all the campaignList where start is less than or equal to SMALLER_START
        defaultCampaignShouldNotBeFound("start.lessThanOrEqual=" + SMALLER_START);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsLessThanSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start is less than DEFAULT_START
        defaultCampaignShouldNotBeFound("start.lessThan=" + DEFAULT_START);

        // Get all the campaignList where start is less than UPDATED_START
        defaultCampaignShouldBeFound("start.lessThan=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllCampaignsByStartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaignList where start is greater than DEFAULT_START
        defaultCampaignShouldNotBeFound("start.greaterThan=" + DEFAULT_START);

        // Get all the campaignList where start is greater than SMALLER_START
        defaultCampaignShouldBeFound("start.greaterThan=" + SMALLER_START);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCampaignShouldBeFound(String filter) throws Exception {
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(campaign.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))));

        // Check, that the count call also returns 1
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCampaignShouldNotBeFound(String filter) throws Exception {
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCampaignMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCampaign() throws Exception {
        // Get the campaign
        restCampaignMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();

        // Update the campaign
        Campaign updatedCampaign = campaignRepository.findById(campaign.getId()).get();
        // Disconnect from session so that the updates on updatedCampaign are not directly saved in db
        em.detach(updatedCampaign);
        updatedCampaign.name(UPDATED_NAME).type(UPDATED_TYPE).discount(UPDATED_DISCOUNT).start(UPDATED_START);
        CampaignDTO campaignDTO = campaignMapper.toDto(updatedCampaign);

        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
        Campaign testCampaign = campaignList.get(campaignList.size() - 1);
        assertThat(testCampaign.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCampaign.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCampaign.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testCampaign.getStart()).isEqualTo(UPDATED_START);
    }

    @Test
    @Transactional
    void putNonExistingCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(campaignDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCampaignWithPatch() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();

        // Update the campaign using partial update
        Campaign partialUpdatedCampaign = new Campaign();
        partialUpdatedCampaign.setId(campaign.getId());

        partialUpdatedCampaign.type(UPDATED_TYPE).start(UPDATED_START);

        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCampaign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCampaign))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
        Campaign testCampaign = campaignList.get(campaignList.size() - 1);
        assertThat(testCampaign.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCampaign.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCampaign.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
        assertThat(testCampaign.getStart()).isEqualTo(UPDATED_START);
    }

    @Test
    @Transactional
    void fullUpdateCampaignWithPatch() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();

        // Update the campaign using partial update
        Campaign partialUpdatedCampaign = new Campaign();
        partialUpdatedCampaign.setId(campaign.getId());

        partialUpdatedCampaign.name(UPDATED_NAME).type(UPDATED_TYPE).discount(UPDATED_DISCOUNT).start(UPDATED_START);

        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCampaign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCampaign))
            )
            .andExpect(status().isOk());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
        Campaign testCampaign = campaignList.get(campaignList.size() - 1);
        assertThat(testCampaign.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCampaign.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCampaign.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testCampaign.getStart()).isEqualTo(UPDATED_START);
    }

    @Test
    @Transactional
    void patchNonExistingCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, campaignDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCampaign() throws Exception {
        int databaseSizeBeforeUpdate = campaignRepository.findAll().size();
        campaign.setId(count.incrementAndGet());

        // Create the Campaign
        CampaignDTO campaignDTO = campaignMapper.toDto(campaign);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCampaignMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(campaignDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Campaign in the database
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        int databaseSizeBeforeDelete = campaignRepository.findAll().size();

        // Delete the campaign
        restCampaignMockMvc
            .perform(delete(ENTITY_API_URL_ID, campaign.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Campaign> campaignList = campaignRepository.findAll();
        assertThat(campaignList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
