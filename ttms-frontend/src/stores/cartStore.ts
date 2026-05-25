// 购物车状态管理：选座购票临时状态

import { create } from 'zustand';

interface Seat {
  id: number;
  row: number;
  col: number;
}

interface LockInfo {
  lockToken: string;
  expireAt: string;
}

interface CartState {
  scheduleId: number | null;
  selectedSeats: Seat[];
  lockToken: string | null;
  expireAt: string | null;
  totalPrice: number;
  addSeat: (seat: Seat) => void;
  removeSeat: (seatId: number) => void;
  clearCart: () => void;
  setLockInfo: (info: LockInfo) => void;
}

export const useCartStore = create<CartState>((set) => ({
  scheduleId: null,
  selectedSeats: [],
  lockToken: null,
  expireAt: null,
  totalPrice: 0,

  addSeat: (seat) =>
    set((state) => ({
      selectedSeats: [...state.selectedSeats, seat],
    })),

  removeSeat: (seatId) =>
    set((state) => ({
      selectedSeats: state.selectedSeats.filter((s) => s.id !== seatId),
    })),

  clearCart: () =>
    set({
      scheduleId: null,
      selectedSeats: [],
      lockToken: null,
      expireAt: null,
      totalPrice: 0,
    }),

  setLockInfo: (info) =>
    set({
      lockToken: info.lockToken,
      expireAt: info.expireAt,
    }),
}));
