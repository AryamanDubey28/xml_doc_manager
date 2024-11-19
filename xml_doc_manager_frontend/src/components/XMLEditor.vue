<template>
    <PrimeCard>
      <template #title>
        <h2>Create New Document</h2>
      </template>
      <template #content>
        <div class="flex flex-column gap-3">
          <div class="flex gap-4 align-items-end">
            <span class="p-float-label flex-grow-1">
              <InputText
                id="documentName"
                v-model="documentName"
                class="w-full"
              />
              <label for="documentName">Document Name</label>
            </span>
  
            <div>
              <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept=".xml"
                class="hidden"
              />
              <PrimeButton
                icon="pi pi-upload"
                severity="secondary"
                text
                rounded
                @click="triggerFileUpload"
                v-tooltip="'Upload XML File'"
              />
            </div>
          </div>
  
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
            <PrimeButton
              label="Create Document"
              icon="pi pi-plus"
              @click="createDocument"
              :disabled="!documentName || !xmlContent"
            />
          </div>
        </div>
      </template>
    </PrimeCard>
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
  import loader from '@monaco-editor/loader';
  
  const documentName = ref('');
  const xmlContent = ref('');
  const fileInput = ref(null);
  const editorContainer = ref(null);
  let editor = null;
  let monaco = null;
  
  const emit = defineEmits(['document-created']);
  
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
          value: xmlContent.value,
          language: 'xml',
          theme: 'vs-dark',
          ...monacoOptions
        });
  
        editor.onDidChangeModelContent(() => {
          xmlContent.value = editor.getValue();
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
  
  onMounted(async () => {
    await nextTick();
    await initMonaco();
  });
  
  onBeforeUnmount(() => {
    destroyMonaco();
  });
  
  const triggerFileUpload = () => {
    fileInput.value?.click();
  };
  
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (file && (file.type === 'text/xml' || file.name.endsWith('.xml'))) {
      try {
        const content = await file.text();
        xmlContent.value = content;
        if (editor) {
          editor.setValue(content);
        }
        documentName.value = file.name.replace(/\.xml$/, '');
      } catch (error) {
        console.error('Error reading file:', error);
      }
    }
  };
  
  const formatXML = async () => {
    if (editor) {
      try {
        await editor.getAction('editor.action.formatDocument').run();
      } catch (error) {
        console.error('Error formatting XML:', error);
      }
    }
  };
  
  const createDocument = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/documents', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: documentName.value,
          content: xmlContent.value,
          version: '1.0'
        })
      });
  
      if (response.ok) {
        documentName.value = '';
        xmlContent.value = '';
        if (editor) {
          editor.setValue('');
        }
        emit('document-created');
      }
    } catch (error) {
      console.error('Error creating document:', error);
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
  
  .hidden {
    display: none;
  }
  </style>




<!-- <template>
    <PrimeCard>
      <template #title>
        <h2>Create New Document</h2>
      </template>
      <template #content>
        <div class="flex flex-column gap-3">
          <div class="flex gap-4 align-items-end">
            <span class="p-float-label flex-grow-1">
              <InputText
                id="documentName"
                v-model="documentName"
                class="w-full"
              />
              <label for="documentName">Document Name</label>
            </span>
            
            <div>
              <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept=".xml"
                class="hidden"
              />
              <PrimeButton
                icon="pi pi-upload"
                severity="secondary"
                text
                rounded
                @click="triggerFileUpload"
                v-tooltip="'Upload XML File'"
              />
            </div>
          </div>
  
          <span class="p-float-label">
            <PrimeTextarea
              id="xmlContent"
              v-model="xmlContent"
              rows="24"
              class="w-full font-mono"
              style="min-height: 60vh"
            />
            <label for="xmlContent">XML Content</label>
          </span>
  
          <div class="flex justify-content-end">
            <PrimeButton
              label="Create Document"
              icon="pi pi-plus"
              @click="createDocument"
              :disabled="!documentName || !xmlContent"
            />
          </div>
        </div>
      </template>
    </PrimeCard>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const documentName = ref('')
  const xmlContent = ref('')
  const fileInput = ref(null)
  
  const emit = defineEmits(['document-created'])
  
  const triggerFileUpload = () => {
    fileInput.value?.click()
  }
  
  const handleFileUpload = async (event) => {
    const file = event.target.files[0]
    if (file && (file.type === 'text/xml' || file.name.endsWith('.xml'))) {
      try {
        const content = await file.text()
        xmlContent.value = content
        // Set document name to file name without extension
        documentName.value = file.name.replace(/\.xml$/, '')
      } catch (error) {
        console.error('Error reading file:', error)
      }
    }
  }
  
  const createDocument = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/documents', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: documentName.value,
          content: xmlContent.value,
          version: '1.0'
        })
      })
  
      if (response.ok) {
        documentName.value = ''
        xmlContent.value = ''
        emit('document-created')
      }
    } catch (error) {
      console.error('Error creating document:', error)
    }
  }
  </script> -->