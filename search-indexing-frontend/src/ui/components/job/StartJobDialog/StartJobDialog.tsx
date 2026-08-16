import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  TextField,
  Stack,
  Box,
  MenuItem,
  IconButton
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import type { CreateJobRequest, SeedType } from '../../../../api/types/job.ts';
import useJobs from '../../../../hooks/useJobs.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';

interface StartJobDialogProps {
  open: boolean;
  onClose: () => void;
}

const seedTypes: SeedType[] = ['URL', 'SITEMAP', 'SECTION'];

const StartJobDialog = ({ open, onClose }: StartJobDialogProps) => {
  const { onCreate } = useJobs();
  const { showSnackbar } = useSnackbar();

  const [baseUrl, setBaseUrl] = useState('');
  const [description, setDescription] = useState('');
  const [seeds, setSeeds] = useState<Array<{ type: SeedType; value: string }>>([
    { type: 'URL', value: '' }
  ]);

  const handleAddSeed = () => {
    setSeeds([...seeds, { type: 'URL', value: '' }]);
  };

  const handleRemoveSeed = (index: number) => {
    setSeeds(seeds.filter((_, i) => i !== index));
  };

  const handleSeedChange = (index: number, field: 'type' | 'value', newValue: string) => {
    const newSeeds = [...seeds];
    if (field === 'type') {
      newSeeds[index].type = newValue as SeedType;
    } else {
      newSeeds[index].value = newValue;
    }
    setSeeds(newSeeds);
  };

  const handleSubmit = async () => {
    if (!baseUrl.trim()) {
      showSnackbar('Base URL is required', 'error');
      return;
    }

    const validSeeds = seeds.filter((s) => s.value.trim());
    if (validSeeds.length === 0) {
      showSnackbar('At least one seed is required', 'error');
      return;
    }

    const data: CreateJobRequest = {
      baseUrl: baseUrl.trim(),
      description: description.trim(),
      seeds: validSeeds
    };

    try {
      await onCreate(data);
      handleClose();
      showSnackbar('Job created successfully', 'success');
    } catch {
      showSnackbar('Failed to create job', 'error');
    }
  };

  const handleClose = () => {
    setBaseUrl('');
    setDescription('');
    setSeeds([{ type: 'URL', value: '' }]);
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth='sm'>
      <DialogTitle>New Indexing Job</DialogTitle>
      <DialogContent sx={{ pt: 2 }}>
        <Stack spacing={2}>
          <TextField
            label='Base URL'
            placeholder='https://example.mk'
            value={baseUrl}
            onChange={(e) => setBaseUrl(e.target.value)}
            fullWidth
            autoFocus
          />

          <TextField
            label='Description'
            placeholder='Optional job description'
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
            multiline
            rows={2}
          />

          <Box>
            <Stack spacing={1}>
              {seeds.map((seed, index) => (
                <Stack key={index} direction='row' spacing={1} sx={{ alignItems: 'flex-start' }}>
                  <TextField
                    select
                    label='Type'
                    value={seed.type}
                    onChange={(e) => handleSeedChange(index, 'type', e.target.value)}
                    size='small'
                    sx={{ minWidth: 120 }}
                  >
                    {seedTypes.map((type) => (
                      <MenuItem key={type} value={type}>
                        {type}
                      </MenuItem>
                    ))}
                  </TextField>

                  <TextField
                    label='Value'
                    placeholder='URL, sitemap URL, or section path'
                    value={seed.value}
                    onChange={(e) => handleSeedChange(index, 'value', e.target.value)}
                    fullWidth
                    size='small'
                  />

                  <IconButton
                    onClick={() => handleRemoveSeed(index)}
                    size='small'
                    color='error'
                    sx={{ mt: 1 }}
                  >
                    <DeleteIcon/>
                  </IconButton>
                </Stack>
              ))}
            </Stack>

            <Button
              startIcon={<AddIcon/>}
              onClick={handleAddSeed}
              variant='outlined'
              size='small'
              sx={{ mt: 1 }}
            >
              Add Seed
            </Button>
          </Box>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button onClick={handleSubmit} variant='contained' color='primary'>
          Create Job
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default StartJobDialog;
