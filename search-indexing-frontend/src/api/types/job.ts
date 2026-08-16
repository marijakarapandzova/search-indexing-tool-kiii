export type JobStatus = 'CREATED' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'STOPPED';

export type SeedType = 'URL' | 'SITEMAP' | 'SECTION';

export type CrawlActionType =
  | 'FETCH'
  | 'PARSE'
  | 'DETECT_LANGUAGE'
  | 'INDEX'
  | 'ENQUEUE'
  | 'SKIP'
  | 'FINISH';

export interface CreateSeedRequest {
  type: SeedType;
  value: string;
}

export interface CreateJobRequest {
  baseUrl: string;
  description: string;
  seeds: CreateSeedRequest[];
}

export interface SeedResponse {
  id: number;
  type: SeedType;
  value: string;
}

export interface JobResponse {
  id: number;
  baseUrl: string;
  status: JobStatus;
  description: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  seeds: SeedResponse[];
}

export interface CrawlActionLogResponse {
  id: number;
  actionType: CrawlActionType;
  details: string | null;
  successful: boolean;
  occurredAt: string;
}
