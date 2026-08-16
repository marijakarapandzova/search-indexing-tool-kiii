import { Box, Button, CircularProgress, Grid, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useState } from 'react';
import useJobs from '../../../../hooks/useJobs.ts';
import JobCard from '../../../components/job/JobCard/JobCard.tsx';
import StartJobDialog from '../../../components/job/StartJobDialog/StartJobDialog.tsx';

/**
 * The crawl control panel. The data flow (useJobs -> JobCard) is
 * provided; TODO(student): finish StartJobDialog and JobCard.
 */
const JobsPage = () => {
  const { jobs, loading } = useJobs();

  const [newJobDialogOpen, setNewJobDialogOpen] = useState<boolean>(false);

  return (
    <Box>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress/>
        </Box>
      )}
      {!loading &&
       <>
         <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
           <Typography variant='h5'>Indexing Jobs</Typography>
           <Button variant='contained' startIcon={<AddIcon/>} onClick={() => setNewJobDialogOpen(true)}>
             New Job
           </Button>
         </Box>
         {jobs.length === 0 && (
           <Typography color='text.secondary'>
             No indexing jobs yet. Create one to start crawling your website.
           </Typography>
         )}
         <Grid container spacing={2}>
           {jobs.map((job) => (
             <Grid key={job.id} size={{ xs: 12, sm: 6, md: 4 }}>
               <JobCard job={job}/>
             </Grid>
           ))}
         </Grid>
         <StartJobDialog
           open={newJobDialogOpen}
           onClose={() => setNewJobDialogOpen(false)}
         />
       </>}
    </Box>
  );
};

export default JobsPage;
