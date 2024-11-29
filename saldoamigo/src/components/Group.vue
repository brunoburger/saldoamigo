<template>
    <div class="min-h-screen bg-gradient-to-r from-green-700 to-teal-700 flex flex-col items-center py-8">
      <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-3/4 lg:w-1/2">
        <h1 class="text-3xl font-bold text-gray-900 mb-6 text-center">Detalhes do Grupo</h1>
  
        <div class="flex justify-between mb-4">
          <span class="text-lg font-semibold">Valor Total: R$ {{ totalAmount }}</span>
          <div class="flex space-x-2">
            <input v-model="newAmount" type="number" class="border p-2 rounded-lg" placeholder="Adicionar valor">
            <button @click="addTransaction" class="bg-green-600 text-white py-2 px-4 rounded-lg">Adicionar</button>
          </div>
        </div>
  
        <ul v-if="transactions.length" class="space-y-4">
          <li v-for="transaction in transactions" :key="transaction.id" class="bg-gray-100 p-4 rounded-lg shadow flex justify-between items-center">
            <span>{{ transaction.date.split('-').reverse().join(' / ') }} - R$ {{ transaction.value }}</span>
            <button @click="deleteTransaction(transaction.id)" class="text-red-600 hover:text-red-800">Deletar</button>
          </li>
        </ul>
        <p v-else class="text-center text-gray-600 mt-4">Nenhuma transação encontrada.</p>
  
        <div v-if="qrCodeData" class="mt-4">
          <p class="text-center text-xl">QR Code de Pix</p>
          <div class="flex justify-center">
            <img :src="qrCodeData" alt="QR Code Pix" class="w-64 h-64">
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import api from '../api';
  import QRCode from 'qrcode';
  
  export default {
    name: 'TransactionList',
    data() {
      return {
        transactions: [],
        totalAmount: 0,
        newAmount: '',
        userId: localStorage.getItem('userId'),
        groupId: this.$route.params.id,
        qrCodeData: null,
      };
    },
    async created() {
      await this.fetchTransactions();
      await this.fetchAccountData();
    },
    methods: {
      async fetchTransactions() {
        try {
          const response = await api.get(`/transactions/find/group/${this.groupId}`);
          this.transactions = response.data?._embedded?.transactionDtoList || [];
          this.calculateTotalAmount();
        } catch (error) {
          console.error('Erro ao carregar transações', error);
        }
      },
  
      async addTransaction() {
        if (!this.newAmount) return;
  
        const transaction = {
          value: parseFloat(this.newAmount),
          date: new Date(),
          account: { id: this.userId },
          group: { id: this.groupId },
        };
  
        try {
          await api.post('/transactions', transaction);
          this.fetchTransactions();
          this.newAmount = '';
          this.generatePixQRCode(transaction.value);
        } catch (error) {
          console.error('Erro ao adicionar transação', error);
        }
      },
  
      async generatePixQRCode(newAmount) {
        const ID_PAYLOAD_FORMAT_INDICATOR = "00";
        const ID_MERCHANT_ACCOUNT_INFORMATION = "26";
        const ID_MERCHANT_ACCOUNT_INFORMATION_GUI = "00";
        const ID_MERCHANT_ACCOUNT_INFORMATION_KEY = "01";
        const ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION = "02";
        const ID_MERCHANT_CATEGORY_CODE = "52";
        const ID_TRANSACTION_CURRENCY = "53";
        const ID_TRANSACTION_AMOUNT = "54";
        const ID_COUNTRY_CODE = "58";
        const ID_MERCHANT_NAME = "59";
        const ID_MERCHANT_CITY = "60";
        const ID_ADDITIONAL_DATA_FIELD_TEMPLATE = "62";
        const ID_ADDITIONAL_DATA_FIELD_TEMPLATE_TXID = "05";
        const ID_CRC16 = "63";
  
        const getValue = (id, value) => {
            const size = String(value.length).padStart(2, "0");
            return id + size + value;
        };
  
        const getMerchantAccountInfo = () => {
            const gui = getValue(ID_MERCHANT_ACCOUNT_INFORMATION_GUI, "br.gov.bcb.pix");
            const key = getValue(ID_MERCHANT_ACCOUNT_INFORMATION_KEY, this.account.pixKey);
            const descriptionValue = getValue(ID_MERCHANT_ACCOUNT_INFORMATION_DESCRIPTION, `${this.groupId}`);
            return getValue(ID_MERCHANT_ACCOUNT_INFORMATION, gui + key + descriptionValue);
        };
  
        const getAdditionalDataFieldTemplate = () => {
            const txid = getValue(ID_ADDITIONAL_DATA_FIELD_TEMPLATE_TXID, "SALDOAMIGO");
            return getValue(ID_ADDITIONAL_DATA_FIELD_TEMPLATE, txid);
        };
  
        const getCRC16 = (payload) => {
            function ord(str) {
            return str.charCodeAt(0);
            }
            function dechex(number) {
            return parseInt(number, 10).toString(16).padStart(4, '0');
            }
  
            payload = payload + ID_CRC16 + "04";
            let polinomio = 0x1021;
            let resultado = 0xffff;
  
            for (let i = 0; i < payload.length; i++) {
            resultado ^= ord(payload[i]) << 8;
            for (let j = 0; j < 8; j++) {
                if ((resultado <<= 1) & 0x10000) resultado ^= polinomio;
                resultado &= 0xffff;
            }
            }
  
            return ID_CRC16 + "04" + dechex(resultado).toUpperCase();
        };
  
        const getPayload = () => {
            const amountFormatted = parseFloat(newAmount).toFixed(2);
            const payload =
            getValue(ID_PAYLOAD_FORMAT_INDICATOR, "01") +
            getMerchantAccountInfo() +
            getValue(ID_MERCHANT_CATEGORY_CODE, "0000") +
            getValue(ID_TRANSACTION_CURRENCY, "986") +
            getValue(ID_TRANSACTION_AMOUNT, amountFormatted) +
            getValue(ID_COUNTRY_CODE, "BR") +
            getValue(ID_MERCHANT_NAME, this.account.name) +
            getValue(ID_MERCHANT_CITY, this.account.city) +
            getAdditionalDataFieldTemplate();
  
            return payload + getCRC16(payload);
        };
  
        try {
            const pixPayload = getPayload();
            const qrCode = await QRCode.toDataURL(pixPayload);
            this.qrCodeData = qrCode;
        } catch (error) {
            console.error('Erro ao gerar QR Code de Pix', error);
        }
      },
  
      calculateTotalAmount() {
        this.totalAmount = this.transactions.reduce((sum, transaction) => sum + transaction.value, 0);
      },
  
      async deleteTransaction(transactionId) {
        if (confirm('Tem certeza de que deseja excluir esta transação?')) {
          try {
            await api.delete(`/transactions/${transactionId}`);
            this.fetchTransactions();
          } catch (error) {
            console.error('Erro ao excluir transação', error);
          }
        }
      },
  
      async fetchAccountData() {
        try {
          const response = await api.get(`/accounts/find/user/${localStorage.getItem('userId')}`);
          this.account = response.data?._embedded?.accountDtoList[0] || {};
        } catch (error) {
          console.error('Erro ao carregar dados da conta', error);
        }
      },
  
      async saveAccount() {
        try {
          await api.put('/account', this.account);
          alert('Conta salva com sucesso!');
        } catch (error) {
          console.error('Erro ao salvar conta', error);
        }
      },
    },
  };
  </script>
  