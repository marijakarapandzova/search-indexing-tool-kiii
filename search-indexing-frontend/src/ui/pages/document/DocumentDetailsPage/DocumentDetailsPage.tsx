import { useEffect, useState } from 'react';
import { Box, Card, CardContent, Chip, CircularProgress, Divider, Grid, Link, Stack, Typography } from '@mui/material';
import { useParams } from 'react-router';
import documentApi from '../../../../api/documentApi.ts';
import type { DocumentResponse } from '../../../../api/types/document.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';

const DocumentDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const { showSnackbar } = useSnackbar();
  const [document, setDocument] = useState<DocumentResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDocument();
  }, [id]);

  const fetchDocument = async () => {
    try {
      const response = await documentApi.findById(id!);
      setDocument(response.data);
    } catch (err) {
      showSnackbar('Failed to load document', 'error');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress/>
      </Box>
    );
  }

  if (!document) {
    return (
      <Box>
        <Typography variant='h5' gutterBottom>Document #{id}</Typography>
        <Typography color='text.secondary'>Document not found.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant='h5' gutterBottom>
        {document.title || document.url}
      </Typography>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Card sx={{ mb: 2 }}>
            <CardContent>
              <Stack spacing={2}>
                <Box>
                  <Typography variant='subtitle2' color='text.secondary'>Source</Typography>
                  <Link href={document.url} target='_blank' rel='noopener'>
                    {document.url}
                  </Link>
                </Box>

                <Divider/>

                <Box>
                  <Typography variant='subtitle2' color='text.secondary' gutterBottom>Content</Typography>
                  <Typography variant='body2' sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>
                    {document.content || 'No content available'}
                  </Typography>
                </Box>
              </Stack>
            </CardContent>
          </Card>

          {document.mediaItems.length > 0 && (
            <Card>
              <CardContent>
                <Typography variant='subtitle2' color='text.secondary' gutterBottom>
                  Media ({document.mediaItems.length})
                </Typography>
                <Stack spacing={1}>
                  {document.mediaItems.map((item) => (
                    <Box key={item.id}>
                      <Chip label={item.type} size='small' variant='outlined' sx={{ mr: 1 }}/>
                      {item.storagePath ? (
                        <Link href={item.storagePath} target='_blank' rel='noopener'>
                          {item.sourceUrl}
                        </Link>
                      ) : (
                        <Typography variant='caption' color='text.secondary'>
                          {item.sourceUrl}
                        </Typography>
                      )}
                    </Box>
                  ))}
                </Stack>
              </CardContent>
            </Card>
          )}
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Card>
            <CardContent>
              <Stack spacing={2}>
                <Box>
                  <Typography variant='caption' color='text.secondary'>Resource Type</Typography>
                  <Chip label={document.resourceType} size='small' variant='outlined' sx={{ mt: 0.5 }}/>
                </Box>

                {document.macedonianConfidence !== null && (
                  <Box>
                    <Typography variant='caption' color='text.secondary'>Macedonian Confidence</Typography>
                    <Typography variant='h6'>
                      {(document.macedonianConfidence * 100).toFixed(1)}%
                    </Typography>
                  </Box>
                )}

                <Divider/>

                <Box>
                  <Typography variant='caption' color='text.secondary'>Donation Status</Typography>
                  {document.donationBatchId !== null ? (
                    <Chip
                      label={`Batch #${document.donationBatchId}`}
                      color='success'
                      variant='filled'
                      size='small'
                      sx={{ mt: 0.5 }}
                    />
                  ) : (
                    <Chip
                      label='Not Donated'
                      color='default'
                      variant='outlined'
                      size='small'
                      sx={{ mt: 0.5 }}
                    />
                  )}
                </Box>

                <Divider/>

                {document.indexedAt && (
                  <Box>
                    <Typography variant='caption' color='text.secondary'>Indexed</Typography>
                    <Typography variant='body2'>
                      {new Date(document.indexedAt).toLocaleString()}
                    </Typography>
                  </Box>
                )}
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DocumentDetailsPage;
