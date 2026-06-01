// MSW Handlers 汇总导出

import { authHandlers } from './auth';
import { studioHandlers } from './studio';
import { playHandlers } from './play';
import { scheduleHandlers } from './schedule';
import { saleHandlers } from './sale';
import { checkHandlers } from './check';
import { employeeHandlers } from './employee';
import { customerHandlers } from './customer';
import { roleHandlers } from './role';
import { financeHandlers } from './finance';
import { orderHandlers } from './order';

export const handlers = [
  ...authHandlers,
  ...studioHandlers,
  ...playHandlers,
  ...scheduleHandlers,
  ...saleHandlers,
  ...checkHandlers,
  ...employeeHandlers,
  ...customerHandlers,
  ...roleHandlers,
  ...financeHandlers,
  ...orderHandlers,
];
