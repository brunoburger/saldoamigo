<template>
    <div class="flex justify-center items-center min-h-screen bg-gradient-to-r from-green-700 to-teal-700">
      <div class="bg-white p-8 rounded-xl shadow-2xl w-full sm:w-96 lg:w-1/3">
        <h1 class="text-3xl font-bold text-gray-900 mb-8 text-center">
          {{ group.id ? 'Editar Grupo' : 'Criar Grupo' }}
        </h1>
  
        <div v-if="errorMessage" class="mt-4 text-red-600 text-center">
          {{ errorMessage }}
        </div>
  
        <form @submit.prevent="saveGroup" class="space-y-6">
          <div>
            <label class="block text-gray-600 font-medium" for="groupName">Nome do Grupo</label>
            <input 
              v-model="group.name" 
              type="text" 
              id="groupName" 
              required 
              class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent"
            />
          </div>
  
          <div>
            <label class="block text-gray-600 font-medium" for="description">Descrição</label>
            <textarea 
              v-model="group.description" 
              id="description" 
              rows="4" 
              class="w-full p-4 rounded-lg border border-gray-300 bg-gray-50 text-gray-900 focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="Descreva o propósito do grupo">
            </textarea>
          </div>
  
  
          <div class="mt-6 flex justify-between items-center space-x-4">
            <button @click="goBack" type="button"
              class="w-full bg-gray-500 text-white font-semibold py-3 px-4 rounded-lg hover:bg-gray-600 focus:outline-none transition duration-300 transform hover:scale-105">
              Cancelar
            </button>
            <button type="submit" :disabled="isLoading"
              class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 px-4 rounded-lg transition duration-200 transform hover:scale-105 focus:ring-2 focus:ring-green-500">
              <span v-if="isLoading">Carregando...</span>
              <span v-else>{{ group.id ? 'Atualizar' : 'Criar' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import api from '../api';
  
  export default {
    name: 'GroupForm',
    data() {
      return {
        group: {
          name: '',
          description: '',
          user: { id: localStorage.getItem('userId') }
        },
        isLoading: false,
        errorMessage: null
      };
    },
    created() {
      const groupId = this.$route.params.id;
      if (groupId) {
        this.fetchGroupDetails(groupId);
      }
    },
    methods: {
      async fetchGroupDetails(groupId) {
        this.isLoading = true;
        try {
          const response = await api.get(`/groups/${groupId}`);
          this.group = response.data;
        } catch (error) {
          this.errorMessage = 'Erro ao carregar os dados do grupo.';
          console.error(error);
        } finally {
          this.isLoading = false;
        }
      },
      async saveGroup() {
        this.isLoading = true;
        this.errorMessage = null;
  
        const groupToSave = {
          name: this.group.name,
          description: this.group.description,
          user: { id: localStorage.getItem('userId') },
        };

        try {
          if (this.group.id) {
            await api.put(`/groups`, { id: this.group.id, ...groupToSave});
          } else {
            await api.post('/groups', groupToSave);
          }
          this.$router.push('/');
        } catch (error) {
          this.errorMessage = 'Erro ao salvar o grupo. Verifique os dados e tente novamente.';
          console.error(error);
        } finally {
          this.isLoading = false;
        }
      },
      goBack() {
        this.$router.push('/');
      }
    }
  };
  </script>
  
  <style scoped>
  label, h1, button {
    font-family: 'Roboto', sans-serif;
    font-weight: bold;
  }
  
  .bg-gradient-to-r {
    background: linear-gradient(90deg, #2F4F4F, #1B3A2B);
  }
  
  .text-green-600 {
    color: #16a34a;
  }
  
  .text-red-600 {
    color: #dc2626;
  }
  </style>
  