import { useState, useEffect, useCallback } from 'react';
import type { CrawlActionLogResponse, JobResponse } from '../api/types/job.ts';
import jobApi from '../api/jobApi.ts';

const useJobDetails = (id: string) => {
  const [job, setJob] = useState<JobResponse | null>(null);
  const [logs, setLogs] = useState<CrawlActionLogResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchJob = useCallback(async () => {
    try {
      const response = await jobApi.findById(id);
      setJob(response.data);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch job'));
    }
  }, [id]);

  const fetchLogs = useCallback(async () => {
    try {
      const response = await jobApi.findLogs(id);
      setLogs(response.data);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch logs'));
    }
  }, [id]);

  useEffect(() => {
    setLoading(true);
    Promise.all([fetchJob(), fetchLogs()])
      .catch(err => setError(err instanceof Error ? err : new Error('Failed to fetch job details')))
      .finally(() => setLoading(false));
  }, [fetchJob, fetchLogs]);

  useEffect(() => {
    if (job?.status === 'RUNNING') {
      const interval = setInterval(() => {
        fetchLogs();
      }, 2000);
      return () => clearInterval(interval);
    }
  }, [job?.status, fetchLogs]);

  return { job, logs, loading, error };
};

export default useJobDetails;
