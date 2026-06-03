// 观众钱包页：余额展示 + 充值

import { useEffect, useState } from 'react';
import { Button, InputNumber, Spin, message } from 'antd';
import { WalletOutlined } from '@ant-design/icons';
import { getProfile, rechargeWallet, type CustomerProfile } from '@/services/customer/auth';

function WalletPage() {
  const [loading, setLoading] = useState(false);
  const [recharging, setRecharging] = useState(false);
  const [profile, setProfile] = useState<CustomerProfile | null>(null);
  const [amount, setAmount] = useState<number | null>(100);

  const loadProfile = async () => {
    setLoading(true);
    try {
      const res = await getProfile();
      setProfile(res.data);
      const stored = localStorage.getItem('customerInfo');
      if (stored) {
        localStorage.setItem('customerInfo', JSON.stringify({ ...JSON.parse(stored), balance: res.data.balance }));
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const handleRecharge = async () => {
    if (!amount || amount <= 0) {
      message.warning('请输入有效的充值金额');
      return;
    }
    setRecharging(true);
    try {
      const res = await rechargeWallet(amount);
      setProfile((prev) => prev ? {
        ...prev,
        balance: res.data.balance,
        rechargeTotal: res.data.rechargeTotal,
        rechargeCount: res.data.rechargeCount,
      } : prev);
      const stored = localStorage.getItem('customerInfo');
      if (stored) {
        localStorage.setItem('customerInfo', JSON.stringify({ ...JSON.parse(stored), balance: res.data.balance }));
      }
      message.success(`充值成功，当前余额 ¥${res.data.balance.toFixed(2)}`);
    } finally {
      setRecharging(false);
    }
  };

  if (loading) return <div className="text-center py-20"><Spin size="large" /></div>;

  return (
    <div className="max-w-xl mx-auto">
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-8 text-center">账户余额</h1>

      <div className="border border-warm bg-cream rounded-sm p-8 mb-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-12 h-12 rounded-sm bg-ink text-white flex items-center justify-center text-2xl">
            <WalletOutlined />
          </div>
          <div>
            <p className="text-stone text-sm">当前可用余额</p>
            <p className="text-4xl font-serif text-ink">¥{(profile?.balance ?? 0).toFixed(2)}</p>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4 text-sm border-t border-warm pt-4">
          <div>
            <p className="text-stone">累计充值</p>
            <p className="text-ink font-medium">¥{(profile?.rechargeTotal ?? 0).toFixed(2)}</p>
          </div>
          <div>
            <p className="text-stone">充值次数</p>
            <p className="text-ink font-medium">{profile?.rechargeCount ?? 0} 次</p>
          </div>
        </div>
      </div>

      <div className="border border-warm bg-cream rounded-sm p-6">
        <h3 className="font-serif text-lg text-ink mb-4">充值</h3>
        <div className="flex gap-3">
          <InputNumber
            min={1}
            precision={2}
            value={amount}
            onChange={(value) => setAmount(value)}
            prefix="¥"
            className="flex-1"
          />
          <Button type="primary" loading={recharging} onClick={handleRecharge}>
            立即充值
          </Button>
        </div>
      </div>
    </div>
  );
}

export default WalletPage;
