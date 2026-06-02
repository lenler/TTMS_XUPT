// 观众端首页 —— 水墨留白 · 东方极简
// Hero 大标题 + 热卖剧目 + 近期演出

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography, Spin, Empty, Tag, Button } from 'antd';
import { FireOutlined, ClockCircleOutlined, EnvironmentOutlined } from '@ant-design/icons';
import { getHome } from '@/services/customer/home';

const { Title, Text } = Typography;

interface HotPlay {
  id: number; name: string; poster: string; typeName: string;
  duration: number; basePrice: number; soldCount: number;
}

interface UpcomingSchedule {
  id: number; playId: number; playName: string; studioName: string;
  showTime: string; ticketPrice: number; availableSeats: number;
}

/** 观众端首页 */
function HomePage() {
  const [loading, setLoading] = useState(false);
  const [hotPlays, setHotPlays] = useState<HotPlay[]>([]);
  const [upcoming, setUpcoming] = useState<UpcomingSchedule[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    getHome()
      .then((res) => {
        setHotPlays(res.data.hotPlays);
        setUpcoming(res.data.upcomingSchedules);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  return (
    <Spin spinning={loading}>
      {/* ===== Hero 区域 ===== */}
      <section className="text-center py-16 mb-12">
        <h1 className="font-serif text-4xl md:text-5xl text-ink tracking-wider mb-4">
          汉唐剧院
        </h1>
        <p className="text-stone text-lg mb-8 max-w-md mx-auto leading-relaxed">
          传承经典，演绎非凡。感受舞台艺术的永恒魅力。
        </p>
        <button
          onClick={() => navigate('/schedule')}
          className="inline-block bg-gold text-white px-10 py-3 rounded-sm font-medium
                     hover:bg-[#B8944F] transition-soft cursor-pointer"
        >
          立即购票
        </button>
      </section>

      {/* ===== 热卖剧目 ===== */}
      <section className="mb-14">
        <h2 className="font-serif text-2xl text-ink tracking-wide mb-6 flex items-center gap-2">
          <FireOutlined className="text-gold" />
          热卖剧目
        </h2>
        {hotPlays.length === 0 && !loading && <Empty description="暂无剧目" />}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {hotPlays.map((play) => (
            <div
              key={play.id}
              onClick={() => navigate(`/schedule?playId=${play.id}`)}
              className="border border-warm bg-cream rounded-sm overflow-hidden cursor-pointer
                         hover:border-stone hover:shadow-sm transition-soft group"
            >
              {/* 封面占位 */}
              <div className="h-48 bg-cream flex items-center justify-center group-hover:opacity-80 transition-soft">
                <Text className="text-6xl text-light-ink">🎭</Text>
              </div>
              <div className="p-4">
                <h3 className="font-serif text-lg text-ink mb-2 truncate">{play.name}</h3>
                <div className="flex items-center gap-2 mb-3">
                  <Tag color="gold">{play.typeName}</Tag>
                  <Tag>{play.duration}分钟</Tag>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-stone">基础票价</span>
                  <span className="text-ink font-medium">¥{play.basePrice.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm mt-1">
                  <span className="text-stone">已售</span>
                  <span className="text-gold font-medium">{play.soldCount} 张</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ===== 近期演出 ===== */}
      <section>
        <h2 className="font-serif text-2xl text-ink tracking-wide mb-6 flex items-center gap-2">
          <ClockCircleOutlined className="text-gold" />
          近期演出
        </h2>
        {upcoming.length === 0 && !loading && <Empty description="暂无近期演出" />}
        <div className="space-y-3">
          {upcoming.map((item) => (
            <div
              key={item.id}
              onClick={() => navigate(`/seats/${item.id}`)}
              className="border border-warm bg-cream rounded-sm p-5 flex flex-col sm:flex-row
                         sm:items-center sm:justify-between cursor-pointer
                         hover:border-stone hover:shadow-sm transition-soft group"
            >
              <div className="flex-1 mb-3 sm:mb-0">
                <Title level={5} className="!mb-1 !font-serif">{item.playName}</Title>
                <div className="flex flex-wrap items-center gap-x-6 gap-y-1 text-sm text-stone">
                  <span>
                    <EnvironmentOutlined className="mr-1 text-light-ink" />
                    {item.studioName}
                  </span>
                  <span>
                    <ClockCircleOutlined className="mr-1 text-light-ink" />
                    {item.showTime}
                  </span>
                </div>
              </div>
              <div className="flex items-center gap-6">
                <div className="text-right">
                  <div className="text-2xl text-ink font-medium">
                    ¥{item.ticketPrice.toFixed(2)}
                  </div>
                  <div className={`text-sm ${item.availableSeats > 0 ? 'text-stone' : 'text-red-500'}`}>
                    剩余 {item.availableSeats} 座
                  </div>
                </div>
                <Button
                  className="!border-ink !text-ink hover:!bg-ink hover:!text-white !rounded-sm
                             transition-soft !font-medium"
                >
                  购票
                </Button>
              </div>
            </div>
          ))}
        </div>
      </section>
    </Spin>
  );
}

export default HomePage;
