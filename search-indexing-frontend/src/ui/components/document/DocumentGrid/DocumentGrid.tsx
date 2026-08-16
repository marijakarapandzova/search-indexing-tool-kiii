import { Grid } from '@mui/material';
import type { DocumentResponse } from '../../../../api/types/document.ts';
import DocumentCard from '../DocumentCard/DocumentCard.tsx';

interface DocumentGridProps {
  documents: DocumentResponse[];
  onDelete?: (id: number) => void;
}

const DocumentGrid = ({ documents, onDelete }: DocumentGridProps) => {
  return (
    <Grid container spacing={2}>
      {documents.map((document) => (
        <Grid key={document.id} size={{ xs: 12, sm: 6, md: 4 }}>
          <DocumentCard document={document} onDelete={onDelete}/>
        </Grid>
      ))}
    </Grid>
  );
};

export default DocumentGrid;
