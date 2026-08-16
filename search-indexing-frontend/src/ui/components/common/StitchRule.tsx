import { Box } from '@mui/material';

interface StitchRuleProps {
  color?: 'red' | 'teal' | 'gold';
}

const StitchRule = ({ color = 'red' }: StitchRuleProps) => {
  const colorMap = {
    red: '#A23B33',
    teal: '#2F6E64',
    gold: '#C89B3C',
  };

  return (
    <Box
      component='svg'
      viewBox='0 0 1200 14'
      preserveAspectRatio='none'
      sx={{
        width: '100%',
        height: '14px',
        display: 'block',
        '& path': {
          stroke: colorMap[color],
          strokeWidth: 2,
          fill: 'none',
          strokeLinecap: 'round',
          strokeDasharray: 1200,
          strokeDashoffset: 1200,
          animation: 'stitchDraw 1.6s ease-out forwards',
        },
        '@keyframes stitchDraw': {
          from: { strokeDashoffset: 1200 },
          to: { strokeDashoffset: 0 },
        },
        '@media (prefers-reduced-motion: reduce)': {
          '& path': {
            animation: 'none',
            strokeDashoffset: 0,
          },
        },
      }}
    >
      <path d="M0,7 L20,1 L40,13 L60,1 L80,13 L100,1 L120,13 L140,1 L160,13 L180,1 L200,13 L220,1 L240,13 L260,1 L280,13 L300,1 L320,13 L340,1 L360,13 L380,1 L400,13 L420,1 L440,13 L460,1 L480,13 L500,1 L520,13 L540,1 L560,13 L580,1 L600,13 L620,1 L640,13 L660,1 L680,13 L700,1 L720,13 L740,1 L760,13 L780,1 L800,13 L820,1 L840,13 L860,1 L880,13 L900,1 L920,13 L940,1 L960,13 L980,1 L1000,13 L1020,1 L1040,13 L1060,1 L1080,13 L1100,1 L1120,13 L1140,1 L1160,13 L1180,1 L1200,13"/>
    </Box>
  );
};

export default StitchRule;