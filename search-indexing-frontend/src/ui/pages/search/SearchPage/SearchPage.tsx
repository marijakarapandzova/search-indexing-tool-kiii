import { Box, Typography } from '@mui/material';
import useSearch from '../../../../hooks/useSearch.ts';
import SearchBar from '../../../components/search/SearchBar/SearchBar.tsx';
import SearchResults from '../../../components/search/SearchResults/SearchResults.tsx';
import StitchRule from '../../../components/common/StitchRule.tsx';

/**
 * The full-text search page — the headline feature. The data flow
 * (useSearch -> SearchBar / SearchResults) is wired; TODO(student): finish
 * SearchBar, SearchResults and SearchHitCard, backed by documentApi.search.
 */
const SearchPage = () => {
  const { result, loading, search } = useSearch();

  return (
    <Box sx={{ py: 4.75 }}>
      <Box sx={{ mb: 1.25 }}>
        <Typography sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.78rem', letterSpacing: '0.12em', textTransform: 'uppercase', color: '#2F6E64', mb: 1.25 }}>
          Локален пребарувач · Македонски веб-ресурси
        </Typography>
      </Box>
      <Typography variant='h1' sx={{ mb: 1.5, maxWidth: '16ch', fontFamily: '"PT Serif", Georgia, serif', fontSize: 'clamp(2rem, 4vw, 3.1rem)', fontWeight: 700 }}>
        Извезено од веб. Пребарливо веднаш.
      </Typography>
      <Typography sx={{ color: '#3C4656', maxWidth: '56ch', fontSize: '1.02rem', mb: 4.25 }}>
        Индекс360 обиколува страници, ги препознава на македонски јазик и ги плете во еден брз, локален индекс.
      </Typography>

      <SearchBar onSearch={search}/>

      <StitchRule color='teal'/>

      <Box sx={{ py: 4.75 }}>
        <Box sx={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', flexWrap: 'wrap', gap: 1.25, mb: 2.75 }}>
          <Typography variant='h2' sx={{ fontFamily: '"PT Serif", Georgia, serif', fontSize: '1.5rem', m: 0 }}>
            Резултати
          </Typography>
          {result && (
            <Typography sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#8A8272' }}>
              <strong style={{ color: '#1B2430' }}>{result.totalHits}</strong> погодоци
            </Typography>
          )}
        </Box>
        <SearchResults result={result} loading={loading}/>
      </Box>

      <StitchRule color='gold'/>
    </Box>
  );
};

export default SearchPage;
