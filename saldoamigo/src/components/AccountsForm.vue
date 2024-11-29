<template>
    <div class="min-h-screen bg-gradient-to-r from-green-700 to-teal-700 flex justify-center items-center py-8">
      <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-3/4 lg:w-1/2">
        <h1 class="text-3xl font-bold text-gray-900 mb-6 text-center">Conta</h1>
  
        <!-- Formulário de conta -->
        <form @submit.prevent="saveAccount" class="space-y-4">
          <div>
            <label for="name" class="block font-semibold">Nome do Titular Conta</label>
            <input v-model="account.name" id="name" type="text" class="border p-2 rounded-lg w-full" :placeholder="'Digite o nome completo do titular da conta'" required maxlength="25">
          </div>
  
          <div>
            <label for="pix_key" class="block font-semibold">Chave PIX</label>
            <input v-model="account.pixKey" id="pix_key" type="text" class="border p-2 rounded-lg w-full" :placeholder="'Digite a chave PIX'" required>
          </div>
  
          <div>
            <label for="city" class="block font-semibold">Cidade</label>
            <input v-model="account.city" id="city" type="text" class="border p-2 rounded-lg w-full" :placeholder="'Digite a cidade'" required>
          </div>
  
          <div class="flex justify-end">
            <button type="submit" class="bg-green-600 text-white py-2 px-4 rounded-lg">{{ account.id ? 'Editar Conta' : 'Criar Conta' }}</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import api from '../api';
  
  export default {
    name: 'AccountForm',
    data() {
      return {
        accountId: this.$route.params.id || localStorage.getItem('userId'),
        account: {
          id: localStorage.getItem('userId'),
          name: '',
          pixKey: '',
          city: '',
          user: { id: localStorage.getItem('userId') }
        },
      };
    },
    async created() {
      if (this.accountId) {
        await this.fetchAccountData();
      }
    },
    methods: {
      async fetchAccountData() {
        try {
          const response = await api.get(`/accounts/find/user/${localStorage.getItem('userId')}`);
          const accountData = response.data?._embedded?.accountDtoList[0] || null; 

          if (accountData) {
            this.account = accountData; // Preenche os dados da conta existente
          } else {
            this.accountId = null
          }
        } catch (error) {
          console.error('Erro ao carregar dados da conta', error);
        }
      },
  
      // Função para salvar a conta (criar ou editar)
      async saveAccount() {
        // Limita o nome a 25 caracteres, se necessário
        this.account.name = this.account.name.toUpperCase().slice(0, 25);
        this.account.city = this.account.city.toUpperCase();
  
        // Prepara os dados para enviar ao backend, apenas com os campos necessários.
        const accountToSave = {
          id: localStorage.getItem('userId'),
          name: this.account.name,
          pixKey: this.account.pixKey,
          city: this.account.city,
          user: { id: localStorage.getItem('userId') },
        };
  
        try {
          if (this.accountId) {
            // Atualizar conta existente
            await api.put(`/accounts`, accountToSave);
            alert('Conta editada com sucesso!');
          } else {
            // Criar nova conta
            await api.post('/accounts', accountToSave);
            alert('Conta criada com sucesso!');
          }
          this.$router.push('/account'); // Redireciona para a lista de contas ou onde for necessário
        } catch (error) {
          console.error('Erro ao salvar conta', error);
        }
      },
    },
  };
  </script>
  
  <style scoped>
  h1, button, span {
    font-family: 'Roboto', sans-serif;
  }
  .bg-gradient-to-r {
    background: linear-gradient(90deg, #2F4F4F, #1B3A2B);
  }
  .bg-white {
    background-color: #ffffff;
  }
  .bg-green-600, .hover\:bg-green-700:hover {
    background-color: #16a34a;
  }
  .text-red-600 {
    color: #dc2626;
  }
  .opacity-50 {
    opacity: 0.5;
  }
  </style>
  