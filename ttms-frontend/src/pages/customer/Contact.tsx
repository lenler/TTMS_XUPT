// 联系我们页 —— 水墨留白 · 东方极简
// 信息卡片 + 交通路线

import { Divider } from 'antd';
import { EnvironmentOutlined, PhoneOutlined, MailOutlined, ClockCircleOutlined } from '@ant-design/icons';

/** 联系我们页 */
function ContactPage() {
  return (
    <div>
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-8">联系我们</h1>

      {/* 剧院信息卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-6 mb-6">
        <h2 className="font-serif text-xl text-ink mb-6">汉唐剧院</h2>
        <div className="space-y-5">
          <div className="flex items-start gap-4">
            <EnvironmentOutlined className="text-gold text-lg mt-0.5" />
            <div>
              <p className="text-stone text-sm mb-0.5">地址</p>
              <p className="text-ink">陕西省西安市长安区西长安街618号</p>
            </div>
          </div>
          <div className="flex items-start gap-4">
            <PhoneOutlined className="text-gold text-lg mt-0.5" />
            <div>
              <p className="text-stone text-sm mb-0.5">票务热线</p>
              <p className="text-ink">029-8816 0000</p>
            </div>
          </div>
          <div className="flex items-start gap-4">
            <MailOutlined className="text-gold text-lg mt-0.5" />
            <div>
              <p className="text-stone text-sm mb-0.5">电子邮箱</p>
              <p className="text-ink">service@hantang-theatre.com</p>
            </div>
          </div>
          <div className="flex items-start gap-4">
            <ClockCircleOutlined className="text-gold text-lg mt-0.5" />
            <div>
              <p className="text-stone text-sm mb-0.5">营业时间</p>
              <p className="text-ink">周一至周日 09:00 - 22:00</p>
            </div>
          </div>
        </div>
      </div>

      {/* 交通路线卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-6">
        <h2 className="font-serif text-xl text-ink mb-4">交通路线</h2>
        <Divider className="!my-4 !border-warm" />
        <div className="space-y-4 text-sm">
          <div>
            <p className="text-ink font-medium mb-1">地铁</p>
            <p className="text-stone">乘坐地铁2号线至航天城站，B出口向西步行约800米</p>
          </div>
          <div>
            <p className="text-ink font-medium mb-1">公交</p>
            <p className="text-stone">乘坐215路、229路、600路至西长安街站下车</p>
          </div>
          <div>
            <p className="text-ink font-medium mb-1">自驾</p>
            <p className="text-stone">剧院配有机动车停车场，凭当日演出票可免费停车3小时</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ContactPage;
