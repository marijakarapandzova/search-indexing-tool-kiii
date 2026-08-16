import { Box, CircularProgress, Typography } from '@mui/material';
import type { SearchResultResponse } from '../../../../api/types/search.ts';
import SearchHitCard from '../SearchHitCard/SearchHitCard.tsx';

interface SearchResultsProps {
  result: SearchResultResponse | null;
  loading: boolean;
}

const SearchResults = ({ result, loading }: SearchResultsProps) => {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress/>
      </Box>
    );
  }

  if (!result) {
    return (
      <Typography color='text.secondary'>
        Внесете барање погоре за пребарување на индексираните документи.
      </Typography>
    );
  }

  if (result.hits.length === 0) {
    return (
      <Typography color='text.secondary'>
        Нема документи што се совпаѓаат со "{result.query}".
      </Typography>
    );
  }

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        gap: 2.25,
      }}
    >
      {result.hits.map((hit, index) => (
        <SearchHitCard key={hit.docId} hit={hit} index={index + 1} totalHits={result.totalHits}/>
      ))}
    </Box>
  );
};

export default SearchResults;
