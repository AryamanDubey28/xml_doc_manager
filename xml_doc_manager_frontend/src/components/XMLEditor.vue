<template>
    <PrimeCard>
      <template #title>
        <div class="flex align-items-center justify-content-between">
          <h2>XML Editor</h2>
          <div class="flex gap-2">
            <PrimeButton
              icon="pi pi-upload"
              severity="secondary"
              text
              rounded
              @click="triggerFileUpload"
              v-tooltip="'Upload XML File'"
            />
            <PrimeButton
              icon="pi pi-code"
              severity="secondary"
              text
              rounded
              @click="showMarshalDialog = true"
              v-tooltip="'Marshal XML'"
            />
          </div>
        </div>
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
  
    <!-- Marshal Dialog -->
    <PrimeDialog
      v-model:visible="showMarshalDialog"
      modal
      header="Marshal XML"
      :style="{ width: '90vw' }"
      :maximizable="true"
    >
      <div v-if="!marshalledData" class="flex flex-column gap-3">
        <h3>Select Target Class</h3>
        <div class="flex flex-column gap-2">
          <div 
            v-for="cls in availableClasses" 
            :key="cls.name"
            class="p-3 surface-card border-round cursor-pointer hover:surface-hover"
            @click="selectedClass = cls.name"
            :class="{ 'border-primary': selectedClass === cls.name }"
          >
            <div class="text-lg font-bold">{{ cls.name }}</div>
            <div class="text-sm text-color-secondary">{{ cls.description }}</div>
            <!-- <div class="mt-2 text-sm">
              <pre class="p-2 surface-ground border-round">{{ cls.example }}</pre>
            </div> -->
          </div>
        </div>
        
        <div v-if="marshalError" class="p-error mt-2">{{ marshalError }}</div>
        
        <div class="flex justify-content-end mt-3">
          <PrimeButton
            label="Marshal"
            icon="pi pi-code"
            @click="marshalXml"
            :disabled="!selectedClass"
          />
        </div>
      </div>
      
      <div v-else class="flex flex-column gap-3">
        <h3>Marshalled Object</h3>
        <div class="surface-ground p-4 border-round">
          <ObjectViewer :data="marshalledData" />
        </div>
        <div class="flex justify-content-end">
          <PrimeButton
            label="Back"
            icon="pi pi-arrow-left"
            text
            @click="resetMarshal"
          />
        </div>
      </div>
    </PrimeDialog>
  
    <input
      type="file"
      ref="fileInput"
      @change="handleFileUpload"
      accept=".xml"
      class="hidden"
    />
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
  import loader from '@monaco-editor/loader';
  import ObjectViewer from './ObjectViewer.vue';
  
  const documentName = ref('');
  const xmlContent = ref('');
  const fileInput = ref(null);
  const editorContainer = ref(null);
  const showMarshalDialog = ref(false);
  const marshalledData = ref(null);
  const marshalError = ref(null);
  const selectedClass = ref(null);
  let editor = null;
  let monaco = null;
  
  const emit = defineEmits(['document-created']);
  
  const availableClasses = ref([
    {
      name: 'com.example.xmldemo.model.Book',
      description: 'Book with title, author, publisher, and genres',
      example: `<book id="ID">
    <title>TITLE</title>
    <author>AUTHOR</author>
    <publisher>PUBLISHER</publisher>
    <year>YEAR</year>
    <genres>
      <genre>GENRE 1</genre>
      <genre>GENRE 2</genre>
    </genres>
  </book>`
    }
  ]);
  
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
  
  const marshalXml = async () => {
    try {
      marshalError.value = null;
      const response = await fetch(`http://localhost:8080/api/documents/marshal`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          xml: xmlContent.value,
          targetClass: selectedClass.value
        })
      });
  
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to marshal XML');
      }
  
      const result = await response.json();
      marshalledData.value = result;
    } catch (error) {
      marshalError.value = error.message;
      marshalledData.value = null;
    }
  };
  
  const resetMarshal = () => {
    marshalledData.value = null;
    marshalError.value = null;
    selectedClass.value = null;
  };
  </script>
  
  <style scoped>
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
  
  pre {
    margin: 0;
    white-space: pre-wrap;
    word-wrap: break-word;
  }
  </style>