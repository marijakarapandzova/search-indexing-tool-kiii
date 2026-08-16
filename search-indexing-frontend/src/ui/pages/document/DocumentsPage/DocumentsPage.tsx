import { Box, CircularProgress, Pagination, Stack, Typography } from '@mui/material';
import { useState } from 'react';
import type { DocumentFilter } from '../../../../api/types/document.ts';
import useDocuments from '../../../../hooks/useDocuments.ts';
import DocumentFilters from '../../../components/document/DocumentFilters/DocumentFilters.tsx';
import DocumentGrid from '../../../components/document/DocumentGrid/DocumentGrid.tsx';

const DocumentsPage = () => {
  const [filter, setFilter] = useState<DocumentFilter>({});
  const [page, setPage] = useState<number>(0);
  const pageSize = 12;

  const { documents, loading, onDelete } = useDocuments(filter, page, pageSize);

  const handlePageChange = (_: React.ChangeEvent<unknown>, pageNumber: number) => {
    setPage(pageNumber - 1);
  };

  return (
    <Box>
      <Typography variant='h5' sx={{ mb: 2 }}>Indexed Documents</Typography>
      <DocumentFilters filter={filter} onChange={(newFilter) => {
        setFilter(newFilter);
        setPage(0);
      }}/>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress/>
        </Box>
      )}
      {!loading && (!documents || documents.content.length === 0) && (
        <Typography color='text.secondary'>
          No indexed documents yet. Run an indexing job first.
        </Typography>
      )}
      {!loading && documents && (
        <Stack spacing={2}>
          <DocumentGrid documents={documents.content} onDelete={onDelete}/>
          {documents.totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <Pagination
                count={documents.totalPages}
                page={page + 1}
                onChange={handlePageChange}
              />
            </Box>
          )}
        </Stack>
      )}
    </Box>
  );
};

export default DocumentsPage;
