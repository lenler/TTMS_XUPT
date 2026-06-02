// 观众注册页 —— 水墨留白 · 东方极简
// 居中卡片 + 衬线标题 + 线性表单

import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Input, Button, Select, message } from 'antd';
import { customerRegister } from '@/services/customer/auth';

/** 观众端注册页 */
function RegisterPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: {
    name: string; gender: number; phone: string; email: string;
    username: string; password: string; paymentPassword: string;
  }) => {
    setLoading(true);
    try {
      await customerRegister(values);
      message.success('注册成功，请登录');
      navigate('/login');
    } catch {
      // 错误已在拦截器中提示
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto pt-10 pb-20">
      <div className="border border-warm bg-cream rounded-sm p-8">
        {/* 品牌标题 */}
        <h1 className="font-serif text-2xl text-ink tracking-widest text-center mb-8">
          汉唐剧院
        </h1>

        <Form onFinish={onFinish} size="large" labelCol={{ span: 6 }} wrapperCol={{ span: 18 }}>
          <Form.Item label="姓名" name="name" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item label="性别" name="gender" rules={[{ required: true, message: '请选择性别' }]}
            initialValue={1}>
            <Select options={[{ label: '男', value: 1 }, { label: '女', value: 0 }]} />
          </Form.Item>
          <Form.Item label="手机号" name="phone" rules={[{ required: true, message: '请输入手机号' }]}>
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item label="邮箱" name="email" rules={[{ required: true, message: '请输入邮箱' }, { type: 'email' }]}>
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item label="密码" name="password" rules={[{ required: true, min: 6, message: '密码至少6位' }]}>
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Form.Item label="支付密码" name="paymentPassword" rules={[{ required: true, min: 6, message: '支付密码至少6位' }]}>
            <Input.Password placeholder="请输入支付密码" />
          </Form.Item>
          <Form.Item wrapperCol={{ offset: 6, span: 18 }} className="!mb-4">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              className="!bg-ink !border-ink hover:!bg-gold hover:!border-gold !rounded-sm !h-10"
            >
              注册
            </Button>
          </Form.Item>
          <div className="text-center text-sm">
            <span className="text-stone">已有账号？</span>
            <Link to="/login" className="text-gold hover:text-[#B8944F] ml-1 transition-soft">
              去登录
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}

export default RegisterPage;
