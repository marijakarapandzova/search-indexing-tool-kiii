import { createContext } from 'react';
import type { CreateJobRequest, JobResponse } from '../api/types/job.ts';

export interface JobsContextType {
  jobs: JobResponse[];
  loading: boolean;
  onCreate: (data: CreateJobRequest) => Promise<void>;
  onStart: (id: number) => Promise<void>;
  onStop: (id: number) => Promise<void>;
}

const JobsContext = createContext<JobsContextType>({} as JobsContextType);

export default JobsContext;
