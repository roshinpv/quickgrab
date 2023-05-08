package com.wf.qb.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.wf.qb.domain.Campaign} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CampaignDTO implements Serializable {

    private Long id;

    private String name;

    private String type;

    private Double discount;

    private ZonedDateTime start;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CampaignDTO)) {
            return false;
        }

        CampaignDTO campaignDTO = (CampaignDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, campaignDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CampaignDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", discount=" + getDiscount() +
            ", start='" + getStart() + "'" +
            "}";
    }
}
