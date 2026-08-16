import { Box, TextField, MenuItem, Slider, FormControlLabel, Switch, Stack, Typography } from '@mui/material';
import type { DocumentFilter, ResourceType } from '../../../../api/types/document.ts';
import useJobs from '../../../../hooks/useJobs.ts';

interface DocumentFiltersProps {
  filter: DocumentFilter;
  onChange: (filter: DocumentFilter) => void;
}

const resourceTypes: ResourceType[] = ['ARTICLE', 'PAGE', 'DOCUMENT', 'MEDIA'];

const DocumentFilters = ({ filter, onChange }: DocumentFiltersProps) => {
  const { jobs } = useJobs();

  const handleJobChange = (jobId: number | '') => {
    onChange({ ...filter, jobId: jobId === '' ? undefined : jobId });
  };

  const handleResourceTypeChange = (resourceType: ResourceType | '') => {
    onChange({ ...filter, resourceType: resourceType === '' ? undefined : resourceType });
  };

  const handleConfidenceChange = (_: Event, value: number | number[]) => {
    const confidence = typeof value === 'number' ? value : value[0];
    onChange({ ...filter, minMacedonianConfidence: confidence === 0 ? undefined : confidence });
  };

  const handleDonatedChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange({ ...filter, donated: e.target.checked ? true : undefined });
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange({ ...filter, search: e.target.value || undefined });
  };

  return (
    <Box sx={{ mb: 3 }}>
      <Stack spacing={2}>
        <TextField
          select
          label='Job'
          value={filter.jobId ?? ''}
          onChange={(e) => handleJobChange(e.target.value === '' ? '' : parseInt(e.target.value))}
          fullWidth
        >
          <MenuItem value=''>All Jobs</MenuItem>
          {jobs && jobs.map((job) => (
            <MenuItem key={job.id} value={job.id}>
              {job.baseUrl}
            </MenuItem>
          ))}
        </TextField>

        <TextField
          select
          label='Resource Type'
          value={filter.resourceType ?? ''}
          onChange={(e) => handleResourceTypeChange(e.target.value === '' ? '' : e.target.value as ResourceType)}
          fullWidth
        >
          <MenuItem value=''>All Types</MenuItem>
          {resourceTypes.map((type) => (
            <MenuItem key={type} value={type}>
              {type}
            </MenuItem>
          ))}
        </TextField>

        <Box>
          <Typography variant='caption' color='text.secondary'>
            Min Macedonian Confidence: {filter.minMacedonianConfidence ? `${(filter.minMacedonianConfidence * 100).toFixed(0)}%` : 'Any'}
          </Typography>
          <Slider
            value={filter.minMacedonianConfidence ?? 0}
            onChange={handleConfidenceChange}
            min={0}
            max={1}
            step={0.1}
            marks={[
              { value: 0, label: '0%' },
              { value: 0.5, label: '50%' },
              { value: 1, label: '100%' }
            ]}
            valueLabelFormat={(value) => `${(value * 100).toFixed(0)}%`}
            valueLabelDisplay='auto'
          />
        </Box>

        <FormControlLabel
          control={<Switch checked={filter.donated ?? false} onChange={handleDonatedChange}/>}
          label='Only Donated Documents'
        />

        <TextField
          label='Search Content'
          placeholder='Type to filter by content...'
          value={filter.search ?? ''}
          onChange={handleSearchChange}
          fullWidth
        />
      </Stack>
    </Box>
  );
};

export default DocumentFilters;
