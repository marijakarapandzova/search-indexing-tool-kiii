import './Layout.css';
import { Box } from '@mui/material';
import { Outlet } from 'react-router';
import Header from '../Header/Header.tsx';
import StitchRule from '../../common/StitchRule.tsx';

const Layout = () => {
  return (
    <Box className='layout-box' sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header/>
      <StitchRule color='red'/>
      <Box sx={{ flex: 1, maxWidth: 1180, width: '100%', mx: 'auto', px: 3.5 }}>
        <Outlet/>
      </Box>
      <Box sx={{ mt: 'auto' }}>
        <footer style={{ backgroundColor: '#1B2430', color: '#EFE9DA', marginTop: '20px' }}>
          <Box sx={{ maxWidth: 1180, mx: 'auto', px: 3.5, py: 3.25, display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 1.25, fontFamily: '"JetBrains Mono", monospace', fontSize: '0.75rem', opacity: 0.7 }}>
            <span>Индекс360 · Search Indexing Tool · 360stepeni.mk</span>
            <span>lucene index · последно ажурирање: пред 6 мин.</span>
          </Box>
        </footer>
      </Box>
    </Box>
  );
};

export default Layout;
