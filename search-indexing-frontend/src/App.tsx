import './App.css';
import { BrowserRouter, Outlet, Route, Routes } from 'react-router';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { muiTheme } from './styles/muiTheme.ts';
import Layout from './ui/components/layout/Layout/Layout.tsx';
import HomePage from './ui/pages/home/HomePage/HomePage.tsx';
import RegisterPage from './ui/pages/auth/RegisterPage/RegisterPage.tsx';
import LoginPage from './ui/pages/auth/LoginPage/LoginPage.tsx';
import ProtectedRoute from './ui/components/routing/ProtectedRoute/ProtectedRoute.tsx';
import JobsProvider from './providers/jobsProvider.tsx';
import JobsPage from './ui/pages/job/JobsPage/JobsPage.tsx';
import JobDetailsPage from './ui/pages/job/JobDetailsPage/JobDetailsPage.tsx';
import SearchPage from './ui/pages/search/SearchPage/SearchPage.tsx';
import DocumentsPage from './ui/pages/document/DocumentsPage/DocumentsPage.tsx';
import DocumentDetailsPage from './ui/pages/document/DocumentDetailsPage/DocumentDetailsPage.tsx';
import DonationsPage from './ui/pages/donation/DonationsPage/DonationsPage.tsx';

function App() {
  return (
    <ThemeProvider theme={muiTheme}>
      <CssBaseline/>
      <BrowserRouter>
        <Routes>
          <Route path='/register' element={<RegisterPage/>}/>
          <Route path='/login' element={<LoginPage/>}/>
          <Route path='/' element={<Layout/>}>
            <Route index element={<HomePage/>}/>
            <Route element={<ProtectedRoute/>}>
              <Route element={<JobsProvider><Outlet/></JobsProvider>}>
                <Route path='jobs' element={<JobsPage/>}/>
                <Route path='jobs/:id' element={<JobDetailsPage/>}/>
              </Route>
              <Route path='search' element={<SearchPage/>}/>
              <Route path='documents' element={<DocumentsPage/>}/>
              <Route path='documents/:id' element={<DocumentDetailsPage/>}/>
              <Route path='donations' element={<DonationsPage/>}/>
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
