// 通用 CRUD Hook

import { useCallback, useEffect, useRef, useState } from 'react';
import type { ApiResponse, PageData, PageParams } from '@/types/api';

/** 通用 CRUD Hook 配置 */
interface UseCRUDOptions<T> {
  fetchList: (params: PageParams) => Promise<ApiResponse<PageData<T>>>;
  initialPageSize?: number;
}

/** 提供分页、搜索、刷新和加载状态管理 */
export function useCRUD<T>({ fetchList, initialPageSize = 10 }: UseCRUDOptions<T>) {
  const [list, setList] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [total, setTotal] = useState(0);
  const [keyword, setKeyword] = useState('');
  const paramsRef = useRef<PageParams>({ page: 1, pageSize: initialPageSize, keyword: '' });

  /** 按当前查询条件拉取分页数据 */
  const loadData = useCallback(
    async (nextParams?: Partial<PageParams>) => {
      const params = {
        ...paramsRef.current,
        ...nextParams,
      };
      paramsRef.current = params;

      setLoading(true);
      try {
        const res = await fetchList(params);
        setList(res.data.list);
        setTotal(res.data.total);
        setPage(res.data.page);
        setPageSize(res.data.pageSize);
      } finally {
        setLoading(false);
      }
    },
    [fetchList]
  );

  /** 根据关键字重新查询第一页数据 */
  const search = useCallback(
    (nextKeyword: string) => {
      setKeyword(nextKeyword);
      void loadData({ page: 1, keyword: nextKeyword });
    },
    [loadData]
  );

  /** 切换分页后重新加载数据 */
  const changePage = useCallback(
    (nextPage: number, nextPageSize: number) => {
      void loadData({ page: nextPage, pageSize: nextPageSize });
    },
    [loadData]
  );

  useEffect(() => {
    const timer = window.setTimeout(() => {
      void loadData();
    }, 0);

    return () => {
      window.clearTimeout(timer);
    };
  }, [loadData]);

  return {
    list,
    loading,
    page,
    pageSize,
    total,
    keyword,
    loadData,
    search,
    changePage,
  };
}
