import useAuth from '../../../../hooks/useAuth.ts';
import { Button } from '@mui/material';
import { useNavigate } from 'react-router';

const AuthToggle = () => {
  const { logout, isLoggedIn } = useAuth();

  const navigate = useNavigate();

  const handleLogin = () => {
    navigate('/login');
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Button
      onClick={isLoggedIn ? handleLogout : handleLogin}
      sx={isLoggedIn ? {
        backgroundColor: '#A23B33',
        color: '#EFE9DA',
        fontWeight: 700,
        fontSize: '0.92rem',
        padding: '12px 24px',
        letterSpacing: '0.02em',
        transition: 'transform 0.12s ease, background 0.15s ease',
        '&:hover': {
          backgroundColor: '#8c332c',
          transform: 'translateY(-1px)',
        },
      } : {
        color: '#EFE9DA',
        fontWeight: 700,
        fontSize: '0.92rem',
        padding: '12px 24px',
        letterSpacing: '0.02em',
        transition: 'transform 0.12s ease, opacity 0.15s ease',
        '&:hover': {
          opacity: 0.8,
        },
      }}
    >
      {isLoggedIn ? 'Одјава' : 'Најава'}
    </Button>
  );
};

export default AuthToggle;
