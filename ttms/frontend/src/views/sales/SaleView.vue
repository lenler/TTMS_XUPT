<template>
  <section class="grid cols-2">
    <article class="card">
      <h2>选座售票</h2>
      <div class="toolbar">
        <input v-model.number="scheduleId" type="number" placeholder="演出计划ID" />
        <button class="btn secondary" @click="loadTickets">载入演出票</button>
      </div>
      <div class="seat-map">
        <button
          v-for="ticket in tickets"
          :key="ticket.id"
          class="seat"
          :class="{ sold: ticket.status !== 'AVAILABLE', selected: selected.includes(ticket.id) }"
          @click="toggle(ticket)"
          :title="`票ID ${ticket.id}`"
        />
      </div>
    </article>
    <article class="card">
      <h2>订单处理</h2>
      <p class="muted">已选票据：{{ selected.join(', ') || '无' }}</p>
      <div class="toolbar">
        <button class="btn" @click="placeOrder">下单锁票</button>
        <input v-model.number="paidAmount" type="number" placeholder="收款金额" />
        <button class="btn secondary" @click="pay">确认收款出票</button>
      </div>
      <p>当前订单：{{ saleId || '-' }}</p>
    </article>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { saleApi, ticketApi } from '../../api/ttms'

const scheduleId = ref(1)
const paidAmount = ref(0)
const tickets = ref([])
const selected = ref([])
const saleId = ref(null)

async function loadTickets() {
  const res = await ticketApi.list(scheduleId.value)
  tickets.value = res.data || []
}

function toggle(ticket) {
  if (ticket.status !== 'AVAILABLE') return
  selected.value = selected.value.includes(ticket.id)
    ? selected.value.filter((id) => id !== ticket.id)
    : [...selected.value, ticket.id]
}

async function placeOrder() {
  const res = await saleApi.placeOrder({ employeeId: 1, ticketIds: selected.value })
  saleId.value = res.data?.id
}

async function pay() {
  if (!saleId.value) return
  await saleApi.pay(saleId.value, { paidAmount: paidAmount.value })
}
</script>
