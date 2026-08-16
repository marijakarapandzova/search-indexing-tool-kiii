export type ResourceType = 'ARTICLE' | 'PAGE' | 'DOCUMENT' | 'MEDIA';

export type MediaType = 'IMAGE' | 'VIDEO';

export interface MediaItemResponse {
  id: number;
  type: MediaType;
  sourceUrl: string;
  storagePath: string | null;
}

export interface DocumentResponse {
  id: number;
  jobId: number;
  docId: string | null;
  url: string;
  title: string | null;
  content: string | null;
  resourceType: ResourceType;
  indexedAt: string | null;
  macedonianConfidence: number | null;
  mediaItems: MediaItemResponse[];
  donationBatchId: number | null;
}

/**
 * Optional filters for browsing indexed documents — mirrors the request params
 * of GET /api/documents. Omitted fields are not sent.
 */
export interface DocumentFilter {
  jobId?: number;
  resourceType?: ResourceType;
  minMacedonianConfidence?: number;
  donated?: boolean;
  search?: string;
}

/**
 * Spring Data Page<T> as serialised by the backend.
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
