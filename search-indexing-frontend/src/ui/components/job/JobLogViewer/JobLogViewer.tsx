import { Box, Chip, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import type { CrawlActionLogResponse } from '../../../../api/types/job.ts';

interface JobLogViewerProps {
  logs: CrawlActionLogResponse[];
}

const JobLogViewer = ({ logs }: JobLogViewerProps) => {
  if (logs.length === 0) {
    return (
      <Typography color='text.secondary'>
        No crawl actions logged yet.
      </Typography>
    );
  }

  return (
    <TableContainer>
      <Table size='small'>
        <TableHead>
          <TableRow sx={{ backgroundColor: 'action.hover' }}>
            <TableCell>Action Type</TableCell>
            <TableCell>Details</TableCell>
            <TableCell align='center'>Status</TableCell>
            <TableCell>Timestamp</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {logs.map((log) => (
            <TableRow key={log.id}>
              <TableCell>
                <Chip label={log.actionType} size='small' variant='outlined'/>
              </TableCell>
              <TableCell>
                <Typography variant='body2' color='text.secondary'>
                  {log.details ?? '—'}
                </Typography>
              </TableCell>
              <TableCell align='center'>
                {log.successful ? (
                  <CheckCircleIcon sx={{ color: 'success.main' }}/>
                ) : (
                  <ErrorIcon sx={{ color: 'error.main' }}/>
                )}
              </TableCell>
              <TableCell>
                <Typography variant='caption'>
                  {new Date(log.occurredAt).toLocaleString()}
                </Typography>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default JobLogViewer;
