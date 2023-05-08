import dayjs from 'dayjs';

export interface ICampaign {
  id?: number;
  name?: string | null;
  type?: string | null;
  discount?: number | null;
  start?: string | null;
}

export const defaultValue: Readonly<ICampaign> = {};
