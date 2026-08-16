import { useState } from 'react';
import type { SearchParams, SearchResultResponse } from '../api/types/search.ts';
import documentApi from '../api/documentApi.ts';

const useSearch = () => {
  const [result, setResult] = useState<SearchResultResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);

  const search = async (params: SearchParams) => {
    setLoading(true);
    setError(null);
    try {
      const response = await documentApi.search(params);
      setResult(response.data);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Search failed'));
    } finally {
      setLoading(false);
    }
  };

  return { result, loading, error, search };
};

export default useSearch;
