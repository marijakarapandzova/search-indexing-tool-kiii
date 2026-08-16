import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  Checkbox,
  FormControlLabel,
  Stack,
  CircularProgress,
  Typography,
  Box,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText
} from '@mui/material';
import type { DocumentResponse } from '../../../../api/types/document.ts';
import documentApi from '../../../../api/documentApi.ts';
import useDonations from '../../../../hooks/useDonations.ts';
import useSnackbar from '../../../../hooks/useSnackbar.ts';

interface SubmitDonationDialogProps {
  open: boolean;
  onClose: () => void;
}

const SubmitDonationDialog = ({ open, onClose }: SubmitDonationDialogProps) => {
  const { onCreate } = useDonations();
  const { showSnackbar } = useSnackbar();

  const [documents, setDocuments] = useState<DocumentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (open) {
      fetchDocuments();
    }
  }, [open]);

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      const response = await documentApi.findAll({ donated: false }, 0, 100);
      setDocuments(response.data.content);
      setSelectedIds(new Set());
    } catch (err) {
      showSnackbar('Failed to fetch documents', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleDocument = (id: number) => {
    const newSelected = new Set(selectedIds);
    if (newSelected.has(id)) {
      newSelected.delete(id);
    } else {
      newSelected.add(id);
    }
    setSelectedIds(newSelected);
  };

  const handleSelectAll = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      setSelectedIds(new Set(documents.map((d) => d.id)));
    } else {
      setSelectedIds(new Set());
    }
  };

  const handleSubmit = async () => {
    if (selectedIds.size === 0) {
      showSnackbar('Please select at least one document', 'error');
      return;
    }

    try {
      await onCreate({ documentIds: Array.from(selectedIds) });
      handleClose();
      showSnackbar('Donation batch created successfully', 'success');
    } catch {
      showSnackbar('Failed to create donation batch', 'error');
    }
  };

  const handleClose = () => {
    setSelectedIds(new Set());
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth='md'>
      <DialogTitle>New Donation Batch</DialogTitle>
      <DialogContent sx={{ pt: 2 }}>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress/>
          </Box>
        ) : documents.length === 0 ? (
          <Typography color='text.secondary'>
            All documents have been donated. No documents available for donation.
          </Typography>
        ) : (
          <Stack spacing={2}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={selectedIds.size === documents.length && documents.length > 0}
                  indeterminate={selectedIds.size > 0 && selectedIds.size < documents.length}
                  onChange={handleSelectAll}
                />
              }
              label={`Select All (${selectedIds.size} / ${documents.length} selected)`}
            />

            <List sx={{ maxHeight: 400, overflow: 'auto', border: '1px solid', borderColor: 'divider' }}>
              {documents.map((doc) => (
                <ListItem key={doc.id} disablePadding>
                  <ListItemButton
                    dense
                    onClick={() => handleToggleDocument(doc.id)}
                  >
                    <ListItemIcon>
                      <Checkbox
                        checked={selectedIds.has(doc.id)}
                        tabIndex={-1}
                        disableRipple
                      />
                    </ListItemIcon>
                    <ListItemText
                      primary={doc.title ?? doc.url}
                      secondary={`${doc.resourceType}${doc.macedonianConfidence ? ` • MK: ${(doc.macedonianConfidence * 100).toFixed(0)}%` : ''}`}
                    />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Stack>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button
          onClick={handleSubmit}
          variant='contained'
          color='primary'
          disabled={selectedIds.size === 0 || loading}
        >
          Create Batch ({selectedIds.size})
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default SubmitDonationDialog;
