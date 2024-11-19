<template>
    <v-card>
      <v-card-title class="d-flex justify-space-between align-center">
        Documents
        <v-btn icon @click="$emit('refresh-documents')">
          <v-icon>mdi-refresh</v-icon>
        </v-btn>
      </v-card-title>
  
      <v-card-text>
        <v-list v-if="documents.length > 0">
          <v-list-item v-for="doc in documents" :key="doc.id">
            <div class="d-flex flex-column">
              <v-list-item-title>{{ doc.name }}</v-list-item-title>
              <v-list-item-subtitle>
                Version: {{ doc.version }}
                <br>
                Created: {{ new Date(doc.createdAt).toLocaleString() }}
              </v-list-item-subtitle>
            </div>
            <template v-slot:append>
              <v-btn icon @click="confirmDelete(doc)">
                <v-icon color="error">mdi-delete</v-icon>
              </v-btn>
            </template>
          </v-list-item>
        </v-list>
        <div v-else class="text-center py-4">No documents found</div>
      </v-card-text>
  
      <v-dialog v-model="showDeleteModal" max-width="500px">
        <v-card>
          <v-card-title>Confirm Delete</v-card-title>
          <v-card-text>
            Are you sure you want to delete "{{ documentToDelete?.name }}"? This action cannot be undone.
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn text @click="showDeleteModal = false">Cancel</v-btn>
            <v-btn color="error" text @click="deleteDocument">Delete</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card>
  </template>
  
  <script>
  export default {
    name: 'DocumentsList',
    props: {
      documents: {
        type: Array,
        required: true
      }
    },
    data() {
      return {
        showDeleteModal: false,
        documentToDelete: null
      }
    },
    methods: {
      confirmDelete(document) {
        this.documentToDelete = document
        this.showDeleteModal = true
      },
      deleteDocument() {
        this.$emit('delete-document', this.documentToDelete.id)
        this.showDeleteModal = false
        this.documentToDelete = null
      }
    }
  }
  </script>