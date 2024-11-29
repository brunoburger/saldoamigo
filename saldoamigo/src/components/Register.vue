<template>
  <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700">
    <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-96 lg:w-1/3">
      <h1 class="text-3xl font-bold text-gray-900 mb-8 text-center">Cadastro</h1>
      <form @submit.prevent="register">
        <div class="mb-6">
          <label class="block text-gray-600 font-medium" for="username">Nome Completo</label>
          <input 
            v-model="username"
            type="text" 
            id="username" 
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
          />
        </div>
        <div class="mb-6">
          <label class="block text-gray-600 font-medium" for="email">Email</label>
          <input 
            v-model="email"
            type="email" 
            id="email" 
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
          />
        </div>
        <div class="mb-6">
          <label class="block text-gray-600 font-medium" for="phone">Telefone</label>
          <input 
            v-model="phone"
            type="text" 
            id="phone" 
            v-mask="'(##) #####-####'"
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
            placeholder="(00) 00000-0000"
          />
        </div>
        <div class="mb-8">
          <label class="block text-gray-600 font-medium" for="password">Senha</label>
          <input 
            v-model="password"
            type="password" 
            id="password" 
            required 
            class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent" 
          />
        </div>
        <button 
          type="submit" 
          :disabled="isLoading"
          class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 transform hover:scale-105">
          <span v-if="isLoading">Carregando...</span>
          <span v-else>Cadastrar</span>
        </button>
      </form>
      <p v-if="errorMessage" class="text-red-600 text-center mt-4 font-medium">{{ errorMessage }}</p>
      <p class="text-gray-600 text-center mt-6">
        Já tem uma conta? <a href="/login" class="text-green-600 hover:underline font-medium">Faça Login</a>
      </p>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'RegisterForm',
  data() {
    return {
      username: '',
      phone: '',
      email: '',
      password: '',
      role: 'user',
      isLoading: false,
      errorMessage: ''
    };
  },
  methods: {
    async register() {
      if (this.email && this.password) {
        this.isLoading = true;
        this.errorMessage = '';
        try {
          await api.post('/auth/register', {
            username: this.username,
            phone: this.phone,
            email: this.email,
            password: this.password,
            role: this.role
          });

          this.$router.push('/login'); 
        } catch (error) {
          if (error.response && error.response.status === 401) {
            this.errorMessage = 'Credenciais inválidas. Tente novamente.';
          }
          else if (error.response && error.response.status === 403) {
            this.errorMessage = 'Erro de autorização. Tente novamente.';
          }
          else {
            this.errorMessage = error.response?.data || 'Erro de conexão. Verifique sua rede e tente novamente.';
          }
          console.error(error);
        } finally {
          this.isLoading = false;
        }
      } else {
        this.errorMessage = 'Por favor, preencha todos os campos.';
      }
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
