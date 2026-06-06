// 通用表单校验规则（配合 Ant Design Form 的 rules 使用）

import type { Rule } from 'antd/es/form';

/** 必填校验 */
export function requiredRule(label: string): Rule {
  return { required: true, message: `请输入${label}` };
}

/** 手机号校验 */
export const phoneRule: Rule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入正确的手机号',
};

/** 邮箱校验 */
export const emailRule: Rule = {
  type: 'email',
  message: '请输入正确的邮箱地址',
};

/** 密码校验（至少6位） */
export const passwordRule: Rule = {
  min: 6,
  message: '密码至少6位',
};

/** 金额校验（正数，最多两位小数） */
export const moneyRule: Rule = {
  pattern: /^\d+(\.\d{1,2})?$/,
  message: '请输入合法的金额（正数，最多两位小数）',
};

/** 整数校验 */
export const integerRule: Rule = {
  pattern: /^\d+$/,
  message: '请输入正整数',
};
