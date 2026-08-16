import { Button, Card, CardActions, CardContent, Chip, Stack, Typography } from '@mui/material';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import InfoIcon from '@mui/icons-material/Info';
import { useNavigate } from 'react-router';
import type { JobResponse, JobStatus } from '../../../../api/types/job.ts';
import useJobs from '../../../../hooks/useJobs.ts';

interface JobCardProps {
  job: JobResponse;
}

const getStatusColor = (status: JobStatus): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  switch (status) {
    case 'CREATED':
      return 'default';
    case 'RUNNING':
      return 'info';
    case 'COMPLETED':
      return 'success';
    case 'FAILED':
      return 'error';
    case 'STOPPED':
      return 'warning';
    default:
      return 'default';
  }
};

const JobCard = ({ job }: JobCardProps) => {
  const navigate = useNavigate();
  const { onStart, onStop } = useJobs();

  const canStart = job.status !== 'RUNNING';
  const canStop = job.status === 'RUNNING';

  return (
    <Card sx={{ maxWidth: 300, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 1 }}>
        <Typography variant='h5'>{job.baseUrl}</Typography>
        <Typography variant='subtitle2' sx={{ flexGrow: 1 }}>{job.description}</Typography>
        <Chip label={job.status} size='small' color={getStatusColor(job.status)} variant='filled'/>

        {job.seeds.length > 0 && (
          <div>
            <Typography variant='caption' color='text.secondary'>Seeds:</Typography>
            <Stack spacing={0.5}>
              {job.seeds.map((seed) => (
                <Typography key={seed.id} variant='caption' color='text.secondary'>
                  • {seed.type}: {seed.value}
                </Typography>
              ))}
            </Stack>
          </div>
        )}

        <Stack spacing={0.5}>
          {job.startedAt && (
            <Typography variant='caption' color='text.secondary'>
              Started: {new Date(job.startedAt).toLocaleString()}
            </Typography>
          )}
          {job.finishedAt && (
            <Typography variant='caption' color='text.secondary'>
              Finished: {new Date(job.finishedAt).toLocaleString()}
            </Typography>
          )}
        </Stack>
      </CardContent>
      <CardActions sx={{ justifyContent: 'space-between' }}>
        <Button
          startIcon={<InfoIcon/>}
          onClick={() => navigate(`/jobs/${job.id}`)}
        >
          Info
        </Button>
        <Button
          startIcon={<PlayArrowIcon/>}
          color='success'
          onClick={() => onStart(job.id)}
          disabled={!canStart}
        >
          Start
        </Button>
        <Button
          startIcon={<StopIcon/>}
          color='error'
          onClick={() => onStop(job.id)}
          disabled={!canStop}
        >
          Stop
        </Button>
      </CardActions>
    </Card>
  );
};

export default JobCard;
