import { Card, CardActions, CardContent, Button, Chip, Stack, Typography } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SendIcon from '@mui/icons-material/Send';
import type { DonationBatchResponse, DonationStatus } from '../../../../api/types/donation.ts';
import useDonations from '../../../../hooks/useDonations.ts';

interface DonationBatchCardProps {
  batch: DonationBatchResponse;
}

const getStatusColor = (status: DonationStatus): 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success' => {
  switch (status) {
    case 'DRAFT':
      return 'default';
    case 'APPROVED':
      return 'info';
    case 'SUBMITTED':
      return 'primary';
    case 'ACCEPTED':
      return 'success';
    case 'REJECTED':
      return 'error';
    case 'FAILED':
      return 'error';
    default:
      return 'default';
  }
};

const DonationBatchCard = ({ batch }: DonationBatchCardProps) => {
  const { onApprove, onSubmit } = useDonations();

  const canApprove = batch.status === 'DRAFT';
  const canSubmit = batch.status === 'APPROVED';

  return (
    <Card>
      <CardContent>
        <Stack spacing={1}>
          <Stack direction='row' sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant='h6'>Batch #{batch.id}</Typography>
            <Chip label={batch.status} size='small' color={getStatusColor(batch.status)} variant='filled'/>
          </Stack>

          <Typography variant='body2' color='text.secondary'>
            Documents: {batch.documentIds.length}
          </Typography>

          {batch.submittedAt && (
            <Typography variant='caption' color='text.secondary'>
              Submitted: {new Date(batch.submittedAt).toLocaleString()}
            </Typography>
          )}

          {batch.vezilkaReference && (
            <Stack direction='row' spacing={1} sx={{ alignItems: 'center' }}>
              <CheckCircleIcon sx={{ fontSize: '1rem', color: 'success.main' }}/>
              <Typography variant='caption'>
                Reference: {batch.vezilkaReference}
              </Typography>
            </Stack>
          )}

          <Typography variant='caption' color='text.secondary'>
            Created: {new Date(batch.createdAt).toLocaleString()}
          </Typography>
        </Stack>
      </CardContent>
      <CardActions>
        <Button
          startIcon={<CheckCircleIcon/>}
          onClick={() => onApprove(batch.id)}
          disabled={!canApprove}
          size='small'
        >
          Approve
        </Button>
        <Button
          startIcon={<SendIcon/>}
          color='success'
          onClick={() => onSubmit(batch.id)}
          disabled={!canSubmit}
          size='small'
        >
          Submit
        </Button>
      </CardActions>
    </Card>
  );
};

export default DonationBatchCard;
