import {
  Box, Button, Drawer, IconButton, List, ListItem, ListItemButton, ListItemText, Typography
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { Link } from 'react-router';
import { useState } from 'react';
import AuthToggle from '../../auth/AuthToggle/AuthToggle.tsx';
import useAuth from '../../../../hooks/useAuth.ts';
import type { Role } from '../../../../api/types/user.ts';

interface Page {
  path: string;
  name: string;
  authenticated: boolean;
  role?: Role;
}

const pages: Page[] = [
  { path: '/', name: 'Почетна', authenticated: false },
  { path: '/search', name: 'Пребарување', authenticated: true },
  { path: '/jobs', name: 'Задачи', authenticated: true },
  { path: '/documents', name: 'Документи', authenticated: true },
  { path: '/donations', name: 'Донации', authenticated: true }
];

const Header = () => {
  const [drawerOpen, setDrawerOpen] = useState(false);

  const { isLoggedIn, user } = useAuth();
  const visiblePages = pages.filter((page) =>
    (!page.authenticated || isLoggedIn) &&
    (!page.role || (user?.roles.includes(page.role) ?? false))
  );

  return (
    <Box sx={{ backgroundColor: '#1B2430', color: '#EFE9DA' }}>
      <Box
        sx={{
          maxWidth: 1180,
          mx: 'auto',
          px: 3.5,
          py: 2.25,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: 2,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 1.25 }}>
          <Typography sx={{ fontSize: '1.05rem', color: '#C89B3C', letterSpacing: '0.14em' }}>
            ⁘
          </Typography>
          <Typography
            sx={{
              fontSize: '1.55rem',
              fontWeight: 700,
              fontFamily: '"PT Serif", Georgia, serif',
              '& em': { fontStyle: 'italic', color: '#A23B33' },
            }}
          >
            Индекс<em>360</em>
          </Typography>
        </Box>

        <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 3.25, fontSize: '0.92rem' }}>
          {visiblePages.map((page) => (
            <Link key={page.name} to={page.path} style={{ textDecoration: 'none' }}>
              <Box
                component='span'
                sx={{
                  color: '#EFE9DA',
                  opacity: 0.78,
                  paddingBottom: '4px',
                  borderBottom: '2px solid transparent',
                  transition: 'opacity .15s ease, border-color .15s ease',
                  cursor: 'pointer',
                  '&:hover': {
                    opacity: 1,
                  },
                  '&.active': {
                    opacity: 1,
                    borderColor: '#A23B33',
                  },
                }}
              >
                {page.name}
              </Box>
            </Link>
          ))}
        </Box>

        <Box sx={{ display: { xs: 'flex', md: 'none' } }}>
          <IconButton
            size='large'
            color='inherit'
            aria-label='menu'
            onClick={() => setDrawerOpen(true)}
          >
            <MenuIcon/>
          </IconButton>
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
          <AuthToggle/>
        </Box>
      </Box>

      <Drawer anchor='left' open={drawerOpen} onClose={() => setDrawerOpen(false)}>
        <Box sx={{ width: 240, backgroundColor: '#1B2430', color: '#EFE9DA', height: '100%' }} role='presentation' onClick={() => setDrawerOpen(false)}>
          <List>
            {visiblePages.map((page) => (
              <ListItem key={page.name} disablePadding>
                <ListItemButton component={Link} to={page.path}>
                  <ListItemText primary={page.name}/>
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Box>
      </Drawer>
    </Box>
  );
};

export default Header;
