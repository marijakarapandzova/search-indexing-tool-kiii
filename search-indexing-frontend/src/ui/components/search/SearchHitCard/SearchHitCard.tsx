import { Box, Link, Typography } from '@mui/material';
import type { SearchHitResponse } from '../../../../api/types/search.ts';

interface SearchHitCardProps {
  hit: SearchHitResponse;
  index: number;
  totalHits: number;
}

const SearchHitCard = ({ hit, index, totalHits }: SearchHitCardProps) => {
  const resourceType = hit.url.includes('.pdf') ? 'документ' : hit.url.includes('/page/') ? 'страница' : 'статија';

  return (
    <Box
      sx={{
        backgroundColor: '#F8F5EC',
        border: '1px solid #E4DCC7',
        p: '18px 20px 20px',
        position: 'relative',
        opacity: 0,
        animation: 'cardIn 0.5s ease forwards',
        [`&:nth-child(${index})`]: {
          animationDelay: `${index * 0.1}s`,
        },
        '&::after': {
          content: '""',
          position: 'absolute',
          left: 0,
          right: 0,
          bottom: -1,
          height: 6,
          backgroundImage: 'radial-gradient(circle at 6px 0, transparent 4px, #EFE9DA 4.5px)',
          backgroundSize: '12px 6px',
          backgroundRepeat: 'repeat-x',
        },
        '@keyframes cardIn': {
          from: { opacity: 0, transform: 'translateY(10px)' },
          to: { opacity: 1, transform: 'translateY(0)' },
        },
        '@media (prefers-reduced-motion: reduce)': {
          animation: 'none',
          opacity: 1,
        },
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.25 }}>
        <Typography sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.75rem', color: '#A23B33', letterSpacing: '0.05em' }}>
          {String(index).padStart(2, '0')} / {totalHits}
        </Typography>
        <Box
          sx={{
            fontFamily: '"JetBrains Mono", monospace',
            fontSize: '0.66rem',
            letterSpacing: '0.08em',
            textTransform: 'uppercase',
            padding: '2px 8px',
            border: '1px solid currentColor',
            borderRadius: '12px',
            color: resourceType === 'страница' ? '#8A8272' : '#2F6E64',
          }}
        >
          {resourceType}
        </Box>
      </Box>

      <Typography
        component='h3'
        sx={{
          fontFamily: '"PT Serif", Georgia, serif',
          fontSize: '1.12rem',
          lineHeight: 1.3,
          m: '0 0 8px',
        }}
      >
        <Link
          href={hit.url}
          target='_blank'
          rel='noopener'
          underline='none'
          sx={{
            color: 'inherit',
            '&:hover': { color: '#A23B33' },
          }}
        >
          {hit.title ?? hit.url}
        </Link>
      </Typography>

      {hit.snippet && (
        <Typography
          sx={{
            fontSize: '0.9rem',
            color: '#3C4656',
            m: '0 0 14px',
            '& mark': {
              backgroundColor: 'rgba(200,155,60,0.35)',
              color: 'inherit',
              padding: '0 2px',
            },
          }}
        >
          {hit.snippet}
        </Typography>
      )}

      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          fontFamily: '"JetBrains Mono", monospace',
          fontSize: '0.7rem',
          color: '#8A8272',
          borderTop: '1px dashed #E4DCC7',
          pt: 1.25,
        }}
      >
        <Typography sx={{ fontSize: '0.7rem', color: '#8A8272' }}>
          {hit.url}
        </Typography>
        {hit.macedonianConfidence !== null && (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.625 }}>
            <Box
              sx={{
                width: 7,
                height: 7,
                borderRadius: '50%',
                backgroundColor: hit.macedonianConfidence > 0.8 ? '#2F6E64' : '#A23B33',
              }}
            />
            <Typography sx={{ fontSize: '0.7rem', color: '#8A8272' }}>
              {(hit.macedonianConfidence * 100).toFixed(0)}%
            </Typography>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default SearchHitCard;
