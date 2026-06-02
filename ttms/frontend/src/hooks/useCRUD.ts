// 通用 CRUD Hook：列表加载、搜索、分页、刷新

import { useState, useCallback, useEffect } from 'react';
import { usePagination } from './usePagination';
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type FetchFn = (params: any) => any;

export function useCRUD<T>(fetchFn: FetchFn) {
  const [list, setList] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [extraParams, setExtraParams] = useState<Record<string, string>>({});
  const { page, pageSize, total, setPage, setPageSize, setTotal, resetPage } =
    usePagination();

  /** 请求数据 */
  const refresh = useCallback(
    async (params?: { keyword?: string; page?: number }) => {
      setLoading(true);
      try {
        const kw = params?.keyword ?? keyword;
        const pg = params?.page ?? page;
        const res = await fetchFn({
          keyword: kw,
          page: pg,
          pageSize,
          ...extraParams,
        });
        const newList = res.data.list;
        const newTotal = res.data.total;
        // 如果当前页已超出总页数（如删除最后一页的最后一条），回退到最后一页
        const lastPage = Math.max(1, Math.ceil(newTotal / pageSize));
        const correctedPage = pg > lastPage ? lastPage : pg;
        if (correctedPage !== pg) {
          // 用正确的页码重新请求
          const retryRes = await fetchFn({
            keyword: kw,
            page: correctedPage,
            pageSize,
            ...extraParams,
          });
          setList(retryRes.data.list);
          setTotal(retryRes.data.total);
          setPage(correctedPage);
        } else {
          setList(newList);
          setTotal(newTotal);
          setPage(pg);
        }
      } catch {
        setList([]);
        setTotal(0);
      } finally {
        setLoading(false);
      }
    },
    [fetchFn, keyword, page, pageSize, extraParams, setPage, setTotal]
  );

  /** keyword 或 extraParams 变化时重新请求第 1 页 */
  useEffect(() => {
    refresh({ keyword, page: 1 });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyword, extraParams]);

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
    extraParams,
    setExtraParams,
  };
}
