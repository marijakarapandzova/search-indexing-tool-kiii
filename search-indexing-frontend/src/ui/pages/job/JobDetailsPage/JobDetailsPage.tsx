import { Box, Card, CardContent, Chip, CircularProgress, Divider, Stack, Typography } from '@mui/material';
import { useParams } from 'react-router';
import useJobDetails from '../../../../hooks/useJobDetails.ts';
import JobLogViewer from '../../../components/job/JobLogViewer/JobLogViewer.tsx';

const JobDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const { job, logs, loading } = useJobDetails(id!);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress/>
      </Box>
    );
  }

  if (!job) {
    return (
      <Box>
        <Typography variant='h5' gutterBottom>Job #{id}</Typography>
        <Typography color='text.secondary'>Job not found.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant='h5' gutterBottom>
        Job #{job.id} — {job.baseUrl}
      </Typography>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Stack spacing={2}>
            <Box>
              <Typography variant='subtitle2' color='text.secondary'>Status</Typography>
              <Chip label={job.status} color='primary' variant='filled' sx={{ mt: 0.5 }}/>
            </Box>

            {job.description && (
              <Box>
                <Typography variant='subtitle2' color='text.secondary'>Description</Typography>
                <Typography variant='body2'>{job.description}</Typography>
              </Box>
            )}

            <Divider/>

            <Box>
              <Typography variant='subtitle2' color='text.secondary' gutterBottom>Seeds ({job.seeds.length})</Typography>
              <Stack spacing={1}>
                {job.seeds.map((seed) => (
                  <Box key={seed.id}>
                    <Chip label={seed.type} size='small' variant='outlined' sx={{ mr: 1 }}/>
                    <Typography variant='body2' component='span'>{seed.value}</Typography>
                  </Box>
                ))}
              </Stack>
            </Box>

            <Divider/>

            <Stack direction='row' spacing={3}>
              {job.startedAt && (
                <Box>
                  <Typography variant='caption' color='text.secondary'>Started</Typography>
                  <Typography variant='body2'>
                    {new Date(job.startedAt).toLocaleString()}
                  </Typography>
                </Box>
              )}
              {job.finishedAt && (
                <Box>
                  <Typography variant='caption' color='text.secondary'>Finished</Typography>
                  <Typography variant='body2'>
                    {new Date(job.finishedAt).toLocaleString()}
                  </Typography>
                </Box>
              )}
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Typography variant='h6' gutterBottom>Crawl Trace</Typography>
      <JobLogViewer logs={logs}/>
    </Box>
  );
};

export default JobDetailsPage;
