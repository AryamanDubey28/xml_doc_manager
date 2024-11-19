<template>
    <div class="flex align-items-center justify-content-center min-h-screen">
      <PrimeCard class="w-full md:w-6 lg:w-4">
        <template #title>
          <h2 class="text-center">{{ isSignUp ? 'Create Account' : 'Sign In' }}</h2>
        </template>
        <template #content>
          <form @submit.prevent="handleSubmit" class="flex flex-column gap-3">
            <div class="flex flex-column gap-2">
              <label for="username">Username</label>
              <InputText
                id="username"
                v-model="form.username"
                :class="{ 'p-invalid': v$.form.username.$error }"
              />
              <small class="p-error" v-if="v$.form.username.$error">
                {{ v$.form.username.$errors[0].$message }}
              </small>
            </div>
  
            <div v-if="isSignUp" class="flex flex-column gap-2">
              <label for="email">Email</label>
              <InputText
                id="email"
                v-model="form.email"
                type="email"
                :class="{ 'p-invalid': v$.form.email.$error }"
              />
              <small class="p-error" v-if="v$.form.email.$error">
                {{ v$.form.email.$errors[0].$message }}
              </small>
            </div>
  
            <div class="flex flex-column gap-2">
              <label for="password">Password</label>
              <Password
                id="password"
                v-model="form.password"
                :toggleMask="true"
                :class="{ 'p-invalid': v$.form.password.$error }"
              />
              <small class="p-error" v-if="v$.form.password.$error">
                {{ v$.form.password.$errors[0].$message }}
              </small>
            </div>
  
            <div v-if="showConfirmation" class="flex flex-column gap-2">
              <label for="code">Confirmation Code</label>
              <InputText
                id="code"
                v-model="form.confirmationCode"
                :class="{ 'p-invalid': v$.form.confirmationCode.$error }"
              />
              <small class="p-error" v-if="v$.form.confirmationCode.$error">
                {{ v$.form.confirmationCode.$errors[0].$message }}
              </small>
            </div>
  
            <PrimeButton
              :label="buttonLabel"
              type="submit"
              :loading="loading"
              class="mt-3"
            />
  
            <div class="text-center mt-3">
              <a href="#" @click.prevent="toggleMode" class="text-primary">
                {{ isSignUp ? 'Already have an account? Sign in' : 'Create new account' }}
              </a>
            </div>
          </form>
        </template>
      </PrimeCard>
  
      <Toast />
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { useVuelidate } from '@vuelidate/core';
  import { required, email, minLength } from '@vuelidate/validators';
  import { useToast } from 'primevue/usetoast';
  import AuthService from '../services/AuthService';
  
  const router = useRouter();
  const toast = useToast();
  const loading = ref(false);
  const isSignUp = ref(false);
  const showConfirmation = ref(false);
  
  const form = ref({
    username: '',
    email: '',
    password: '',
    confirmationCode: ''
  });
  
  const rules = computed(() => ({
    form: {
      username: { required, minLength: minLength(3) },
      email: isSignUp.value ? { required, email } : {},
      password: { required, minLength: minLength(8) },
      confirmationCode: showConfirmation.value ? { required } : {}
    }
  }));
  
  const v$ = useVuelidate(rules, { form });
  
  const buttonLabel = computed(() => {
    if (showConfirmation.value) return 'Verify Account';
    return isSignUp.value ? 'Create Account' : 'Sign In';
  });
  
  const toggleMode = () => {
    isSignUp.value = !isSignUp.value;
    showConfirmation.value = false;
    form.value = {
      username: '',
      email: '',
      password: '',
      confirmationCode: ''
    };
  };
  
  const handleSubmit = async () => {
    try {
      const isValid = await v$.value.$validate();
      if (!isValid) return;
  
      loading.value = true;
  
      if (showConfirmation.value) {
        await AuthService.confirmSignUp(form.value.username, form.value.confirmationCode);
        showConfirmation.value = false;
        isSignUp.value = false;
        toast.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Account verified successfully',
          life: 3000
        });
      } else if (isSignUp.value) {
        await AuthService.signUp(form.value.username, form.value.password, form.value.email);
        showConfirmation.value = true;
        toast.add({
          severity: 'info',
          summary: 'Verification Required',
          detail: 'Please check your email for the verification code',
          life: 5000
        });
      } else {
        await AuthService.signIn(form.value.username, form.value.password);
        router.push('/');
      }
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message,
        life: 3000
      });
    } finally {
      loading.value = false;
    }
  };
  </script>