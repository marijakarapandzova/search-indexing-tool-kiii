import { useState, useEffect, useCallback } from 'react';
import type { DocumentFilter, DocumentResponse, PageResponse } from '../api/types/document.ts';
import documentApi from '../api/documentApi.ts';

const useDocuments = (filter: DocumentFilter, page: number, size: number) => {
  const [documents, setDocuments] = useState<PageResponse<DocumentResponse> | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchDocuments = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await documentApi.findAll(filter, page, size);
      setDocuments(response.data);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch documents'));
    } finally {
      setLoading(false);
    }
  }, [filter, page, size]);

  useEffect(() => {
    fetchDocuments();
  }, [fetchDocuments]);

  const onDelete = useCallback(async (id: number) => {
    try {
      await documentApi.delete(id.toString());
      await fetchDocuments();
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to delete document'));
    }
  }, [fetchDocuments]);

  return { documents, loading, error, onDelete };
};

export default useDocuments;
