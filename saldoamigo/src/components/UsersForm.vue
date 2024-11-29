<template>
  <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700">
    <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-96 lg:w-1/3">
      <h1 class="text-3xl font-bold text-gray-900 mb-8 text-center">
        {{ user.id ? 'Editar Usuário' : 'Cadastrar Usuário' }}
      </h1>

      <div v-if="errorMessage" class="mt-4 text-red-600 text-center">
        {{ errorMessage }}
      </div>

      <form @submit.prevent="saveUser" class="space-y-6">
        <div>
          <label class="block text-gray-600 font-medium" for="username">Nome Completo</label>
          <input v-model="user.username" type="text" id="username" required
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" />
        </div>

        <div>
          <label class="block text-gray-600 font-medium" for="email">Email</label>
          <input v-model="user.email" type="email" id="email" required
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" />
        </div>

        <div>
          <label class="block text-gray-600 font-medium" for="phone">Telefone</label>
          <input v-model="user.phone" type="text" id="phone" v-mask="'(##) #####-####'" required
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent"
            placeholder="(00) 00000-0000" />
        </div>
        <div>
          <label class="block text-gray-600 font-medium" for="password">Senha</label>
          <input v-model="user.password" type="password" id="password" :required="!user.id"
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" />
        </div>

        <div v-if="isAdmin()">
          <label class="block text-gray-600 font-medium" for="role">Função</label>
          <select v-model="user.role" id="role" required
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent">
            <option disabled value="">Selecione uma função</option>
            <option value="USER">Usuário</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>

        <div class="mt-6 flex justify-between items-center space-x-4">
          <button @click="goBack" type="button"
            class="w-full bg-gray-500 text-white font-semibold py-3 px-4 rounded-lg hover:bg-gray-600 focus:outline-none transition duration-300 transform hover:scale-105">
            Cancelar
          </button>
          <button type="submit" :disabled="isLoading"
            class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 transform hover:scale-105 focus:ring-2 focus:ring-green-500">
            <span v-if="isLoading">Carregando...</span>
            <span v-else>{{ user.id ? 'Atualizar' : 'Salvar' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'UserForm',
  data() {
    return {
      user: {
        username: '',
        email: '',
        phone: '',
        address: '',
        password: '',
        role: 'USER'
      },
      isLoading: false,
      errorMessage: null
    };
  },
  created() {
    const userId = this.$route.params.id;
    if (userId) {
      this.fetchUserDetails(userId);
    }
  },
  methods: {
    async fetchUserDetails(userId) {
      this.isLoading = true;
      try {
        const response = await api.get(`/users/${userId}`);
        this.user = response.data;
      } catch (error) {
        this.errorMessage = 'Erro ao carregar os dados do usuário.';
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    async saveUser() {
      this.isLoading = true;
      this.errorMessage = null;

      try {
        if (this.user.id) {
          await api.put(`/users`, {id: this.user.id, ...this.user});
        } else {
          await api.post('/auth/register', this.user);
        }
        this.$router.push('/');
      } catch (error) {
        this.errorMessage = 'Erro ao salvar o usuário. Verifique os dados e tente novamente.';
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    goBack() {
      this.$router.push('/');
    },
    isAdmin() {
      return localStorage.getItem('userRole') === 'ROLE_ADMIN';
    }
  }
};
</script>

<style scoped>
label, h1, button {
  font-family: 'Roboto', sans-serif;
  font-weight: bold;
}

p {
  font-family: 'Roboto', sans-serif;
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

.text-green-700 {
  color: #15803d;
}

.focus:ring-green-500 {
  ring-color: #16a34a;
}

.text-red-600 {
  color: #dc2626;
}
</style>

