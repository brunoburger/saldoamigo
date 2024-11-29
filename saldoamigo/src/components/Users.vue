<template>
  <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700">
    <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-96 lg:w-1/3">
      <h2 class="text-3xl font-semibold text-gray-900 mb-8 text-center">Detalhes do Usuário</h2>

      <div v-if="errorMessage" class="text-red-600 text-lg mb-6 text-center">
        {{ errorMessage }}
      </div>

      <div class="space-y-6">
        <div class="flex flex-col sm:flex-row items-start sm:items-center sm:space-x-4">
          <label class="text-gray-700 font-medium w-full sm:w-32">Nome</label>
          <div class="text-lg text-gray-900">{{ user.username }}</div>
        </div>

        <div class="flex flex-col sm:flex-row items-start sm:items-center sm:space-x-4">
          <label class="text-gray-700 font-medium w-full sm:w-32">Email</label>
          <div class="text-lg text-gray-900">{{ user.email }}</div>
        </div>

        <div class="flex flex-col sm:flex-row items-start sm:items-center sm:space-x-4">
          <label class="text-gray-700 font-medium w-full sm:w-32">Telefone</label>
          <div class="text-lg text-gray-900">{{ user.phone }}</div>
        </div>
      </div>

      <div class="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 mt-8">
        <button 
          @click="editUser" 
          class="w-full sm:w-auto px-6 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-green-500 transition duration-300 ease-in-out transform hover:scale-105">
          Editar Usuário
        </button>
        <button 
          @click="logout" 
          class="w-full sm:w-auto px-6 py-3 bg-teal-600 hover:bg-teal-700 text-white rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-teal-500 transition duration-300 ease-in-out transform hover:scale-105">
          Sair
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'UserCard',
  data() {
    return {
      user: {
        username: '',
        email: '',
        phone: '',
        role: 'USER'
      },
      errorMessage: null
    };
  },
  async created() {
    const userId = localStorage.getItem('userId') || 1;
    await this.fetchUserDetails(userId);
  },
  methods: {
    async fetchUserDetails(userId) {
      try {
        const response = await api.get(`/users/${userId}`);
        this.user = response.data;
      } catch (error) {
        this.errorMessage = 'Erro ao carregar os dados do usuário.';
        console.error(error);
      }
    },
    editUser() {
      this.$router.push(`/edit/user/` + localStorage.getItem('userId'));
    },
    logout() {
      localStorage.clear();
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
@media (max-width: 640px) {
  .text-lg {
    font-size: 1rem;
  }
  .w-32 {
    width: auto;
  }
}
.bg-gradient-to-r {
  background: linear-gradient(90deg, #2F4F4F, #1B3A2B);
}
.bg-white {
  background-color: #ffffff;
}
.text-green-600 {
  color: #16a34a;
}
.text-teal-600 {
  color: #0d9488;
}
.focus:ring-green-500 {
  ring-color: #16a34a;
}
.focus:ring-teal-500 {
  ring-color: #0d9488;
}
.text-red-600 {
  color: #dc2626;
}
</style>
