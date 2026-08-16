import { Box, Card, CardContent, CircularProgress, Grid, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import useJobs from '../../../../hooks/useJobs.ts';
import documentApi from '../../../../api/documentApi.ts';
import StitchRule from '../../../components/common/StitchRule.tsx';
import JobStamps from '../../../components/job/JobStamps.tsx';

const HomePage = () => {
  const { jobs } = useJobs();
  const [stats, setStats] = useState<{
    totalDocuments: number;
    macedonianDocuments: number;
    donatedDocuments: number;
  }>({ totalDocuments: 0, macedonianDocuments: 0, donatedDocuments: 0 });
  const [statsLoading, setStatsLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const allDocs = await documentApi.findAll({}, 0, 10000);
      const totalDocs = allDocs.data.totalElements;
      const macedonianDocs = allDocs.data.content.filter(d => d.macedonianConfidence && d.macedonianConfidence > 0.8).length;
      const donatedDocs = allDocs.data.content.filter(d => d.donationBatchId !== null).length;

      setStats({
        totalDocuments: totalDocs,
        macedonianDocuments: macedonianDocs,
        donatedDocuments: donatedDocs
      });
    } catch {
      // Stats fetch failed, show 0s
    } finally {
      setStatsLoading(false);
    }
  };

  return (
    <Box sx={{ py: 4.75 }}>
      <Box sx={{ mb: 1.25 }}>
        <Typography sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.78rem', letterSpacing: '0.12em', textTransform: 'uppercase', color: '#2F6E64', mb: 1.25 }}>
          Локален пребарувач · Македонски веб-ресурси
        </Typography>
      </Box>
      <Typography variant='h1' sx={{ mb: 1.5, fontFamily: '"PT Serif", Georgia, serif', fontSize: 'clamp(2rem, 4vw, 3.1rem)', fontWeight: 700 }}>
        Извезено од веб. Пребарливо веднаш.
      </Typography>
      <Typography sx={{ color: '#3C4656', maxWidth: '56ch', fontSize: '1.02rem', mb: 4.25 }}>
        Индекс360 обиколува страници од 360stepeni.mk, ги препознава оние напишани на македонски јазик и ги плете во еден брз, локален пребарувачки индекс — подготвен и за донирање кон Vezilka.
      </Typography>

      <StitchRule color='red'/>

      <Box sx={{ py: 4.75 }}>
        <Box sx={{ mb: 2.75 }}>
          <Typography variant='h2' sx={{ fontFamily: '"PT Serif", Georgia, serif', fontSize: '1.5rem', m: 0, mb: 2.75 }}>
            Преглед на индекс
          </Typography>
        </Box>

        <Grid container spacing={2.25}>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card>
              <CardContent>
                <Typography color='text.secondary' gutterBottom>
                  Вкупно документи
                </Typography>
                {statsLoading ? (
                  <CircularProgress size={24}/>
                ) : (
                  <Typography variant='h5'>{stats.totalDocuments}</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card>
              <CardContent>
                <Typography color='text.secondary' gutterBottom>
                  Македонски &gt; 80%
                </Typography>
                {statsLoading ? (
                  <CircularProgress size={24}/>
                ) : (
                  <Typography variant='h5'>{stats.macedonianDocuments}</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card>
              <CardContent>
                <Typography color='text.secondary' gutterBottom>
                  Донирани
                </Typography>
                {statsLoading ? (
                  <CircularProgress size={24}/>
                ) : (
                  <Typography variant='h5'>{stats.donatedDocuments}</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card>
              <CardContent>
                <Typography color='text.secondary' gutterBottom>
                  Статус на работа
                </Typography>
                {statsLoading ? (
                  <CircularProgress size={24}/>
                ) : jobs && jobs.length > 0 ? (
                  <Typography variant='h5'>#{jobs[0].id}</Typography>
                ) : (
                  <Typography variant='body2' color='text.secondary'>Нема задачи</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>

      <StitchRule color='teal'/>

      <Box sx={{ py: 4.75 }}>
        <Box sx={{ mb: 2.75 }}>
          <Typography variant='h2' sx={{ fontFamily: '"PT Serif", Georgia, serif', fontSize: '1.5rem', m: 0, mb: 2.75 }}>
            Статус на задачи
          </Typography>
          <Typography sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#8A8272' }}>
            последни <strong style={{ color: '#1B2430' }}>3</strong> задачи за индексирање
          </Typography>
        </Box>

        {jobs && jobs.length > 0 ? (
          <JobStamps jobs={jobs} limit={3}/>
        ) : (
          <Typography color='text.secondary'>
            Нема завршени задачи. Посетете го страницата „Задачи" за да почнете со индексирање.
          </Typography>
        )}
      </Box>

      <StitchRule color='gold'/>
    </Box>
  );
};

export default HomePage;
