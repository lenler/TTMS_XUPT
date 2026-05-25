// 通用 CRUD Hook：列表加载、搜索、分页、刷新

import { useState, useCallback, useEffect } from 'react';
import { usePagination } from './usePagination';
import type { PageParams } from '@/types/api';

/**
 * 列表请求函数类型
 * 注：Axios 响应拦截器已解包，运行时返回 ApiResponse<PageData<T>>，
 *     但 TS 类型系统无法感知拦截器转换，这里用宽松类型接受。
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type FetchFn = (params: PageParams) => any;

export function useCRUD<T>(fetchFn: FetchFn) {
  const [list, setList] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const { page, pageSize, total, setPage, setPageSize, setTotal, resetPage } =
    usePagination();

  /** 请求数据 */
  const refresh = useCallback(
    async (params?: { keyword?: string; page?: number }) => {
      setLoading(true);
      try {
        const kw = params?.keyword ?? keyword;
        const pg = params?.page ?? page;
        const res = await fetchFn({ keyword: kw, page: pg, pageSize });
        setList(res.data.list);
        setTotal(res.data.total);
        setPage(pg);
      } catch {
        setList([]);
        setTotal(0);
      } finally {
        setLoading(false);
      }
    },
    [fetchFn, keyword, page, pageSize, setPage, setTotal]
  );

  /** 首次加载 + keyword 变化时自动请求 */
  useEffect(() => {
    refresh({ keyword, page: 1 });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyword]);

  /** 搜索：重置到第 1 页 */
  const search = useCallback(
    (kw: string) => {
      setKeyword(kw);
      resetPage();
    },
    [resetPage]
  );

  return {
    list,
    loading,
    pagination: { page, pageSize, total },
    keyword,
    setKeyword: search,
    setPage,
    setPageSize,
    refresh,
  };
}
