import { useState } from 'react';
import { Box, TextField, Button, Slider, Typography } from '@mui/material';
import type { SearchParams } from '../../../../api/types/search.ts';

interface SearchBarProps {
  onSearch: (params: SearchParams) => void;
}

const SearchBar = ({ onSearch }: SearchBarProps) => {
  const [query, setQuery] = useState('');
  const [minConfidence, setMinConfidence] = useState<number>(0.55);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      onSearch({
        query: query.trim(),
        minMacedonianConfidence: minConfidence > 0 ? minConfidence : undefined
      });
    }
  };

  return (
    <Box component='form' onSubmit={handleSubmit} sx={{ mb: 2 }}>
      <Box
        sx={{
          position: 'relative',
          maxWidth: 760,
          mb: 1,
        }}
      >
        <Box
          sx={{
            backgroundColor: '#F8F5EC',
            border: '1px solid #E4DCC7',
            p: '22px 26px 24px',
            position: 'relative',
            boxShadow: '0 18px 40px -22px rgba(27,36,48,0.45)',
            '&::before': {
              content: '""',
              position: 'absolute',
              inset: '8px',
              border: '1px dashed rgba(162,59,51,0.35)',
              pointerEvents: 'none',
            },
          }}
        >
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', fontFamily: '"JetBrains Mono", monospace', fontSize: '0.72rem', letterSpacing: '0.08em', color: '#8A8272', textTransform: 'uppercase', mb: 1.75 }}>
            <span>Ticket <span style={{ color: '#A23B33' }}>№ 0047</span></span>
            <span>lucene · index-360stepeni</span>
          </Box>

          <Box sx={{ display: 'flex', gap: 1.25, mb: 2 }}>
            <TextField
              fullWidth
              placeholder='на пр. „избори локални 2026"'
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              sx={{
                '& .MuiOutlinedInput-root': {
                  backgroundColor: '#EFE9DA',
                  '& fieldset': { borderColor: '#E4DCC7' },
                },
                '& .MuiInputBase-input': {
                  fontFamily: '"PT Serif", Georgia, serif',
                  fontSize: '1.15rem',
                  p: '12px 14px',
                },
              }}
            />
            <Button
              type='submit'
              variant='contained'
              disabled={!query.trim()}
              sx={{
                fontFamily: '"PT Sans", sans-serif',
                fontWeight: 700,
                fontSize: '0.92rem',
                p: '0 24px',
                backgroundColor: '#A23B33',
                color: '#EFE9DA',
                letterSpacing: '0.02em',
                transition: 'transform .12s ease, background .15s ease',
                '&:hover': {
                  backgroundColor: '#8c332c',
                  transform: 'translateY(-1px)',
                },
                '&:active': {
                  transform: 'translateY(0)',
                },
              }}
            >
              Барај
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.75, fontFamily: '"JetBrains Mono", monospace', fontSize: '0.78rem', color: '#3C4656' }}>
            <label htmlFor='conf' style={{ whiteSpace: 'nowrap' }}>Праг на македонска доверба</label>
            <Slider
              id='conf'
              value={minConfidence}
              onChange={(_, value) => setMinConfidence(typeof value === 'number' ? value : 0.55)}
              min={0}
              max={1}
              step={0.05}
              sx={{
                flex: 1,
                maxWidth: 220,
                '& .MuiSlider-thumb': {
                  backgroundColor: '#2F6E64',
                },
                '& .MuiSlider-track': {
                  backgroundColor: '#2F6E64',
                },
                '& .MuiSlider-rail': {
                  backgroundColor: '#E4DCC7',
                },
              }}
            />
            <Box sx={{ color: '#2F6E64', fontWeight: 600, minWidth: '3ch' }}>
              {minConfidence.toFixed(2)}
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default SearchBar;
