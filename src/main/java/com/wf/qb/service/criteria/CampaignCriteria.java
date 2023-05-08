package com.wf.qb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.wf.qb.domain.Campaign} entity. This class is used
 * in {@link com.wf.qb.web.rest.CampaignResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /campaigns?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CampaignCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter type;

    private DoubleFilter discount;

    private ZonedDateTimeFilter start;

    private Boolean distinct;

    public CampaignCriteria() {}

    public CampaignCriteria(CampaignCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.discount = other.discount == null ? null : other.discount.copy();
        this.start = other.start == null ? null : other.start.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CampaignCriteria copy() {
        return new CampaignCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getType() {
        return type;
    }

    public StringFilter type() {
        if (type == null) {
            type = new StringFilter();
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public DoubleFilter getDiscount() {
        return discount;
    }

    public DoubleFilter discount() {
        if (discount == null) {
            discount = new DoubleFilter();
        }
        return discount;
    }

    public void setDiscount(DoubleFilter discount) {
        this.discount = discount;
    }

    public ZonedDateTimeFilter getStart() {
        return start;
    }

    public ZonedDateTimeFilter start() {
        if (start == null) {
            start = new ZonedDateTimeFilter();
        }
        return start;
    }

    public void setStart(ZonedDateTimeFilter start) {
        this.start = start;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CampaignCriteria that = (CampaignCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(discount, that.discount) &&
            Objects.equals(start, that.start) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, discount, start, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CampaignCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (discount != null ? "discount=" + discount + ", " : "") +
            (start != null ? "start=" + start + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
