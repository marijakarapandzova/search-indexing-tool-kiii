import { Card, CardActions, CardContent, Button, Link, Stack, Typography, Chip, Box } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import { useNavigate } from 'react-router';
import type { DocumentResponse } from '../../../../api/types/document.ts';

interface DocumentCardProps {
  document: DocumentResponse;
  onDelete?: (id: number) => void;
}

const DocumentCard = ({ document, onDelete }: DocumentCardProps) => {
  const navigate = useNavigate();

  const contentPreview = document.content
    ? document.content.substring(0, 150) + (document.content.length > 150 ? '...' : '')
    : null;

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', gap: 1 }}>
        <Link
          component='button'
          variant='h6'
          onClick={() => navigate(`/documents/${document.id}`)}
          sx={{ textAlign: 'left', cursor: 'pointer' }}
        >
          {document.title ?? document.url}
        </Link>

        <Link href={document.url} target='_blank' rel='noopener' underline='hover'>
          <Typography variant='caption' color='primary'>
            {document.url}
            <OpenInNewIcon sx={{ fontSize: '0.75rem', ml: 0.5, verticalAlign: 'text-bottom' }}/>
          </Typography>
        </Link>

        {contentPreview && (
          <Typography variant='body2' color='text.secondary' sx={{ mb: 1 }}>
            {contentPreview}
          </Typography>
        )}

        <Stack direction='row' spacing={1} sx={{ mb: 1 }}>
          <Chip label={document.resourceType} size='small' variant='outlined'/>
          {document.macedonianConfidence !== null && (
            <Chip
              label={`MK: ${(document.macedonianConfidence * 100).toFixed(0)}%`}
              size='small'
              color={document.macedonianConfidence > 0.8 ? 'success' : 'default'}
              variant='outlined'
            />
          )}
          {document.donationBatchId !== null && (
            <Chip label={`Donation #${document.donationBatchId}`} size='small' color='info' variant='filled'/>
          )}
        </Stack>

        {document.mediaItems.length > 0 && (
          <Box>
            <Typography variant='caption' color='text.secondary'>Media: {document.mediaItems.length} item(s)</Typography>
          </Box>
        )}
      </CardContent>
      <CardActions>
        <Button
          size='small'
          onClick={() => navigate(`/documents/${document.id}`)}
        >
          View
        </Button>
        {onDelete && (
          <Button
            size='small'
            startIcon={<DeleteIcon/>}
            color='error'
            onClick={() => onDelete(document.id)}
          >
            Delete
          </Button>
        )}
      </CardActions>
    </Card>
  );
};

export default DocumentCard;
