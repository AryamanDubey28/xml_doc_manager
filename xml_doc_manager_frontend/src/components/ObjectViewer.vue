<template>
    <div class="object-viewer">
      <div class="metadata mb-4">
        <div class="text-xl font-bold">{{ data.metadata.className }}</div>
        <div class="text-sm text-color-secondary">{{ data.metadata.packageName }}</div>
      </div>
      
      <div class="property-tree">
        <div v-for="(value, key) in data.object" :key="key" class="property">
          <div 
            class="property-header"
            @click="toggleProperty(key)"
            :class="{ 'expanded': expandedProperties.has(key) }"
          >
            <i class="pi" :class="getIconClass(value)" />
            <span class="property-name">{{ key }}:</span>
            <span class="property-value" v-if="!isComplexValue(value)">{{ value }}</span>
          </div>
          
          <div v-if="isComplexValue(value) && expandedProperties.has(key)" class="property-children">
            <div v-if="Array.isArray(value)" class="array-items">
              <div v-for="(item, index) in value" :key="index" class="array-item">
                <span class="array-index">[{{ index }}]</span>
                <span class="array-value">{{ item }}</span>
              </div>
            </div>
            <div v-else class="object-properties">
              <div v-for="(subValue, subKey) in value" :key="subKey" class="property">
                <span class="property-name">{{ subKey }}:</span>
                <span class="property-value">{{ subValue }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const props = defineProps({
    data: {
      type: Object,
      required: true
    }
  });
  
  const expandedProperties = ref(new Set());
  
  const isComplexValue = (value) => {
    return typeof value === 'object' && value !== null;
  };
  
  const getIconClass = (value) => {
    if (Array.isArray(value)) {
      return 'pi-list';
    }
    if (isComplexValue(value)) {
      return 'pi-folder';
    }
    return 'pi-circle-fill';
  };
  
  const toggleProperty = (key) => {
    if (expandedProperties.value.has(key)) {
      expandedProperties.value.delete(key);
    } else {
      expandedProperties.value.add(key);
    }
  };
  </script>
  
  <style scoped>
  .object-viewer {
    font-family: monospace;
  }
  
  .property {
    margin: 8px 0;
  }
  
  .property-header {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
  }
  
  .property-header:hover {
    background-color: var(--surface-hover);
  }
  
  .property-name {
    color: var(--primary-color);
    font-weight: bold;
  }
  
  .property-value {
    color: var(--text-color-secondary);
  }
  
  .property-children {
    margin-left: 24px;
    margin-top: 4px;
    padding-left: 12px;
    border-left: 2px solid var(--surface-border);
  }
  
  .array-item {
    display: flex;
    gap: 8px;
    padding: 2px 0;
  }
  
  .array-index {
    color: var(--primary-color);
    opacity: 0.7;
  }
  
  .array-value {
    color: var(--text-color-secondary);
  }
  
  .expanded {
    background-color: var(--surface-hover);
  }
  
  .pi {
    font-size: 0.8rem;
  }
  </style>