// 放映安排列表页 —— 水墨留白 · 东方极简
// 搜索 + 日期筛选 + 横向卡片布局

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Input, Typography, Spin, Empty, Tag } from 'antd';
import { ClockCircleOutlined, EnvironmentOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { getSchedules } from '@/services/customer/schedule';

const { Title, Text } = Typography;

interface ScheduleItem {
  id: number; playId: number; playName: string; playPoster: string;
  playType: string; playDuration: number;
  studioId: number; studioName: string;
  showTime: string; ticketPrice: number; availableSeats: number;
}

/** 日期快速选择按钮 */
const datePresets = [
  { label: '全部', value: '' },
  { label: '今天', value: dayjs().format('YYYY-MM-DD') },
  { label: '明天', value: dayjs().add(1, 'day').format('YYYY-MM-DD') },
  { label: '后天', value: dayjs().add(2, 'day').format('YYYY-MM-DD') },
  { label: '本周', value: 'week' },
];

/** 放映安排列表页 */
function SchedulePage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ScheduleItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [date, setDate] = useState('');
  const [keyword, setKeyword] = useState('');
  const navigate = useNavigate();
  const pageSize = 9;

  const fetchData = () => {
    setLoading(true);
    const params: { page: number; pageSize: number; keyword?: string; date?: string } = { page, pageSize };
    if (keyword.trim()) params.keyword = keyword.trim();
    if (date) params.date = date;
    getSchedules(params)
      .then((res) => { setData(res.data.list); setTotal(res.data.total); })
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, [page, date, keyword]);

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      {/* 页标题 */}
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-6">放映安排</h1>

      {/* 筛选栏 —— 搜索 + 日期 */}
      <div className="flex flex-wrap items-center gap-4 mb-8">
        {/* 搜索框 */}
        <Input
          placeholder="搜索剧目名称"
          prefix={<SearchOutlined className="text-light-ink" />}
          value={keyword}
          onChange={(e) => { setKeyword(e.target.value); setPage(1); }}
          allowClear
          className="max-w-xs"
        />

        {/* 日期快捷按钮 */}
        <div className="flex items-center border border-warm rounded-sm overflow-hidden">
          {datePresets.map((p) => (
            <button
              key={p.value}
              onClick={() => { setDate(p.value); setPage(1); }}
              className={`px-4 py-1.5 text-sm transition-soft cursor-pointer border-r border-warm last:border-r-0
                ${date === p.value ? 'bg-ink text-white' : 'text-stone hover:bg-cream'}`}
            >
              {p.label}
            </button>
          ))}
        </div>
      </div>

      {/* 列表 */}
      <Spin spinning={loading}>
        {data.length === 0 && !loading && <Empty description="暂无放映安排" />}
        <div className="space-y-3">
          {data.map((item) => (
            <div
              key={item.id}
              onClick={() => navigate(`/seats/${item.id}`)}
              className="border border-warm bg-cream rounded-sm p-4 flex gap-5 cursor-pointer
                         hover:border-stone hover:shadow-sm transition-soft group"
            >
              {/* 左侧海报占位 */}
              <div className="hidden sm:flex w-28 h-28 bg-[#EAE4DA] rounded-sm items-center justify-center shrink-0
                              group-hover:opacity-80 transition-soft">
                <Text className="text-4xl text-light-ink">🎬</Text>
              </div>

              {/* 右侧信息 */}
              <div className="flex-1 flex flex-col justify-between min-w-0">
                <div>
                  <Title level={5} className="!mb-1 !font-serif !text-lg">{item.playName}</Title>
                  <div className="flex items-center gap-2 mb-2">
                    <Tag color="gold">{item.playType}</Tag>
                    <Tag>{item.playDuration}分钟</Tag>
                  </div>
                </div>
                <div className="flex flex-wrap items-center gap-x-5 gap-y-1 text-sm text-stone">
                  <span>
                    <EnvironmentOutlined className="mr-1 text-light-ink" />{item.studioName}
                  </span>
                  <span>
                    <ClockCircleOutlined className="mr-1 text-light-ink" />{item.showTime}
                  </span>
                </div>
              </div>

              {/* 右侧价格 + 按钮 */}
              <div className="flex flex-col items-end justify-center shrink-0">
                <div className="text-2xl text-ink font-medium">¥{item.ticketPrice.toFixed(2)}</div>
                <div className={`text-sm mb-2 ${item.availableSeats > 0 ? 'text-stone' : 'text-red-500'}`}>
                  剩余 {item.availableSeats} 座
                </div>
                <span className="inline-block border border-ink text-ink px-4 py-1 rounded-sm text-sm
                                 group-hover:bg-ink group-hover:text-white transition-soft">
                  选座购票
                </span>
              </div>
            </div>
          ))}
        </div>
      </Spin>

      {/* 分页 */}
      {total > pageSize && (
        <div className="flex items-center justify-center gap-6 mt-10 text-sm">
          {page > 1 ? (
            <span
              onClick={() => setPage(page - 1)}
              className="text-ink hover:text-gold cursor-pointer transition-soft"
            >
              ← 上一页
            </span>
          ) : (
            <span className="text-light-ink cursor-not-allowed">← 上一页</span>
          )}
          <span className="text-stone">第 {page} / {totalPages} 页</span>
          {page < totalPages ? (
            <span
              onClick={() => setPage(page + 1)}
              className="text-ink hover:text-gold cursor-pointer transition-soft"
            >
              下一页 →
            </span>
          ) : (
            <span className="text-light-ink cursor-not-allowed">下一页 →</span>
          )}
        </div>
      )}
    </div>
  );
}

export default SchedulePage;
