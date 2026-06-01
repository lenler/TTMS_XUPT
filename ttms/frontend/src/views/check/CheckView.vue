<template>
  <section class="card">
    <h2>票 ID 验证</h2>
    <div class="toolbar">
      <input v-model.number="ticketId" type="number" placeholder="输入票ID" />
      <button class="btn" @click="checkIn">验票放行</button>
    </div>
    <p :class="result.ok ? '' : 'muted'">{{ result.message }}</p>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ticketApi } from '../../api/ttms'

const ticketId = ref()
const result = reactive({ ok: false, message: '等待验票' })

async function checkIn() {
  try {
    await ticketApi.checkIn(ticketId.value)
    result.ok = true
    result.message = '验票通过'
  } catch {
    result.ok = false
    result.message = '票据无效、已退票、已验票或不属于当前可验状态'
  }
}
</script>
