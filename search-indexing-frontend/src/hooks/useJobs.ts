import { useContext } from 'react';
import JobsContext, { type JobsContextType } from '../contexts/jobsContext.ts';

const useJobs = () => useContext<JobsContextType>(JobsContext);

export default useJobs;
