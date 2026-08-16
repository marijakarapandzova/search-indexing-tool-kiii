/**
 * Request params for GET /api/documents/search. Omitted optional fields are
 * not sent.
 */
export interface SearchParams {
  query: string;
  page?: number;
  size?: number;
  minMacedonianConfidence?: number;
}

export interface SearchHitResponse {
  docId: string;
  url: string;
  title: string | null;
  snippet: string | null;
  score: number;
  macedonianConfidence: number | null;
}

export interface SearchResultResponse {
  query: string;
  totalHits: number;
  hits: SearchHitResponse[];
}
