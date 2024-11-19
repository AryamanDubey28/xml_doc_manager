<!-- src/App.vue -->
<template>
  <div class="card">
    <div class="grid">
      <div class="col-8">
        <XMLEditor @document-created="refreshDocuments" />
      </div>

      <div class="col-4">
        <PrimeCard>
          <template #title>
            <div class="flex align-items-center justify-content-between">
              <h2>Documents</h2>
              <PrimeButton
                icon="pi pi-refresh"
                severity="secondary"
                text
                rounded
                @click="refreshDocuments"
                v-tooltip="'Refresh'"
              />
            </div>
          </template>
          <template #content>
            <div v-if="documents.length > 0" class="flex flex-column gap-2">
              <div 
                v-for="doc in documents" 
                :key="doc.id" 
                class="p-3 surface-card border-round cursor-pointer"
                @click="openEditor(doc)"
              >
                <div class="flex align-items-center justify-content-between">
                  <div>
                    <h3 class="m-0 mb-2">{{ doc.name }}</h3>
                    <p class="text-sm m-0 mb-1">{{ doc.name }}.xml</p>
                    <p class="text-sm m-0 text-color-secondary">
                      Created: {{ new Date(doc.createdAt).toLocaleString() }}
                    </p>
                  </div>
                  <PrimeButton
                    icon="pi pi-trash"
                    severity="danger"
                    text
                    rounded
                    @click.stop="confirmDelete(doc)"
                    v-tooltip="'Delete'"
                  />
                </div>
              </div>
            </div>
            <div v-else class="flex align-items-center justify-content-center p-5">
              <p class="text-color-secondary">No documents found</p>
            </div>
          </template>
        </PrimeCard>
      </div>
    </div>

    <!-- Delete Confirmation Dialog -->
    <PrimeDialog
      v-model:visible="showDeleteModal"
      modal
      header="Confirm Delete"
      :style="{ width: '450px' }"
    >
      <p class="m-0">
        Are you sure you want to delete "{{ documentToDelete?.name }}"? This action cannot be undone.
      </p>
      <template #footer>
        <PrimeButton
          label="Cancel"
          icon="pi pi-times"
          text
          @click="showDeleteModal = false"
        />
        <PrimeButton
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteDocument"
        />
      </template>
    </PrimeDialog>

    <!-- Document Editor Dialog -->
    <DocumentEditor
      :document="selectedDocument"
      :visible="showEditor"
      @close="closeEditor"
      @save="refreshDocuments"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import XMLEditor from './components/XMLEditor.vue'
import DocumentEditor from './components/DocumentEditor.vue'

const documents = ref([])
const showDeleteModal = ref(false)
const documentToDelete = ref(null)
const showEditor = ref(false)
const selectedDocument = ref(null)

const refreshDocuments = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/documents')
    documents.value = await response.json()
  } catch (error) {
    console.error('Error fetching documents:', error)
  }
}

const confirmDelete = (document) => {
  documentToDelete.value = document
  showDeleteModal.value = true
}

const deleteDocument = async () => {
  try {
    await fetch(`http://localhost:8080/api/documents/${documentToDelete.value.id}`, {
      method: 'DELETE'
    })
    showDeleteModal.value = false
    documentToDelete.value = null
    refreshDocuments()
  } catch (error) {
    console.error('Error deleting document:', error)
  }
}

const openEditor = (document) => {
  selectedDocument.value = document
  showEditor.value = true
}

const closeEditor = () => {
  showEditor.value = false
  selectedDocument.value = null
}

onMounted(() => {
  refreshDocuments()
})
</script>

<style>
@import 'primevue/resources/themes/lara-dark-purple/theme.css';
@import 'primevue/resources/primevue.min.css';
@import 'primeicons/primeicons.css';
@import 'primeflex/primeflex.css';

.p-card {
  background: var(--surface-card);
  border-radius: var(--border-radius);
}

.font-mono {
  font-family: monospace;
}

.cursor-pointer {
  cursor: pointer;
}
</style>