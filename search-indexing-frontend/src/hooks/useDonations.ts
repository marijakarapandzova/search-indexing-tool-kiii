import { useState, useEffect, useCallback } from 'react';
import type { CreateDonationBatchRequest, DonationBatchResponse } from '../api/types/donation.ts';
import donationApi from '../api/donationApi.ts';

const useDonations = () => {
  const [donations, setDonations] = useState<DonationBatchResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchDonations = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await donationApi.findAll();
      setDonations(response.data);
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch donations'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDonations();
  }, [fetchDonations]);

  const onCreate = useCallback(async (data: CreateDonationBatchRequest) => {
    try {
      await donationApi.add(data);
      await fetchDonations();
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to create donation batch'));
    }
  }, [fetchDonations]);

  const onApprove = useCallback(async (id: number) => {
    try {
      await donationApi.approve(id.toString());
      await fetchDonations();
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to approve donation batch'));
    }
  }, [fetchDonations]);

  const onSubmit = useCallback(async (id: number) => {
    try {
      await donationApi.submit(id.toString());
      await fetchDonations();
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to submit donation batch'));
    }
  }, [fetchDonations]);

  return { donations, loading, error, onCreate, onApprove, onSubmit };
};

export default useDonations;
