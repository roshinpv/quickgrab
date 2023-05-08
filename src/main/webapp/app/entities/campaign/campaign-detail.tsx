import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './campaign.reducer';

export const CampaignDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const campaignEntity = useAppSelector(state => state.campaign.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="campaignDetailsHeading">Campaign</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{campaignEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{campaignEntity.name}</dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{campaignEntity.type}</dd>
          <dt>
            <span id="discount">Discount</span>
          </dt>
          <dd>{campaignEntity.discount}</dd>
          <dt>
            <span id="start">Start</span>
          </dt>
          <dd>{campaignEntity.start ? <TextFormat value={campaignEntity.start} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/campaign" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/campaign/${campaignEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CampaignDetail;
