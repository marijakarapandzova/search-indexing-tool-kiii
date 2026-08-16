import { Box, Typography } from '@mui/material';
import type { IndexingJobResponse } from '../../api/types/job.ts';

interface JobStampsProps {
  jobs: IndexingJobResponse[];
  limit?: number;
}

const getStampStatus = (status: string) => {
  switch (status.toUpperCase()) {
    case 'RUNNING':
      return { symbol: '⟳', label: 'Во тек', color: '#2F6E64', rotation: -4 };
    case 'COMPLETED':
      return { symbol: '✓', label: 'Завршено', color: '#1B2430', rotation: 3 };
    case 'FAILED':
      return { symbol: '✕', label: 'Неуспешно', color: '#A23B33', rotation: -8 };
    default:
      return { symbol: '?', label: status, color: '#8A8272', rotation: 0 };
  }
};

const JobStamps = ({ jobs, limit = 3 }: JobStampsProps) => {
  const displayJobs = jobs.slice(0, limit);

  return (
    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2.5, justifyContent: 'center' }}>
      {displayJobs.map((job) => {
        const stampStatus = getStampStatus(job.status);
        return (
          <Box
            key={job.id}
            sx={{
              fontFamily: '"JetBrains Mono", monospace',
              border: `2px solid ${stampStatus.color}`,
              borderRadius: '50%',
              width: 118,
              height: 118,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              textAlign: 'center',
              fontSize: '0.72rem',
              letterSpacing: '0.06em',
              textTransform: 'uppercase',
              transform: `rotate(${stampStatus.rotation}deg)`,
              flexDirection: 'column',
              gap: 0.5,
              lineHeight: 1.25,
              color: stampStatus.color,
            }}
          >
            <Box sx={{ fontSize: '1.3rem', fontFamily: '"PT Serif", Georgia, serif', fontWeight: 700 }}>
              {stampStatus.symbol}
            </Box>
            <Box>
              {stampStatus.label}
              <br/>#{job.id}
            </Box>
          </Box>
        );
      })}
    </Box>
  );
};

export default JobStamps;