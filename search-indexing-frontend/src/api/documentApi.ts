import axiosInstance from '../axios/axios.ts';
import type { DocumentFilter, DocumentResponse, PageResponse } from './types/document.ts';
import type { SearchParams, SearchResultResponse } from './types/search.ts';

const documentApi = {
  findAll: async (filter: DocumentFilter, page: number, size: number) => {
    return await axiosInstance.get<PageResponse<DocumentResponse>>('/documents', {
      params: { ...filter, page, size }
    });
  },
  findById: async (id: string) => {
    return await axiosInstance.get<DocumentResponse>(`/documents/${id}`);
  },
  delete: async (id: string) => {
    return await axiosInstance.delete<DocumentResponse>(`/documents/${id}/delete`);
  },
  search: async (params: SearchParams) => {
    return await axiosInstance.get<SearchResultResponse>('/documents/search', { params });
  }
};

export default documentApi;
