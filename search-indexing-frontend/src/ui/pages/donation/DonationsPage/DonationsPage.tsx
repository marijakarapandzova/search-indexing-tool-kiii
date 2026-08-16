import { Box, Button, Card, CardContent, CircularProgress, Grid, Stack, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useState } from 'react';
import useDonations from '../../../../hooks/useDonations.ts';
import DonationBatchCard from '../../../components/donation/DonationBatchCard/DonationBatchCard.tsx';
import SubmitDonationDialog from '../../../components/donation/SubmitDonationDialog/SubmitDonationDialog.tsx';

const DonationsPage = () => {
  const { donations, loading } = useDonations();
  const [newBatchDialogOpen, setNewBatchDialogOpen] = useState<boolean>(false);

  const stats = {
    totalBatches: donations.length,
    totalDocuments: donations.reduce((sum, batch) => sum + batch.documentIds.length, 0),
    draft: donations.filter(b => b.status === 'DRAFT').length,
    approved: donations.filter(b => b.status === 'APPROVED').length,
    submitted: donations.filter(b => b.status === 'SUBMITTED').length,
    accepted: donations.filter(b => b.status === 'ACCEPTED').length,
    rejected: donations.filter(b => b.status === 'REJECTED').length,
    failed: donations.filter(b => b.status === 'FAILED').length
  };

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
           <Typography variant='h5'>Donations</Typography>
           <Button variant='contained' startIcon={<AddIcon/>} onClick={() => setNewBatchDialogOpen(true)}>
             New Batch
           </Button>
         </Box>

         {donations.length > 0 && (
           <Grid container spacing={2} sx={{ mb: 3 }}>
             <Grid size={{ xs: 12, sm: 6, md: 3 }}>
               <Card>
                 <CardContent>
                   <Typography color='text.secondary' gutterBottom>Total Batches</Typography>
                   <Typography variant='h5'>{stats.totalBatches}</Typography>
                 </CardContent>
               </Card>
             </Grid>
             <Grid size={{ xs: 12, sm: 6, md: 3 }}>
               <Card>
                 <CardContent>
                   <Typography color='text.secondary' gutterBottom>Total Documents</Typography>
                   <Typography variant='h5'>{stats.totalDocuments}</Typography>
                 </CardContent>
               </Card>
             </Grid>
             <Grid size={{ xs: 12, sm: 6, md: 3 }}>
               <Card>
                 <CardContent>
                   <Typography color='text.secondary' gutterBottom>Accepted</Typography>
                   <Typography variant='h5'>{stats.accepted}</Typography>
                 </CardContent>
               </Card>
             </Grid>
             <Grid size={{ xs: 12, sm: 6, md: 3 }}>
               <Card>
                 <CardContent>
                   <Typography color='text.secondary' gutterBottom>In Progress</Typography>
                   <Typography variant='h5'>{stats.draft + stats.approved + stats.submitted}</Typography>
                 </CardContent>
               </Card>
             </Grid>
           </Grid>
         )}

         {donations.length === 0 && (
           <Typography color='text.secondary'>
             No donation batches yet. Group indexed documents into a batch and
             donate them to doniraj.vezilka.ai.
           </Typography>
         )}
         <Grid container spacing={2}>
           {donations.map((batch) => (
             <Grid key={batch.id} size={{ xs: 12, sm: 6, md: 4 }}>
               <DonationBatchCard batch={batch}/>
             </Grid>
           ))}
         </Grid>
         <SubmitDonationDialog
           open={newBatchDialogOpen}
           onClose={() => setNewBatchDialogOpen(false)}
         />
       </>}
    </Box>
  );
};

export default DonationsPage;
