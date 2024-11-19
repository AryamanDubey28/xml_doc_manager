<template>
    <PrimeDialog
      v-model:visible="isVisible"
      modal
      :header="document?.name || 'Edit Document'"
      :style="{ width: '80vw' }"
      @hide="handleClose"
      maximizable
    >
      <div v-if="editedDocument" class="flex flex-column gap-3">
        <span class="p-float-label">
          <InputText
            id="documentName"
            v-model="editedDocument.name"
            class="w-full"
          />
          <label for="documentName">Document Name</label>
        </span>
  
        <div
          ref="editorContainer"
          class="editor-container"
          style="height: 60vh; border: 1px solid var(--surface-border);"
        ></div>
  
        <div class="flex justify-content-end gap-2">
          <PrimeButton
            label="Format XML"
            icon="pi pi-code"
            text
            @click="formatXML"
          />
          <div class="flex gap-2">
            <PrimeButton
              label="Cancel"
              icon="pi pi-times"
              text
              @click="handleClose"
            />
            <PrimeButton
              label="Save"
              icon="pi pi-save"
              @click="saveDocument"
              :loading="saving"
            />
          </div>
        </div>
      </div>
    </PrimeDialog>
  </template>
  
  <script setup>
  import { ref, watchEffect, onBeforeUnmount, nextTick } from 'vue';
  import loader from '@monaco-editor/loader';
  
  const props = defineProps({
    document: {
      type: Object,
      required: true
    },
    visible: {
      type: Boolean,
      required: true
    }
  });
  
  const emit = defineEmits(['close', 'save']);
  
  const isVisible = ref(props.visible);
  const saving = ref(false);
  const editedDocument = ref({ ...props.document });
  const editorContainer = ref(null);
  let editor = null;
  let monaco = null;
  
  const monacoOptions = {
    automaticLayout: true,
    formatOnPaste: true,
    formatOnType: true,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    wrappingIndent: 'indent',
    fontSize: 14,
    lineNumbers: 'on',
    renderWhitespace: 'selection',
    tabSize: 2,
    scrollbar: {
      vertical: 'visible',
      horizontal: 'visible'
    }
  };
  
  loader.config({
    paths: {
      vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.36.1/min/vs'
    }
  });
  
  const initMonaco = async () => {
    if (!monaco) {
      monaco = await loader.init();
    }
  
    await nextTick();
    
    if (editorContainer.value && !editor) {
      try {
        editor = monaco.editor.create(editorContainer.value, {
          value: editedDocument.value.content || '',
          language: 'xml',
          theme: 'vs-dark',
          ...monacoOptions
        });
  
        editor.onDidChangeModelContent(() => {
          if (editedDocument.value) {
            editedDocument.value.content = editor.getValue();
          }
        });
      } catch (error) {
        console.error('Failed to initialize Monaco editor:', error);
      }
    }
  };
  
  const destroyMonaco = () => {
    if (editor) {
      editor.dispose();
      editor = null;
    }
  };
  
  const handleClose = () => {
    destroyMonaco();
    emit('close');
  };
  
  watchEffect(async () => {
    isVisible.value = props.visible;
    if (isVisible.value) {
      await nextTick();
      await initMonaco();
    }
  });
  
  watchEffect(() => {
    if (props.document) {
      editedDocument.value = { ...props.document };
      if (editor) {
        editor.setValue(editedDocument.value.content || '');
      }
    }
  });
  
  onBeforeUnmount(() => {
    destroyMonaco();
  });
  
  const formatXML = async () => {
    if (editor) {
      try {
        await editor.getAction('editor.action.formatDocument').run();
      } catch (error) {
        console.error('Error formatting XML:', error);
      }
    }
  };
  
  const saveDocument = async () => {
    try {
      saving.value = true;
      const response = await fetch(
        `http://localhost:8080/api/documents/${editedDocument.value.id}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(editedDocument.value)
        }
      );
  
      if (response.ok) {
        emit('save');
        handleClose();
      } else {
        throw new Error('Failed to save document');
      }
    } catch (error) {
      console.error('Error saving document:', error);
    } finally {
      saving.value = false;
    }
  };
  </script>
  
  <style>
  .editor-container {
    border-radius: var(--border-radius);
    overflow: hidden;
    min-height: 60vh;
  }
  
  .editor-container :deep(.monaco-editor) {
    padding-top: 8px;
  }
  </style>




<!-- <template>
    <PrimeDialog 
      v-model:visible="isVisible" 
      modal 
      :header="document?.name || 'Edit Document'" 
      :style="{ width: '80vw' }"
      @hide="$emit('close')"
    >
      <div v-if="editedDocument" class="flex flex-column gap-3">
        <span class="p-float-label">
          <InputText
            id="documentName"
            v-model="editedDocument.name"
            class="w-full"
          />
          <label for="documentName">Document Name</label>
        </span>
  
        <span class="p-float-label">
          <PrimeTextarea
            id="xmlContent"
            v-model="editedDocument.content"
            rows="20"
            class="w-full font-mono"
          />
          <label for="xmlContent">XML Content</label>
        </span>
  
        <div class="flex justify-content-end gap-2">
          <PrimeButton
            label="Cancel"
            icon="pi pi-times"
            text
            @click="$emit('close')"
          />
          <PrimeButton
            label="Save"
            icon="pi pi-save"
            @click="saveDocument"
            :loading="saving"
          />
        </div>
      </div>
    </PrimeDialog>
  </template>
  
  <script setup>
  import { ref, watchEffect } from 'vue'
  
  const props = defineProps({
    document: {
      type: Object,
      required: false,
      default: null
    },
    visible: {
      type: Boolean,
      required: true
    }
  })
  
  const emit = defineEmits(['close', 'save'])
  
  const isVisible = ref(props.visible)
  const saving = ref(false)
  const editedDocument = ref(null)
  
  // Keep the dialog visibility in sync with the prop
  watchEffect(() => {
    isVisible.value = props.visible
  })
  
  // Reset the edited document when the input document changes
  watchEffect(() => {
    if (props.document) {
      editedDocument.value = { ...props.document }
    } else {
      editedDocument.value = null
    }
  })
  
  const saveDocument = async () => {
    if (!editedDocument.value) return
  
    try {
      saving.value = true
      const response = await fetch(`http://localhost:8080/api/documents/${editedDocument.value.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(editedDocument.value)
      })
  
      if (response.ok) {
        emit('save')
        emit('close')
      } else {
        throw new Error('Failed to save document')
      }
    } catch (error) {
      console.error('Error saving document:', error)
    } finally {
      saving.value = false
    }
  }
  </script> -->