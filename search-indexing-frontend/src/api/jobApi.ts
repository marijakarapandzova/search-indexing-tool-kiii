import axiosInstance from '../axios/axios.ts';
import type { CrawlActionLogResponse, CreateJobRequest, JobResponse } from './types/job.ts';

const jobApi = {
  findAll: async () => {
    return await axiosInstance.get<JobResponse[]>('/jobs');
  },
  findById: async (id: string) => {
    return await axiosInstance.get<JobResponse>(`/jobs/${id}`);
  },
  add: async (data: CreateJobRequest) => {
    return await axiosInstance.post<JobResponse>('/jobs/add', data);
  },
  start: async (id: string) => {
    return await axiosInstance.post<JobResponse>(`/jobs/${id}/start`);
  },
  stop: async (id: string) => {
    return await axiosInstance.post<JobResponse>(`/jobs/${id}/stop`);
  },
  findLogs: async (id: string) => {
    return await axiosInstance.get<CrawlActionLogResponse[]>(`/jobs/${id}/logs`);
  }
};

export default jobApi;
