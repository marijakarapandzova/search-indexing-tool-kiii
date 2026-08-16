export type DonationStatus =
  | 'DRAFT'
  | 'APPROVED'
  | 'SUBMITTED'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'FAILED';

export interface CreateDonationBatchRequest {
  documentIds: number[];
}

export interface DonationBatchResponse {
  id: number;
  status: DonationStatus;
  vezilkaReference: string | null;
  submittedAt: string | null;
  createdAt: string;
  documentIds: number[];
}
