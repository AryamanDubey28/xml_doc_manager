import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import AuthService from '../services/AuthService';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null);
  const loading = ref(true);

  const isAuthenticated = computed(() => !!user.value);

  async function initialize() {
    try {
      loading.value = true;
      user.value = await AuthService.getCurrentUser();
    } catch (error) {
      console.error('Failed to initialize auth:', error);
    } finally {
      loading.value = false;
    }
  }

  async function signOut() {
    try {
      await AuthService.signOut();
      user.value = null;
    } catch (error) {
      console.error('Failed to sign out:', error);
    }
  }

  return {
    user,
    loading,
    isAuthenticated,
    initialize,
    signOut
  };
});