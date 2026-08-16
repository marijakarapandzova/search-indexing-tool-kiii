import { useCallback, useEffect, useMemo, useState } from 'react';
import * as React from 'react';
import jobApi from '../api/jobApi.ts';
import type { CreateJobRequest, JobResponse } from '../api/types/job.ts';
import JobsContext from '../contexts/jobsContext.ts';
import useSnackbar from '../hooks/useSnackbar.ts';

/**
 * Fully provided as the reference example of the provider pattern used in
 * this template — mirror it when you build the documents and donations features.
 * Note: until the backend TODO(student) services are implemented, every call
 * surfaces a "Not Implemented" error snackbar.
 */
const JobsProvider = ({ children }: { children: React.ReactNode }) => {
  const { showSnackbar } = useSnackbar();

  const [jobs, setJobs] = useState<JobResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetch = useCallback(async () => {
    setLoading(true);

    try {
      const response = await jobApi.findAll();
      setJobs(response.data);
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to load jobs.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showSnackbar]);

  const onCreate = useCallback(async (data: CreateJobRequest) => {
    try {
      await jobApi.add(data);
      await fetch();
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to create job.', 'error');
    }
  }, [fetch, showSnackbar]);

  const onStart = useCallback(async (id: number) => {
    try {
      await jobApi.start(id.toString());
      await fetch();
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to start job.', 'error');
    }
  }, [fetch, showSnackbar]);

  const onStop = useCallback(async (id: number) => {
    try {
      await jobApi.stop(id.toString());
      await fetch();
    } catch (err) {
      showSnackbar(err instanceof Error ? err.message : 'Failed to stop job.', 'error');
    }
  }, [fetch, showSnackbar]);

  useEffect(() => {
    void fetch();
  }, [fetch]);

  const value = useMemo(
    () => ({ jobs, loading, onCreate, onStart, onStop }),
    [jobs, loading, onCreate, onStart, onStop]
  );

  return <JobsContext value={value}>{children}</JobsContext>;
};

export default JobsProvider;
