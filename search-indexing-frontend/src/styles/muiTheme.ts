import { createTheme } from '@mui/material/styles';

const threadRed = '#A23B33';
const threadTeal = '#2F6E64';
const gold = '#C89B3C';
const paper = '#EFE9DA';
const paperDeep = '#E4DCC7';
const ink = '#1B2430';
const inkSoft = '#3C4656';
const muted = '#8A8272';
const card = '#F8F5EC';

export const muiTheme = createTheme({
  palette: {
    primary: {
      main: threadTeal,
      light: '#4a9b8f',
      dark: '#1f5049',
    },
    secondary: {
      main: threadRed,
      light: '#c86a60',
      dark: '#7a2420',
    },
    background: {
      default: paper,
      paper: card,
    },
    text: {
      primary: ink,
      secondary: inkSoft,
    },
    divider: paperDeep,
    action: {
      active: threadTeal,
      hover: 'rgba(47, 110, 100, 0.08)',
      selected: 'rgba(47, 110, 100, 0.12)',
      disabled: muted,
      disabledBackground: paperDeep,
    },
    success: {
      main: threadTeal,
    },
    warning: {
      main: gold,
    },
    error: {
      main: threadRed,
    },
    info: {
      main: threadTeal,
    },
  },
  typography: {
    fontFamily: '"PT Sans", "Segoe UI", sans-serif',
    h1: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontSize: 'clamp(1.8rem, 4vw, 2.8rem)',
      fontWeight: 700,
      lineHeight: 1.1,
    },
    h2: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontSize: '1.5rem',
      fontWeight: 700,
      lineHeight: 1.2,
    },
    h3: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontSize: '1.2rem',
      fontWeight: 700,
      lineHeight: 1.3,
    },
    h4: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontWeight: 700,
    },
    h5: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontWeight: 700,
    },
    h6: {
      fontFamily: '"PT Serif", Georgia, serif',
      fontWeight: 700,
    },
    body1: {
      fontSize: '1rem',
      lineHeight: 1.5,
    },
    body2: {
      fontSize: '0.9rem',
      lineHeight: 1.5,
    },
    button: {
      fontWeight: 700,
      letterSpacing: '0.02em',
      textTransform: 'none',
    },
    caption: {
      fontFamily: '"JetBrains Mono", "Courier New", monospace',
      fontSize: '0.75rem',
      letterSpacing: '0.08em',
    },
  },
  shape: {
    borderRadius: 2,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          padding: '12px 24px',
          transition: 'transform 0.12s ease, background 0.15s ease',
          '&:hover': {
            transform: 'translateY(-1px)',
          },
        },
        contained: {
          backgroundColor: threadRed,
          color: paper,
          '&:hover': {
            backgroundColor: '#8c332c',
          },
        },
        outlined: {
          borderColor: paperDeep,
          color: ink,
          '&:hover': {
            borderColor: threadTeal,
            backgroundColor: 'rgba(47, 110, 100, 0.06)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: card,
          border: `1px solid ${paperDeep}`,
          borderRadius: 2,
          boxShadow: '0 18px 40px -22px rgba(27, 36, 48, 0.45)',
          position: 'relative',
          '&::after': {
            content: '""',
            position: 'absolute',
            left: 0,
            right: 0,
            bottom: -1,
            height: 6,
            backgroundImage: `radial-gradient(circle at 6px 0, transparent 4px, ${paper} 4.5px)`,
            backgroundSize: '12px 6px',
            backgroundRepeat: 'repeat-x',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundColor: card,
          borderColor: paperDeep,
        },
        outlined: {
          borderColor: paperDeep,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            backgroundColor: paper,
            '& fieldset': {
              borderColor: paperDeep,
            },
            '&:hover fieldset': {
              borderColor: threadTeal,
            },
            '&.Mui-focused fieldset': {
              borderColor: threadTeal,
              borderWidth: 1,
            },
          },
          '& .MuiInputBase-input': {
            fontFamily: '"PT Serif", Georgia, serif',
            color: ink,
            '&::placeholder': {
              color: muted,
              opacity: 1,
            },
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          backgroundColor: 'rgba(47, 110, 100, 0.1)',
          borderColor: threadTeal,
          color: inkSoft,
          fontFamily: '"JetBrains Mono", "Courier New", monospace',
          fontSize: '0.75rem',
          fontWeight: 600,
        },
      },
    },
    MuiDivider: {
      styleOverrides: {
        root: {
          borderColor: paperDeep,
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          backgroundColor: paperDeep,
          '& .MuiLinearProgress-bar': {
            backgroundColor: threadTeal,
          },
        },
      },
    },
    MuiCircularProgress: {
      styleOverrides: {
        root: {
          color: threadTeal,
        },
      },
    },
  },
});
